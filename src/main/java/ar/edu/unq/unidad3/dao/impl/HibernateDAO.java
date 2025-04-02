package ar.edu.unq.unidad3.dao.impl;

import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.hibernate.Session;

import java.util.List;

public class HibernateDAO<T> {

    private final Class<T> entityType;

    public HibernateDAO(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void guardar(T entity) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        session.saveOrUpdate(entity);
    }

    public T recuperar(Long id) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        return session.get(entityType, id);
    }

    public void eliminar(T entity) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        session.remove(entity);
    }
    public void eliminarTodo() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        session.createQuery("delete from " + entityType.getSimpleName()).executeUpdate();
    }
    
    public List<T> recuperarTodos() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        return session.createQuery("from " + entityType.getSimpleName(), entityType).list();
    }
}