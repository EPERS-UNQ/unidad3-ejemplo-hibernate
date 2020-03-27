package ar.edu.unq.unidad3.service

import ar.edu.unq.unidad3.dao.DataDAO
import ar.edu.unq.unidad3.dao.ItemDAO
import ar.edu.unq.unidad3.dao.PersonajeDAO
import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje
import ar.edu.unq.unidad3.service.runner.TransactionRunner.runTrx

class InventarioService(
    private val personajeDAO: PersonajeDAO,
    private val itemDAO: ItemDAO,
    private val dataDAO: DataDAO
) {

    val allItems: Collection<Item>
        get() = runTrx { itemDAO.all }

    val heaviestItem: Item
        get() = runTrx { this.itemDAO.heaviestItem }

    fun guardarItem(item: Item) {
        runTrx { this.itemDAO.guardar(item) }
    }

    fun guardarPersonaje(personaje: Personaje) {
        runTrx { this.personajeDAO.guardar(personaje) }
    }

    fun recuperarPersonaje(personajeId: Long?): Personaje {
        return runTrx { this.personajeDAO.recuperar(personajeId) }
    }

    fun recoger(personajeId: Long?, itemId: Long?) {
        runTrx {
            val personaje = this.personajeDAO.recuperar(personajeId)
            val item = this.itemDAO.recuperar(itemId)
            personaje.recoger(item)
        }
    }

    fun getMasPesdos(peso: Int): Collection<Item> {
        return runTrx { this.itemDAO.getMasPesados(peso) }
    }

    fun getItemsPersonajesDebiles(vida: Int): Collection<Item> {
        return runTrx { this.itemDAO.getItemsDePersonajesDebiles(vida) }
    }


    fun clear() {
        runTrx { this.dataDAO.clear() }
    }


}
