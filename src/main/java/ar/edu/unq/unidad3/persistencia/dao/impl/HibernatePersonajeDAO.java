package ar.edu.unq.unidad3.persistencia.dao.impl;

import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.persistencia.dao.PersonajeDAO;

public class HibernatePersonajeDAO extends HibernateDAO<Personaje> implements PersonajeDAO {

    public HibernatePersonajeDAO() {
        super(Personaje.class);
    }
}
