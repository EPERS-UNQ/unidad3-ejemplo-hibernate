package ar.edu.unq.unidad3.wop.dao.impl;

import ar.edu.unq.unidad3.wop.dao.PersonajeDAO;
import ar.edu.unq.unidad3.wop.modelo.Personaje;

/**
 * Una implementacion de {@link PersonajeDAO} que persiste
 * en una base de datos relacional utilizando JDBC
 * 
 */
public class HibernatePersonajeDAO extends HibernateDAO<Personaje> implements PersonajeDAO {

	public HibernatePersonajeDAO() {
		super(Personaje.class);
	}
}
