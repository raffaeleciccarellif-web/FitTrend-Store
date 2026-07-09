package dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestore centralizzato delle connessioni al database.
 * Utilizza il DataSource JNDI configurato in META-INF/context.xml.
 * Classe final con costruttore privato (utility class, non istanziabile).
 */
public final class DbManager {

    private static DataSource dataSource;

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup("jdbc/FitTrendDB");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(
                    "Impossibile inizializzare il DataSource JNDI jdbc/FitTrendDB: " + e.getMessage()
            );
        }
    }

    /** Costruttore privato: la classe non deve essere istanziata. */
    private DbManager() {
        throw new UnsupportedOperationException("Classe utility, non istanziabile.");
    }

    /**
     * Restituisce una connessione dal pool JNDI.
     *
     * @return Connection dal DataSource
     * @throws SQLException se il DataSource non riesce a fornire una connessione
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource JNDI jdbc/FitTrendDB non disponibile.");
        }
        return dataSource.getConnection();
    }
}
