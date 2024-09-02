package ar.edu.unq.unidad3.dao.impl;

import ar.edu.unq.unidad3.dao.PersonajeDAO;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;

public class HibernatePersonajeDAO extends HibernateDAO<Personaje> implements PersonajeDAO {

    public HibernatePersonajeDAO() {
        super(Personaje.class);
    }

    public Personaje findByIdWithLock(Long id, LockModeType lockModeType) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        return session.find(Personaje.class, id, lockModeType);
    }
}
