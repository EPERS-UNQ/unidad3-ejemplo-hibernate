package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;

import java.io.IOException;

public class HibernateTransactionRunner {

    public static <T> T runTrx(TransactionBlock<T> bloque) {
        Session session = HibernateSessionFactoryProvider.getInstance().createSession();
        HibernateSessionContext.setCurrentSession(session);
        var tx = session.beginTransaction();
        
        try {
            T resultado = bloque.execute();
            tx.commit();
            return resultado;
        } catch (Exception e) {
            tx.rollback();
            throw e instanceof RuntimeException re ? re : new RuntimeException(e);
        } finally {
            closeSession(session);
        }
    }

    private static void closeSession(Session session) {
        session.close();
        HibernateSessionContext.clearCurrentSession();
    }

    @FunctionalInterface
    public interface TransactionBlock<T> {
        T execute() throws IOException;
    }
}