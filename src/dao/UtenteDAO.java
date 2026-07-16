package dao;

import model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UtenteDAO {

    public synchronized void doSave(Utente utente, String passwordChiara) throws SQLException {
        String query = "INSERT INTO Utente (nome, cognome, email, password_hash, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, PasswordUtil.sha256(passwordChiara));
            ps.setBoolean(5, utente.isAdmin());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    utente.setId(rs.getInt(1));
                }
            }
        }
    }

    public synchronized Utente doRetrieveByEmail(String email) throws SQLException {
        String query = "SELECT id, nome, cognome, email, password_hash, is_admin FROM Utente WHERE email = ?";
        try (Connection conn = DbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUtenteFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public synchronized Utente doRetrieveByEmailAndPassword(String email, String passwordChiara) throws SQLException {
        String query = "SELECT id, nome, cognome, email, password_hash, is_admin FROM Utente WHERE email = ? AND password_hash = ?";
        try (Connection conn = DbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, PasswordUtil.sha256(passwordChiara));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUtenteFromResultSet(rs);
                }
            }
        }
        return null;
    }


    private Utente extractUtenteFromResultSet(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setAdmin(rs.getBoolean("is_admin"));
        return u;
    }
}
