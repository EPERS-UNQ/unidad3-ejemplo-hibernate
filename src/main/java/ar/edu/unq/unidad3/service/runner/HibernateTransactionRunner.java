package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;

public class HibernateTransactionRunner {

    public static <T> T runTrx(TransactionBlock<T> bloque) {
        Session session = HibernateSessionFactoryProvider.getInstance().createSession();
        HibernateSessionContext.setCurrentSession(session);
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
            HibernateSessionContext.clearCurrentSession();
        }
    }

    @FunctionalInterface
    public interface TransactionBlock<T> {
        T execute();
    }
}