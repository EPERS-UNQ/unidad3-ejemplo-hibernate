package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;

import java.sql.Connection;

public class HibernateTransactionRunner {

    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    public static Session getCurrentSession() {
        if (sessionThreadLocal.get() == null) {
            throw new RuntimeException("No hay ninguna session en el contexto");
        }
        return sessionThreadLocal.get();
    }

    public static <T> T runTrx(Integer isolationLevel, TransactionBlock<T> block) {
        Session session = HibernateSessionFactoryProvider.getInstance().createSession();
        sessionThreadLocal.set(session);
        var tx = session.beginTransaction();

        // Seteamos el nivel de isolacion
        if(isolationLevel != null){
            session.doWork(connection -> connection.setTransactionIsolation(isolationLevel));
        }

        try {
            T result = block.execute();
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
            sessionThreadLocal.set(null);
        }
    }

    public static <T> T runTrx(TransactionBlock<T> block) {
        return runTrx(Connection.TRANSACTION_REPEATABLE_READ, block); // Default to REPEATABLE_READ
    }

    @FunctionalInterface
    public interface TransactionBlock<T> {
        T execute();
    }
}
