package ar.edu.unq.unidad3.dao.impl;

import ar.edu.unq.unidad3.dao.PersonajeDAO;
import ar.edu.unq.unidad3.modelo.Personaje;

public class HibernatePersonajeDAO extends HibernateDAO<Personaje> implements PersonajeDAO {

    public HibernatePersonajeDAO() {
        super(Personaje.class);
    }
}