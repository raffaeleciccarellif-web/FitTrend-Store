package dao;

import model.DettaglioOrdine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// DAO per il recupero delle righe di Dettaglio_Ordine.
// La scrittura è delegata esclusivamente a OrdineDAO.salvaOrdine() dentro la sua transazione.
public class DettaglioOrdineDAO {

    // Recupera tutte le righe di dettaglio di un ordine, ordinate per id crescente
    public Collection<DettaglioOrdine> doRetrieveByOrdine(int ordineId) throws SQLException {
        String sql = "SELECT id, ordine_id, prodotto_id, nome_prodotto_acquisto, quantita, prezzo_acquisto " +
                     "FROM Dettaglio_Ordine WHERE ordine_id = ? ORDER BY id ASC";
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ordineId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) dettagli.add(mapRow(rs));
            }
        }
        return dettagli;
    }

    // Recupera una singola riga di dettaglio per chiave primaria; null se non trovata
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

    // Mappa una riga del ResultSet su un oggetto DettaglioOrdine
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
