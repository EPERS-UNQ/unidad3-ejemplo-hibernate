package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.dao.PersonajeDAO
import ar.edu.unq.unidad3.modelo.Personaje

/**
 * Una implementacion de [PersonajeDAO] que persiste
 * en una base de datos relacional utilizando JDBC
 *
 */
open class HibernatePersonajeDAO : HibernateDAO<Personaje>(Personaje::class.java), PersonajeDAO
