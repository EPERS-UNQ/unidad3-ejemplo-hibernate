package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class HibernateSessionFactoryProvider {

    private static HibernateSessionFactoryProvider INSTANCE;
    private final SessionFactory sessionFactory;

    private HibernateSessionFactoryProvider() {
        String user = "postgres";
        String password = "postgres";
        String dataBase = "epers_ejemplo_hibernate";
        String host = "localhost";
        String port = "5432";

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dataBase);
        String dialect = System.getenv().getOrDefault("HIBERNATE_DIALECT", "org.hibernate.dialect.PostgreSQLDialect");
        String driver = System.getenv().getOrDefault("SQL_DRIVER", "org.postgresql.Driver");

        createDatabaseIfNotExists(dataBase, user, password, host, port);

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.setProperty("hibernate.connection.username", user);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.connection.url", url);
        configuration.setProperty("connection.driver_class", driver);
        configuration.setProperty("dialect", dialect);

        this.sessionFactory = configuration.buildSessionFactory();
    }

    public Session createSession() {
        return this.sessionFactory.openSession();
    }

    public static HibernateSessionFactoryProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HibernateSessionFactoryProvider();
        }
        return INSTANCE;
    }

    public static void destroy() {
        if (INSTANCE != null && INSTANCE.sessionFactory != null) {
            INSTANCE.sessionFactory.close();
        }
        INSTANCE = null;
    }

    private void createDatabaseIfNotExists(String databaseName, String user, String password, String host, String port) {
        String url = String.format("jdbc:postgresql://%s:%s/postgres", host, port);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";
            ResultSet rs = stmt.executeQuery(checkDbQuery);

            if (!rs.next()) {  // Si no hay resultados, la base de datos no existe
                stmt.executeUpdate("CREATE DATABASE " + databaseName);
                System.out.println("Base de datos creada: " + databaseName);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar/crear la base de datos: " + e.getMessage(), e);
        }
    }
}
