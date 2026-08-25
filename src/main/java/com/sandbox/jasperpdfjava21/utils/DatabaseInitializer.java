package com.sandbox.jasperpdfjava21.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {

            Properties properties = new Properties();

            try (InputStream config = getClass()
                    .getClassLoader()
                    .getResourceAsStream("database.properties")) {

                if (config == null) {
                    throw new IllegalStateException(
                            "Required configuration file 'database.properties' was not found in the classpath.");
                }

                properties.load(config);
            }

            String jdbcUrl = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            LOGGER.info("Loading MariaDB JDBC Driver...");
            Class.forName("org.mariadb.jdbc.Driver");
            LOGGER.info("Connecting to database...");

            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {

                LOGGER.info("Database connected successfully.");

                try (InputStream is = getClass()
                        .getClassLoader()
                        .getResourceAsStream("data.sql")) {

                    if (is != null) {

                        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                            StringBuilder sqlBatch = new StringBuilder();
                            String line;

                            while ((line = br.readLine()) != null) {
                                String trimmedLine = line.trim();

                                if (trimmedLine.startsWith("--") || trimmedLine.isEmpty()) {
                                    continue;
                                }

                                sqlBatch.append(line).append('\n');

                                if (trimmedLine.endsWith(";")) {
                                    try (Statement stmt = conn.createStatement()) {
                                        stmt.execute(sqlBatch.toString());
                                    }
                                    sqlBatch.setLength(0);
                                }
                            }

                            LOGGER.info("data.sql executed successfully.");

                        }

                    } else {
                        LOGGER.warning("data.sql not found. Skipping database initialization.");
                    }
                }
            }

        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed.", e);
        }
    }
}
