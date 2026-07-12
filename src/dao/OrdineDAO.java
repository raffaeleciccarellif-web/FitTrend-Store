package dao;

import model.Carrello;
import model.DettaglioOrdine;
import model.ItemCarrello;
import model.Ordine;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// DAO per la gestione degli ordini con transazioni esplicite
public class OrdineDAO {

    // Whitelist degli stati ammessi dal DB
    private static final Set<String> STATI_VALIDI = Set.of(
            Ordine.STATO_IN_ELABORAZIONE,
            Ordine.STATO_IN_CONSEGNA,
            Ordine.STATO_CONSEGNATO,
            Ordine.STATO_ANNULLATO
    );

    // Salva un nuovo ordine in modo transazionale.
    // 1. SELECT prodotto FOR UPDATE -> lock riga e verifica stock
    // 2. Ricalcolo totale reale da DB (ignora prezzi dal client)
    // 3. INSERT Ordine con stato in_elaborazione
    // 4. INSERT righe Dettaglio_Ordine con prezzi storici
    // 5. Decremento stock per ogni prodotto
    // 6. Commit; rollback automatico su qualsiasi eccezione
    // Ritorna l'id del nuovo ordine oppure lancia SQLException in caso di errore
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

            // Lock righe prodotto e ricalcolo totale: ignora il prezzo in sessione
            for (ItemCarrello item : items) {
                String sqlLock = "SELECT prezzo, quantita_disponibile FROM Prodotto WHERE id = ? AND is_deleted = 0 FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sqlLock)) {
                    ps.setInt(1, item.getIdProdotto());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            throw new SQLException("Prodotto id=" + item.getIdProdotto() + " non trovato o eliminato.");
                        }
                        BigDecimal prezzoDb = rs.getBigDecimal("prezzo");
                        int stockDb = rs.getInt("quantita_disponibile");
                        if (stockDb < item.getQuantita()) {
                            con.rollback();
                            throw new SQLException("Stock insufficiente per '" + item.getNomeProdotto()
                                    + "'. Disponibili: " + stockDb + ", richiesti: " + item.getQuantita());
                        }
                        totaleRicalcolato = totaleRicalcolato.add(prezzoDb.multiply(new BigDecimal(item.getQuantita())));
                    }
                }
            }

            // INSERT Ordine
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
                ps.setString(7, ordine.getUltimeCifreCarta()); // null se non carta
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

            // INSERT dettagli storici + decremento stock per ogni articolo
            String sqlDettaglio = "INSERT INTO Dettaglio_Ordine " +
                                  "(ordine_id, prodotto_id, nome_prodotto_acquisto, quantita, prezzo_acquisto) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE Prodotto SET quantita_disponibile = quantita_disponibile - ? WHERE id = ?";

            for (ItemCarrello item : items) {
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
            if (con != null) { try { con.rollback(); } catch (SQLException ignored) {} }
            throw e;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {} }
        }
    }

    // Legge il prezzo corrente di un prodotto usando la connessione della transazione aperta
    private BigDecimal getPrezzoAttualeDB(Connection con, int prodottoId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT prezzo FROM Prodotto WHERE id = ?")) {
            ps.setInt(1, prodottoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("prezzo");
                throw new SQLException("Prodotto id=" + prodottoId + " non trovato durante il salvataggio.");
            }
        }
    }

    // Recupera tutti gli ordini di un utente, ordinati per data decrescente (senza dettagli)
    public Collection<Ordine> doRetrieveByUtente(int utenteId) throws SQLException {
        String sql = "SELECT * FROM `Ordine` WHERE utente_id = ? ORDER BY data_ordine DESC";
        List<Ordine> ordini = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ordini.add(mapRow(rs));
            }
        }
        return ordini;
    }

    // Recupera ordini con filtri opzionali (admin). I parametri null vengono ignorati.
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
        // Filtro stato solo se il valore è nella whitelist
        if (stato != null && !stato.isBlank() && STATI_VALIDI.contains(stato)) {
            sql.append(" AND stato = ?");
            params.add(stato);
        }
        sql.append(" ORDER BY data_ordine DESC");

        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ordini.add(mapRow(rs));
            }
        }
        return ordini;
    }

    // Recupera un singolo ordine per id con i dettagli popolati; null se non trovato
    public Ordine doRetrieveByKey(int ordineId) throws SQLException {
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM `Ordine` WHERE id = ?")) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ordine ordine = mapRow(rs);
                    ordine.setDettagli(new ArrayList<>(new DettaglioOrdineDAO().doRetrieveByOrdine(ordineId)));
                    return ordine;
                }
            }
        }
        return null;
    }

    // Aggiorna lo stato di un ordine con whitelist e verifica delle transizioni ammesse.
    // Se il nuovo stato è ANNULLATO, ripristina lo stock nella stessa transazione.
    public void doUpdateStato(int ordineId, String nuovoStato) throws SQLException {
        if (!STATI_VALIDI.contains(nuovoStato)) {
            throw new IllegalArgumentException("Stato non valido: " + nuovoStato);
        }

        Connection con = null;
        try {
            con = DbManager.getConnection();
            con.setAutoCommit(false);

            // Legge e blocca lo stato corrente
            String statoAttuale;
            try (PreparedStatement ps = con.prepareStatement("SELECT stato FROM `Ordine` WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, ordineId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Ordine id=" + ordineId + " non trovato.");
                    }
                    statoAttuale = rs.getString("stato");
                }
            }

            // Verifica la transizione nella mappa ammessa
            Set<String> ammesse = Ordine.TRANSIZIONI_AMMESSE.getOrDefault(statoAttuale, Collections.emptySet());
            if (!ammesse.contains(nuovoStato)) {
                con.rollback();
                throw new IllegalStateException("Transizione non ammessa: " + statoAttuale + " -> " + nuovoStato);
            }

            // Se annullato, ripristina stock nella stessa transazione (una sola volta)
            if (Ordine.STATO_ANNULLATO.equals(nuovoStato)) {
                ripristinaStock(con, ordineId);
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE `Ordine` SET stato = ? WHERE id = ?")) {
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

    // Ripristina lo stock di tutti i prodotti dell'ordine nella transazione aperta
    private void ripristinaStock(Connection con, int ordineId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT prodotto_id, quantita FROM Dettaglio_Ordine WHERE ordine_id = ?")) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try (PreparedStatement upd = con.prepareStatement(
                            "UPDATE Prodotto SET quantita_disponibile = quantita_disponibile + ? WHERE id = ?")) {
                        upd.setInt(1, rs.getInt("quantita"));
                        upd.setInt(2, rs.getInt("prodotto_id"));
                        upd.executeUpdate();
                    }
                }
            }
        }
    }

    // Mappa una riga del ResultSet su un oggetto Ordine
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
