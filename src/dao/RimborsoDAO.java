package dao;

import model.Rimborso;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// DAO per la gestione dei rimborsi
public class RimborsoDAO {

    // Whitelist degli stati ammessi
    private static final Set<String> STATI_VALIDI = Set.of(
            Rimborso.STATO_RICHIESTO,
            Rimborso.STATO_APPROVATO,
            Rimborso.STATO_RIFIUTATO,
            Rimborso.STATO_COMPLETATO
    );

    // Recupera il rimborso associato a un ordine; null se non esiste
    public Rimborso doRetrieveByOrdine(int ordineId) throws SQLException {
        String sql = "SELECT r.*, o.utente_id, u.email AS utente_email " +
                     "FROM Rimborso r " +
                     "JOIN `Ordine` o ON o.id = r.ordine_id " +
                     "JOIN Utente u ON u.id = o.utente_id " +
                     "WHERE r.ordine_id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // Recupera tutti i rimborsi di un utente, ordinati per data richiesta decrescente
    public Collection<Rimborso> doRetrieveByUtente(int utenteId) throws SQLException {
        String sql = "SELECT r.*, o.utente_id, u.email AS utente_email " +
                     "FROM Rimborso r " +
                     "JOIN `Ordine` o ON o.id = r.ordine_id " +
                     "JOIN Utente u ON u.id = o.utente_id " +
                     "WHERE o.utente_id = ? ORDER BY r.data_richiesta DESC";
        List<Rimborso> lista = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // Recupera tutti i rimborsi (admin), filtrati per stato se non null, ordinati per data decrescente
    public Collection<Rimborso> doRetrieveAll(String stato) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, o.utente_id, u.email AS utente_email " +
                "FROM Rimborso r " +
                "JOIN `Ordine` o ON o.id = r.ordine_id " +
                "JOIN Utente u ON u.id = o.utente_id");

        // Filtro stato solo se nella whitelist
        boolean filtraStato = stato != null && !stato.isBlank() && STATI_VALIDI.contains(stato);
        if (filtraStato) sql.append(" WHERE r.stato = ?");
        sql.append(" ORDER BY r.data_richiesta DESC");

        List<Rimborso> lista = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (filtraStato) ps.setString(1, stato);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // Crea una nuova richiesta di rimborso in transazione.
    // 1. Legge l'ordine verificando che appartenga all'utente
    // 2. Verifica che lo stato sia "consegnato" o "annullato"
    // 3. Verifica che non esista già un rimborso per l'ordine (UNIQUE su ordine_id)
    // 4. Usa come importo il totale dell'ordine da DB (non un valore client)
    // 5. Inserisce il rimborso con stato "richiesto"
    // Ritorna l'id del rimborso creato
    public int richiediRimborso(int ordineId, int utenteId, String motivo) throws SQLException {
        Connection con = null;
        try {
            con = DbManager.getConnection();
            con.setAutoCommit(false);

            // Passo 1 & 2: legge ordine e verifica proprietà + stato ammesso
            String sqlOrdine = "SELECT utente_id, stato, totale FROM `Ordine` WHERE id = ? FOR UPDATE";
            java.math.BigDecimal totaleOrdine;
            try (PreparedStatement ps = con.prepareStatement(sqlOrdine)) {
                ps.setInt(1, ordineId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Ordine id=" + ordineId + " non trovato.");
                    }
                    if (rs.getInt("utente_id") != utenteId) {
                        con.rollback();
                        throw new IllegalStateException("L'ordine non appartiene all'utente specificato.");
                    }
                    String statoOrdine = rs.getString("stato");
                    if (!"consegnato".equals(statoOrdine) && !"annullato".equals(statoOrdine)) {
                        con.rollback();
                        throw new IllegalStateException("Il rimborso può essere richiesto solo per ordini consegnati o annullati. Stato attuale: " + statoOrdine);
                    }
                    totaleOrdine = rs.getBigDecimal("totale");
                }
            }

            // Passo 3: verifica assenza rimborso esistente
            String sqlCheck = "SELECT id FROM Rimborso WHERE ordine_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, ordineId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        throw new IllegalStateException("Esiste già un rimborso per l'ordine id=" + ordineId);
                    }
                }
            }

            // Passo 4 & 5: INSERT rimborso con importo da DB e stato "richiesto"
            String sqlInsert = "INSERT INTO Rimborso (ordine_id, importo, motivo, stato) VALUES (?, ?, ?, ?)";
            int idRimborso;
            try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ordineId);
                ps.setBigDecimal(2, totaleOrdine);
                ps.setString(3, motivo);
                ps.setString(4, Rimborso.STATO_RICHIESTO);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Impossibile ottenere l'id del nuovo rimborso.");
                    }
                    idRimborso = rs.getInt(1);
                }
            }

            con.commit();
            return idRimborso;

        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignored) {} }
            throw e;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {} }
        }
    }

    // Aggiorna lo stato di un rimborso con whitelist e transizioni ammesse.
    // Transizioni: richiesto -> approvato | rifiutato; approvato -> completato
    // Imposta data_elaborazione a NOW() quando lo stato esce da "richiesto"
    public void aggiornaStato(int rimborsoId, String nuovoStato) throws SQLException {
        if (!STATI_VALIDI.contains(nuovoStato)) {
            throw new IllegalArgumentException("Stato rimborso non valido: " + nuovoStato);
        }

        Connection con = null;
        try {
            con = DbManager.getConnection();
            con.setAutoCommit(false);

            // Legge stato corrente con lock
            String statoAttuale;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT stato FROM Rimborso WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, rimborsoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        throw new SQLException("Rimborso id=" + rimborsoId + " non trovato.");
                    }
                    statoAttuale = rs.getString("stato");
                }
            }

            // Verifica transizione ammessa
            boolean transazioneValida = switch (statoAttuale) {
                case Rimborso.STATO_RICHIESTO -> nuovoStato.equals(Rimborso.STATO_APPROVATO)
                                             || nuovoStato.equals(Rimborso.STATO_RIFIUTATO);
                case Rimborso.STATO_APPROVATO -> nuovoStato.equals(Rimborso.STATO_COMPLETATO);
                default -> false; // rifiutato e completato sono stati terminali
            };

            if (!transazioneValida) {
                con.rollback();
                throw new IllegalStateException("Transizione non ammessa: " + statoAttuale + " -> " + nuovoStato);
            }

            // data_elaborazione: viene impostata quando lo stato esce da "richiesto"
            boolean impostaData = !Rimborso.STATO_RICHIESTO.equals(nuovoStato);
            String sqlUpdate = impostaData
                    ? "UPDATE Rimborso SET stato = ?, data_elaborazione = NOW() WHERE id = ?"
                    : "UPDATE Rimborso SET stato = ? WHERE id = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, nuovoStato);
                ps.setInt(2, rimborsoId);
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

    // Mappa una riga del ResultSet su un oggetto Rimborso (include campi JOIN utente)
    private Rimborso mapRow(ResultSet rs) throws SQLException {
        Rimborso r = new Rimborso();
        r.setId(rs.getInt("id"));
        r.setOrdineId(rs.getInt("ordine_id"));
        r.setDataRichiesta(rs.getTimestamp("data_richiesta"));
        r.setDataElaborazione(rs.getTimestamp("data_elaborazione")); // null se ancora "richiesto"
        r.setImporto(rs.getBigDecimal("importo"));
        r.setMotivo(rs.getString("motivo"));
        r.setStato(rs.getString("stato"));
        r.setUtenteId(rs.getInt("utente_id"));
        r.setUtenteEmail(rs.getString("utente_email"));
        return r;
    }
}
