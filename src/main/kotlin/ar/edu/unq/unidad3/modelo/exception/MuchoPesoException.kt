package ar.edu.unq.unidad3.modelo.exception

import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje

class MuchoPesoException(private val personaje: Personaje, private val item: Item) : RuntimeException() {

    override val message: String?
        get() = "El personaje [$personaje] no puede recoger [$item] porque cagar mucho peso ya"

    companion object {

        private val serialVersionUID = 1L
    }

}
