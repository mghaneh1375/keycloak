package com.company.accessManagement.repository;

import org.flywaydb.core.Flyway;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

import javax.sql.DataSource;
import java.sql.Connection;

public class FlywayMigrator {

    private static volatile boolean migrated = false;
    private static final Object lock = new Object();

    public static void migrate(KeycloakSession session) {
        if (migrated) {
            return;
        }

        synchronized (lock) {
            if (migrated) {
                return;
            }

            System.out.println("=== Starting Flyway migration ===");

            try {
                DataSource dataSource = getDataSourceFromKeycloak(session);
                Flyway flyway;

                if(dataSource ==  null) {
                    System.out.println("datasource is null");

                    Connection connection = session.getProvider(JpaConnectionProvider.class)
                            .getEntityManager()
                            .unwrap(org.hibernate.Session.class)
                            .doReturningWork(conn -> conn);

                    dataSource = createDataSourceFromConnection(connection);
                }

                flyway = Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:db/migration")
                        .table("flyway_schema_history_access")
                        .baselineOnMigrate(true)
                        .outOfOrder(true)
                        .load();

                flyway.migrate();
                migrated = true;
                System.out.println("=== Flyway migration completed successfully ===");
                
            } catch (Exception e) {
                System.err.println("=== Flyway migration failed ===");
                e.printStackTrace();
                throw new RuntimeException("Flyway migration failed", e);
            }
        }
    }

    private static DataSource getDataSourceFromKeycloak(KeycloakSession session) {
        try {
            return session.getProvider(JpaConnectionProvider.class)
                    .getEntityManager()
                    .unwrap(org.hibernate.Session.class)
                    .doReturningWork(connection -> {
                        try {
                            return connection.unwrap(javax.sql.DataSource.class);
                        } catch (Exception e) {
                            return null;
                        }
                    });
        } catch (Exception e) {
            return null;
        }
    }

    private static DataSource createDataSourceFromConnection(Connection connection) {
        try {
            String password = getPasswordFromEnvironment();
            com.zaxxer.hikari.HikariDataSource dataSource = new com.zaxxer.hikari.HikariDataSource();
            dataSource.setJdbcUrl(connection.getMetaData().getURL());
            dataSource.setUsername("keycloak");
            dataSource.setPassword(password);
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            System.out.println("JdbcUrl is " + connection.getMetaData().getURL());
            System.out.println("Password is " + password);
            System.out.println("username is " + connection.getMetaData().getUserName());
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Could not create DataSource", e);
        }
    }

    private static String getPasswordFromEnvironment() {
        String password = System.getenv("KC_DB_PASSWORD");
        if (password != null && !password.isEmpty()) {
            return password;
        }

        password = System.getProperty("KC_DB_PASSWORD");
        if (password != null && !password.isEmpty()) {
            return password;
        }

        return "keycloak";
    }
}