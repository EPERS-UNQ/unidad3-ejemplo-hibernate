package ar.edu.unq.unidad3.persistencia.dao.impl;

import ar.edu.unq.unidad3.service.runner.HibernateSessionContext;
import org.hibernate.Session;

public class HibernateDAO<T> {

    private final Class<T> entityType;

    public HibernateDAO(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void guardar(T entity) {
        Session session = HibernateSessionContext.getCurrentSession();
        session.save(entity);
    }

    public T recuperar(Long id) {
        Session session = HibernateSessionContext.getCurrentSession();
        return session.get(entityType, id);
    }

    public void eliminar(T entity) {
        Session session = HibernateSessionContext.getCurrentSession();
        session.remove(entity);
    }
    public void eliminarTodo() {
        Session session = HibernateSessionContext.getCurrentSession();
        session.createQuery("delete from " + entityType.getSimpleName()).executeUpdate();
    }
}