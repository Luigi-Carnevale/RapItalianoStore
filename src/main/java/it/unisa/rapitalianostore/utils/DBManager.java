package it.unisa.rapitalianostore.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class DBManager {

    // Nome JNDI dichiarato in web.xml / context.xml
    private static final String JNDI_NAME = "jdbc/RapItaShop";

    private static DataSource ds;
    private static boolean jndiOK = false;

    static {
        // Prova a risolvere la DataSource via JNDI (PROD)
        try {
            Context init = new InitialContext();
            Context env  = (Context) init.lookup("java:/comp/env");
            ds = (DataSource) env.lookup(JNDI_NAME);
            jndiOK = (ds != null);
        } catch (NamingException ignore) {
            jndiOK = false; // useremo il fallback DEV
            System.err.println("[DB] JNDI '" + JNDI_NAME + "' non trovato: uso fallback DEV (DriverManager).");
        }

        // Assicura il driver MySQL (utile per fallback e alcuni container)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL non trovato nel classpath", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (jndiOK) {
            return ds.getConnection();
        }

        // ==== Fallback DEV (NON mettere credenziali hardcoded nel repo) ====
        // Legge da variabili d'ambiente (o usa default locali ragionevoli)
        String url  = Optional.ofNullable(System.getenv("DB_URL"))
                .orElse("jdbc:mysql://localhost:3306/RapItaShop?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false");
        String user = Optional.ofNullable(System.getenv("DB_USER")).orElse("root");
        String pass = Optional.ofNullable(System.getenv("DB_PASS")).orElse("a");

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pass);
        // Props utili per socket timeout ecc. (facoltative):
        // props.setProperty("socketTimeout", "10000");

        return DriverManager.getConnection(url, props);
    }
}
