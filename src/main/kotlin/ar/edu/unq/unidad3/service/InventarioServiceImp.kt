package ar.edu.unq.unidad3.service

import ar.edu.unq.unidad3.dao.DataDAO
import ar.edu.unq.unidad3.dao.ItemDAO
import ar.edu.unq.unidad3.dao.PersonajeDAO
import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner.runTrx

class InventarioServiceImp(
    private val personajeDAO: PersonajeDAO,
    private val itemDAO: ItemDAO,
    private val dataDAO: DataDAO
) : InventarioService {

    override fun allItems(): Collection<Item> {
        return runTrx { itemDAO.all }
    }

    override fun recuperarPaginados(elementosPorPagina : Int , pagina: Int): ItemsPaginados {
        return runTrx {
            if (pagina == null || pagina < 0) {
                throw RuntimeException("El número de página $pagina es menor a 0")
            }
            val items : Collection<Item> = itemDAO.recuperarPaginados(elementosPorPagina, pagina)
            ItemsPaginados(items, itemDAO.contarTodos())
        }
    }

    override fun heaviestItem(): Item {
        return runTrx { itemDAO.heaviestItem }
    }

    override fun guardarItem(item: Item) {
        runTrx { itemDAO.guardar(item) }
    }

    override fun guardarPersonaje(personaje: Personaje) {
        runTrx { personajeDAO.guardar(personaje) }
    }

    override fun recuperarPersonaje(personajeId: Long?): Personaje {
        return runTrx { personajeDAO.recuperar(personajeId) }
    }

    override fun recoger(personajeId: Long?, itemId: Long?) {
        runTrx {
            val personaje = personajeDAO.recuperar(personajeId)
            val item = itemDAO.recuperar(itemId)
            personaje.recoger(item)
            personajeDAO.guardar(personaje)
        }
    }

    override fun getMasPesdos(peso: Int): Collection<Item> {
        return runTrx { itemDAO.getMasPesados(peso) }
    }

    override fun getItemsPersonajesDebiles(vida: Int): Collection<Item> {
        return runTrx { itemDAO.getItemsDePersonajesDebiles(vida) }
    }


    override fun clear() {
        runTrx { dataDAO.clear() }
    }
    class ItemsPaginados(var items: Collection<Item>, var total: Int)
}
