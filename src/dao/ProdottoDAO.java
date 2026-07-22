package dao;

import model.Prodotto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ProdottoDAO {

    //Whitelist ordinamento
    private static final String DEFAULT_ORDER = "p.nome";

    private String safeOrder(String order) {
        if (order == null) return DEFAULT_ORDER;
        return switch (order.toLowerCase()) {
            case "nome"      -> "p.nome";
            case "prezzo"    -> "p.prezzo";
            case "categoria" -> "c.nome";
            default          -> DEFAULT_ORDER;
        };
    }

    // Query base con JOIN
    private static final String SELECT_BASE =
            "SELECT p.id, p.nome, p.descrizione, p.prezzo, " +
            "       p.categoria_id, c.nome AS categoria_nome, " +
            "       p.immagine, p.quantita_disponibile, p.is_deleted " +
            "FROM prodotto p " +
            "JOIN categoria c ON c.id = p.categoria_id ";

    // Metodi pubblici

    public Collection<Prodotto> doRetrieveByFilters(String nome, Integer categoriaId,
                                                    BigDecimal prezzoMin, BigDecimal prezzoMax,
                                                    String order) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE p.is_deleted = 0 ");

        if (nome != null && !nome.isBlank())  sql.append("AND p.nome LIKE ? ");
        if (categoriaId != null)              sql.append("AND p.categoria_id = ? ");
        if (prezzoMin != null)                sql.append("AND p.prezzo >= ? ");
        if (prezzoMax != null)                sql.append("AND p.prezzo <= ? ");

        sql.append("ORDER BY ").append(safeOrder(order));

        Collection<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nome != null && !nome.isBlank())  ps.setString(idx++, "%" + nome + "%");
            if (categoriaId != null)              ps.setInt(idx++, categoriaId);
            if (prezzoMin != null)                ps.setBigDecimal(idx++, prezzoMin);
            if (prezzoMax != null)                ps.setBigDecimal(idx++, prezzoMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(mapRow(rs));
                }
            }
        }
        return prodotti;
    }

    public Collection<Prodotto> doRetrieveByFilters(String nome, Integer categoriaId,
                                                    BigDecimal prezzoMin, BigDecimal prezzoMax,
                                                    String order, int offset, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE p.is_deleted = 0 ");

        if (nome != null && !nome.isBlank())  sql.append("AND p.nome LIKE ? ");
        if (categoriaId != null)              sql.append("AND p.categoria_id = ? ");
        if (prezzoMin != null)                sql.append("AND p.prezzo >= ? ");
        if (prezzoMax != null)                sql.append("AND p.prezzo <= ? ");

        sql.append("ORDER BY ").append(safeOrder(order));
        sql.append(" LIMIT ? OFFSET ?");

        Collection<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nome != null && !nome.isBlank())  ps.setString(idx++, "%" + nome + "%");
            if (categoriaId != null)              ps.setInt(idx++, categoriaId);
            if (prezzoMin != null)                ps.setBigDecimal(idx++, prezzoMin);
            if (prezzoMax != null)                ps.setBigDecimal(idx++, prezzoMax);
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(mapRow(rs));
                }
            }
        }
        return prodotti;
    }

    public int countByFilters(String nome, Integer categoriaId,
                              BigDecimal prezzoMin, BigDecimal prezzoMax) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM prodotto p ");
        sql.append("WHERE p.is_deleted = 0 ");

        if (nome != null && !nome.isBlank())  sql.append("AND p.nome LIKE ? ");
        if (categoriaId != null)              sql.append("AND p.categoria_id = ? ");
        if (prezzoMin != null)                sql.append("AND p.prezzo >= ? ");
        if (prezzoMax != null)                sql.append("AND p.prezzo <= ? ");

        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nome != null && !nome.isBlank())  ps.setString(idx++, "%" + nome + "%");
            if (categoriaId != null)              ps.setInt(idx++, categoriaId);
            if (prezzoMin != null)                ps.setBigDecimal(idx++, prezzoMin);
            if (prezzoMax != null)                ps.setBigDecimal(idx++, prezzoMax);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Prodotto doRetrieveByKey(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id = ?";
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

    public Collection<Prodotto> doRetrieveAllForAdmin() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY p.id DESC";
        Collection<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prodotti.add(mapRow(rs));
            }
        }
        return prodotti;
    }

    public Collection<Prodotto> doRetrieveAllForAdmin(int offset, int limit) throws SQLException {
        String sql = SELECT_BASE + "ORDER BY p.id DESC LIMIT ? OFFSET ?";
        Collection<Prodotto> prodotti = new ArrayList<>();
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(mapRow(rs));
                }
            }
        }
        return prodotti;
    }

    public int countAllForAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prodotto p";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void doSave(Prodotto p) throws SQLException {
        String sql = "INSERT INTO prodotto (nome, descrizione, prezzo, categoria_id, immagine, quantita_disponibile, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setBigDecimal(3, p.getPrezzo());
            ps.setInt(4, p.getCategoriaId());
            ps.setString(5, p.getImmagine());
            ps.setInt(6, p.getQuantitaDisponibile());
            ps.executeUpdate();
        }
    }

    public void doUpdate(Prodotto p) throws SQLException {
        String sql = "UPDATE prodotto SET nome=?, descrizione=?, prezzo=?, categoria_id=?, " +
                     "immagine=?, quantita_disponibile=? WHERE id=?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setBigDecimal(3, p.getPrezzo());
            ps.setInt(4, p.getCategoriaId());
            ps.setString(5, p.getImmagine());
            ps.setInt(6, p.getQuantitaDisponibile());
            ps.setInt(7, p.getId());
            ps.executeUpdate();
        }
    }

    public void doDelete(int id) throws SQLException {
        String sql = "UPDATE prodotto SET is_deleted = 1 WHERE id = ?";
        try (Connection con = DbManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Helper

    private Prodotto mapRow(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescrizione(rs.getString("descrizione"));
        p.setPrezzo(rs.getBigDecimal("prezzo"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        p.setCategoriaNome(rs.getString("categoria_nome"));
        p.setImmagine(rs.getString("immagine"));
        p.setQuantitaDisponibile(rs.getInt("quantita_disponibile"));
        p.setDeleted(rs.getBoolean("is_deleted"));
        return p;
    }
}
