package dao;

import model.DettaglioOrdine;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DAO per il recupero delle righe di dettaglio di un ordine.
 * Non espone operazioni di scrittura dirette: i dettagli vengono
 * inseriti esclusivamente all'interno della transazione di OrdineDAO.salvaOrdine().
 */
public class DettaglioOrdineDAO {

    /**
     * Recupera tutte le righe di Dettaglio_Ordine associate a un ordine,
     * ordinate per id crescente (ordine di inserimento).
     *
     * @param ordineId id dell'ordine
     * @return Collection di DettaglioOrdine, vuota se l'ordine non ha righe
     */
    public Collection<DettaglioOrdine> doRetrieveByOrdine(int ordineId) throws SQLException {
        String sql = "SELECT id, ordine_id, prodotto_id, nome_prodotto_acquisto, quantita, prezzo_acquisto " +
                     "FROM Dettaglio_Ordine WHERE ordine_id = ? ORDER BY id ASC";
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dettagli.add(mapRow(rs));
                }
            }
        }
        return dettagli;
    }

    /**
     * Recupera una singola riga per chiave primaria.
     *
     * @return DettaglioOrdine o null se non trovato
     */
    public DettaglioOrdine doRetrieveByKey(int dettaglioId) throws SQLException {
        String sql = "SELECT id, ordine_id, prodotto_id, nome_prodotto_acquisto, quantita, prezzo_acquisto " +
                     "FROM Dettaglio_Ordine WHERE id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dettaglioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Helper di mappatura ResultSet → DettaglioOrdine ──────────────────────
    private DettaglioOrdine mapRow(ResultSet rs) throws SQLException {
        DettaglioOrdine d = new DettaglioOrdine();
        d.setId(rs.getInt("id"));
        d.setOrdineId(rs.getInt("ordine_id"));
        d.setProdottoId(rs.getInt("prodotto_id"));
        d.setNomeProdottoAcquisto(rs.getString("nome_prodotto_acquisto"));
        d.setQuantita(rs.getInt("quantita"));
        d.setPrezzoAcquisto(rs.getBigDecimal("prezzo_acquisto"));
        return d;
    }
}
