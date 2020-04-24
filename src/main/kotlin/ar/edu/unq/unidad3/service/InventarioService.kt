package ar.edu.unq.unidad3.service

import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje

interface InventarioService {
    fun allItems(): Collection<Item>
    fun heaviestItem(): Item
    fun guardarItem(item: Item)
    fun guardarPersonaje(personaje: Personaje)
    fun recuperarPersonaje(personajeId: Long?): Personaje
    fun recoger(personajeId: Long?, itemId: Long?)
    fun getMasPesdos(peso: Int): Collection<Item>
    fun getItemsPersonajesDebiles(vida: Int): Collection<Item>
    fun clear()
}