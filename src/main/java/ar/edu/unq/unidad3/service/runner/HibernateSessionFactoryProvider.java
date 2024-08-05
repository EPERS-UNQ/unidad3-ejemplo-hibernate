package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryProvider {

    private static HibernateSessionFactoryProvider INSTANCE;
    private final SessionFactory sessionFactory;

    private HibernateSessionFactoryProvider() {
        String user = "root";
        String password = "root";
        String dataBase = "epers_ejemplo_hibernate";
        String host = "localhost";

        String url = System.getenv().getOrDefault("SQL_URL", "jdbc:mysql://" + host + ":3306/" + dataBase + "?createDatabaseIfNotExist=true&serverTimezone=UTC");
        String dialect = System.getenv().getOrDefault("HIBERNATE_DIALECT", "org.hibernate.dialect.MySQL8Dialect");
        String driver = System.getenv().getOrDefault("SQL_DRIVER", "com.mysql.cj.jdbc.Driver");


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
}
