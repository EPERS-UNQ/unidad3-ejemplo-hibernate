package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;

public class HibernateTransactionRunner {

    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    public static Session getCurrentSession() {
        if (sessionThreadLocal.get() == null) {
            throw new RuntimeException("No hay ninguna session en el contexto");
        }
        return sessionThreadLocal.get();
    }

    public static <T> T runTrx(TransactionBlock<T> bloque) {
        Session session = HibernateSessionFactoryProvider.getInstance().createSession();
        sessionThreadLocal.set(session);
        var tx = session.beginTransaction();
        try {
            T resultado = bloque.execute();
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
            sessionThreadLocal.set(null);
        }
    }

    @FunctionalInterface
    public interface TransactionBlock<T> {
        T execute();
    }
}