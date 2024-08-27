package ar.edu.unq.unidad3.dao.impl;

import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.hibernate.Session;

public class HibernateDAO<T> {

    private final Class<T> entityType;

    public HibernateDAO(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void guardar(T entity) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        session.save(entity);
    }

    public T recuperar(Long id) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        return session.get(entityType, id);
    }
    public void eliminarTodo() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        session.createQuery("delete from " + entityType.getSimpleName()).executeUpdate();
    }
}