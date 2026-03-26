package com.irctc.dao;

import java.sql.Connection;
import java.sql.DriverManager; // Import DriverManager
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {
    private static HikariDataSource dataSource;
    static {
        InputStream input = null;
        try {
            input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("DBConnection Error: config.properties NOT FOUND in classpath!");
            }
            Properties props = new Properties();
            props.load(input);

            // --- THE FIX: Explicitly load the MySQL driver ---
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("--- DBConnection: MySQL Driver loaded successfully. ---");
            } catch (ClassNotFoundException e) {
                System.err.println("--- DBConnection FATAL ERROR: MySQL JDBC Driver not found in classpath! ---");
                throw new RuntimeException("MySQL JDBC Driver not found.", e);
            }
            // --- End of Fix ---

            HikariConfig config = new HikariConfig();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            if (url == null || user == null || pass == null) {
                throw new RuntimeException("DBConnection Error: db.url, db.username, or db.password missing in config.properties!");
            }

            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(10);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config); // Attempt connection
            System.out.println("--- DBConnection: HikariDataSource initialized successfully! ---");

        } catch (Exception e) {
            System.err.println("--- DBConnection FATAL ERROR during initialization: ---");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool.", e);
        } finally {
            if (input != null) {
                try { input.close(); } catch (java.io.IOException ioex) { /* ignore */ }
            }
        }
    }
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
             throw new SQLException("DBConnection Error: HikariDataSource is null. Initialization failed.");
        }
        return dataSource.getConnection();
    }
}