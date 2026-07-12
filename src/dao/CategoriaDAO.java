package dao;

import model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CategoriaDAO {

    /**
     * Recupera tutte le categorie ordinate per nome.
     */
    public Collection<Categoria> doRetrieveAll() throws SQLException {
        String sql = "SELECT id, nome, descrizione FROM categoria ORDER BY nome ASC";
        Collection<Categoria> categorie = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorie.add(mapRow(rs));
            }
        }
        return categorie;
    }

    /**
     * Recupera una categoria per chiave primaria.
     *
     * @param id identificativo della categoria
     * @return la Categoria trovata, o null se non esiste
     */
    public Categoria doRetrieveByKey(int id) throws SQLException {
        String sql = "SELECT id, nome, descrizione FROM categoria WHERE id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Controlla se esiste una categoria con l'id specificato.
     *
     * @param id identificativo da verificare
     * @return true se esiste, false altrimenti
     */
    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM categoria WHERE id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Categoria mapRow(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setDescrizione(rs.getString("descrizione"));
        return c;
    }
}
