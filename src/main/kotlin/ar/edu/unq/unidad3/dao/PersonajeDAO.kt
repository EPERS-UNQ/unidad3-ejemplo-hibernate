package ar.edu.unq.unidad3.dao

import ar.edu.unq.unidad3.modelo.Personaje

/**
 * Tiene la responsabilidad de guardar y recuperar personajes desde
 * el medio persistente
 */
interface PersonajeDAO {

    fun guardar(personaje: Personaje)

    fun recuperar(id: Long?): Personaje

}
