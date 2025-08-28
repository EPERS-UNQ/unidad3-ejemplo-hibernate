package ar.edu.unq.unidad3.service.runner;

import org.hibernate.Session;

public class HibernateSessionContext {
    
    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    public static void setCurrentSession(Session session) {
        sessionThreadLocal.set(session);
    }

    public static Session getCurrentSession() {
        Session session = sessionThreadLocal.get();
        if (session == null) {
            throw new RuntimeException("No hay ninguna session en el contexto");
        }
        return session;
    }

    public static void clearCurrentSession() {
        sessionThreadLocal.set(null);
    }

}
