package dao;

import model.Carrello;
import model.DettaglioOrdine;
import model.ItemCarrello;
import model.Ordine;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DAO per la gestione degli ordini.
 * Tutte le operazioni sensibili (salva, annulla) sono protette da transazione esplicita.
 */
public class OrdineDAO {

    // ── Whitelist stati ammessi ───────────────────────────────────────────────
    private static final java.util.Set<String> STATI_VALIDI = java.util.Set.of(
            Ordine.STATO_IN_ELABORAZIONE,
            Ordine.STATO_IN_CONSEGNA,
            Ordine.STATO_CONSEGNATO,
            Ordine.STATO_ANNULLATO
    );

    // ────────────────────────────────────────────────────────────────────────
    // salvaOrdine
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Salva un nuovo ordine in modo transazionale:
     * 1. SELECT … FOR UPDATE su ogni prodotto → lock riga
     * 2. Ricalcolo del totale reale (non ci si fida del prezzo in sessione)
     * 3. INSERT in Ordine con stato = in_elaborazione
     * 4. INSERT righe Dettaglio_Ordine con prezzi storici
     * 5. Decremento stock per ogni prodotto
     * 6. Commit; in caso di errore → Rollback
     *
     * @param ordine  Bean pre-popolato con indirizzo, pagamento, utente (NO id, NO totale, NO stato)
     * @param carrello Carrello in sessione con gli articoli
     * @return id dell'ordine appena creato, -1 in caso di errore
     * @throws SQLException se si verifica un errore DB non recuperabile
     */
    public int salvaOrdine(Ordine ordine, Carrello carrello) throws SQLException {
        if (carrello == null || carrello.isEmpty()) {
            throw new IllegalArgumentException("Impossibile creare un ordine con il carrello vuoto.");
        }

        Connection con = null;
        try {
            con = DbManager.getConnection();
            con.setAutoCommit(false);

            BigDecimal totaleRicalcolato = BigDecimal.ZERO;
            Collection<ItemCarrello> items = carrello.getItems();

            // ── STEP 1 & 2: lock righe prodotto e ricalcolo totale ──────────
            for (ItemCarrello item : items) {
                String sqlLock = "SELECT prezzo, quantita_disponibile FROM Prodotto WHERE id = ? AND is_deleted = 0 FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sqlLock)) {
                    ps.setInt(1, item.getIdProdotto());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            throw new SQLException("Prodotto id=" + item.getIdProdotto() + " non trovato o eliminato. Ordine annullato.");
                        }
                        BigDecimal prezzoDb = rs.getBigDecimal("prezzo");
                        int stockDb = rs.getInt("quantita_disponibile");
                        if (stockDb < item.getQuantita()) {
                            con.rollback();
                            throw new SQLException("Stock insufficiente per '" + item.getNomeProdotto() + "'. Disponibili: " + stockDb + ", richiesti: " + item.getQuantita());
                        }
                        // Ricalcolo con prezzo reale da DB
                        totaleRicalcolato = totaleRicalcolato.add(prezzoDb.multiply(new BigDecimal(item.getQuantita())));
                    }
                }
            }

            // ── STEP 3: INSERT Ordine ────────────────────────────────────────
            String sqlOrdine = "INSERT INTO `Ordine` (utente_id, totale, indirizzo_spedizione, " +
                               "citta_spedizione, cap_spedizione, metodo_pagamento, ultime_cifre_carta, stato) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int idOrdine;
            try (PreparedStatement ps = con.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ordine.getUtenteId());
                ps.setBigDecimal(2, totaleRicalcolato);
                ps.setString(3, ordine.getIndirizzoSpedizione());
                ps.setString(4, ordine.getCittaSpedizione());
                ps.setString(5, ordine.getCapSpedizione());
                ps.setString(6, ordine.getMetodoPagamento());
                ps.setString(7, ordine.getUltimeCifreCarta()); // può essere null
                ps.setString(8, Ordine.STATO_IN_ELABORAZIONE);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Impossibile ottenere l'id del nuovo ordine.");
                    }
                    idOrdine = rs.getInt(1);
                }
            }

            // ── STEP 4 & 5: INSERT dettagli + decremento stock ───────────────
            String sqlDettaglio = "INSERT INTO Dettaglio_Ordine " +
                                  "(ordine_id, prodotto_id, nome_prodotto_acquisto, quantita, prezzo_acquisto) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE Prodotto SET quantita_disponibile = quantita_disponibile - ? WHERE id = ?";

            for (ItemCarrello item : items) {
                // Prezzo storico: rileggere da DB con prepared statement separato
                BigDecimal prezzoStorico = getPrezzoAttualeDB(con, item.getIdProdotto());

                try (PreparedStatement ps = con.prepareStatement(sqlDettaglio)) {
                    ps.setInt(1, idOrdine);
                    ps.setInt(2, item.getIdProdotto());
                    ps.setString(3, item.getNomeProdotto());
                    ps.setInt(4, item.getQuantita());
                    ps.setBigDecimal(5, prezzoStorico);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                    ps.setInt(1, item.getQuantita());
                    ps.setInt(2, item.getIdProdotto());
                    ps.executeUpdate();
                }
            }

            con.commit();
            ordine.setId(idOrdine);
            ordine.setTotale(totaleRicalcolato);
            ordine.setStato(Ordine.STATO_IN_ELABORAZIONE);
            return idOrdine;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ── Helper privato: legge il prezzo corrente di un prodotto nella transazione aperta ──
    private BigDecimal getPrezzoAttualeDB(Connection con, int prodottoId) throws SQLException {
        String sql = "SELECT prezzo FROM Prodotto WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prodottoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("prezzo");
                throw new SQLException("Prodotto id=" + prodottoId + " non trovato durante il salvataggio del dettaglio.");
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // doRetrieveByUtente
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Recupera tutti gli ordini di un utente, ordinati per data decrescente.
     * I dettagli NON sono caricati (lista lazy).
     */
    public Collection<Ordine> doRetrieveByUtente(int utenteId) throws SQLException {
        String sql = "SELECT * FROM `Ordine` WHERE utente_id = ? ORDER BY data_ordine DESC";
        List<Ordine> ordini = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(mapRow(rs));
                }
            }
        }
        return ordini;
    }

    // ────────────────────────────────────────────────────────────────────────
    // doRetrieveByFilters  (admin panel)
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Recupera ordini con filtri opzionali (per admin).
     * I parametri null vengono ignorati (non aggiungono la clausola WHERE corrispondente).
     *
     * @param dataInizio  stringa "YYYY-MM-DD" o null
     * @param dataFine    stringa "YYYY-MM-DD" o null
     * @param utenteId    id utente o null
     * @param stato       uno dei valori costante Ordine.STATO_* o null
     */
    public Collection<Ordine> doRetrieveByFilters(String dataInizio, String dataFine,
                                                   Integer utenteId, String stato) throws SQLException {
        List<Ordine> ordini = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM `Ordine` WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (dataInizio != null && !dataInizio.isBlank()) {
            sql.append(" AND data_ordine >= ?");
            params.add(dataInizio + " 00:00:00");
        }
        if (dataFine != null && !dataFine.isBlank()) {
            sql.append(" AND data_ordine <= ?");
            params.add(dataFine + " 23:59:59");
        }
        if (utenteId != null) {
            sql.append(" AND utente_id = ?");
            params.add(utenteId);
        }
        if (stato != null && !stato.isBlank() && STATI_VALIDI.contains(stato)) {
            sql.append(" AND stato = ?");
            params.add(stato);
        }
        sql.append(" ORDER BY data_ordine DESC");

        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(mapRow(rs));
                }
            }
        }
        return ordini;
    }

    // ────────────────────────────────────────────────────────────────────────
    // doRetrieveByKey
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Recupera un singolo ordine per id, comprensivo dei dettagli.
     *
     * @return Ordine con lista dettagli popolata, o null se non trovato
     */
    public Ordine doRetrieveByKey(int ordineId) throws SQLException {
        String sql = "SELECT * FROM `Ordine` WHERE id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ordine ordine = mapRow(rs);
                    DettaglioOrdineDAO dettaglioDAO = new DettaglioOrdineDAO();
                    ordine.setDettagli(new ArrayList<>(dettaglioDAO.doRetrieveByOrdine(ordineId)));
                    return ordine;
                }
            }
        }
        return null;
    }

    // ────────────────────────────────────────────────────────────────────────
    // doUpdateStato
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Aggiorna lo stato di un ordine.
     * Applica:
     * - Whitelist degli stati validi
     * - Verifica che la transizione sia ammessa (da Ordine.TRANSIZIONI_AMMESSE)
     * - Se il nuovo stato è ANNULLATO → ripristino stock nella stessa transazione
     *
     * @throws IllegalStateException se la transizione non è ammessa
     * @throws IllegalArgumentException se lo stato non è valido
     */
    public void doUpdateStato(int ordineId, String nuovoStato) throws SQLException {
        // Whitelist
        if (!STATI_VALIDI.contains(nuovoStato)) {
            throw new IllegalArgumentException("Stato non valido: " + nuovoStato);
        }

        Connection con = null;
        try {
            con = DbManager.getConnection();
            con.setAutoCommit(false);

            // Legge stato corrente con lock
            String sqlCurrent = "SELECT stato FROM `Ordine` WHERE id = ? FOR UPDATE";
            String statoAttuale;
            try (PreparedStatement ps = con.prepareStatement(sqlCurrent)) {
                ps.setInt(1, ordineId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Ordine id=" + ordineId + " non trovato.");
                    }
                    statoAttuale = rs.getString("stato");
                }
            }

            // Verifica transizione
            java.util.Set<String> transazioniAmmesse = Ordine.TRANSIZIONI_AMMESSE.getOrDefault(statoAttuale, java.util.Collections.emptySet());
            if (!transazioniAmmesse.contains(nuovoStato)) {
                con.rollback();
                throw new IllegalStateException("Transizione non ammessa: " + statoAttuale + " → " + nuovoStato);
            }

            // Se annullato → ripristino stock (una sola volta)
            if (Ordine.STATO_ANNULLATO.equals(nuovoStato)) {
                ripristinaStock(con, ordineId);
            }

            // UPDATE stato
            String sqlUpdate = "UPDATE `Ordine` SET stato = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, nuovoStato);
                ps.setInt(2, ordineId);
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignored) {} }
            throw e;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {} }
        }
    }

    /**
     * Ripristina lo stock per tutti i prodotti dell'ordine.
     * Chiamato nella stessa transazione di doUpdateStato quando il nuovo stato è ANNULLATO.
     */
    private void ripristinaStock(Connection con, int ordineId) throws SQLException {
        String sqlDettagli = "SELECT prodotto_id, quantita FROM Dettaglio_Ordine WHERE ordine_id = ?";
        String sqlUpdate   = "UPDATE Prodotto SET quantita_disponibile = quantita_disponibile + ? WHERE id = ?";
        try (PreparedStatement psDettagli = con.prepareStatement(sqlDettagli)) {
            psDettagli.setInt(1, ordineId);
            try (ResultSet rs = psDettagli.executeQuery()) {
                while (rs.next()) {
                    int prodId = rs.getInt("prodotto_id");
                    int qta    = rs.getInt("quantita");
                    try (PreparedStatement psStock = con.prepareStatement(sqlUpdate)) {
                        psStock.setInt(1, qta);
                        psStock.setInt(2, prodId);
                        psStock.executeUpdate();
                    }
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helper di mappatura ResultSet → Ordine
    // ────────────────────────────────────────────────────────────────────────
    private Ordine mapRow(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getInt("id"));
        o.setUtenteId(rs.getInt("utente_id"));
        o.setDataOrdine(rs.getTimestamp("data_ordine"));
        o.setTotale(rs.getBigDecimal("totale"));
        o.setIndirizzoSpedizione(rs.getString("indirizzo_spedizione"));
        o.setCittaSpedizione(rs.getString("citta_spedizione"));
        o.setCapSpedizione(rs.getString("cap_spedizione"));
        o.setMetodoPagamento(rs.getString("metodo_pagamento"));
        o.setUltimeCifreCarta(rs.getString("ultime_cifre_carta"));
        o.setStato(rs.getString("stato"));
        return o;
    }
}
