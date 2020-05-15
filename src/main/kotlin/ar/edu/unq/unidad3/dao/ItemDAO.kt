package ar.edu.unq.unidad3.dao

import ar.edu.unq.unidad3.modelo.Item

/**
 * Tiene la responsabilidad de guardar y recuperar items desde
 * el medio persistente
 */
interface ItemDAO {

    val all: Collection<Item>

    val heaviestItem: Item

    fun guardar(item: Item)

    fun recuperar(id: Long?): Item

    fun getMasPesados(peso: Int): Collection<Item>

    fun getItemsDePersonajesDebiles(unaVida: Int): Collection<Item>

}
