package dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DbManager {

    private static final DataSource dataSource;

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

    private DbManager() {
        throw new UnsupportedOperationException("Classe utility, non istanziabile.");
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource JNDI jdbc/FitTrendDB non disponibile.");
        }
        return dataSource.getConnection();
    }
}
