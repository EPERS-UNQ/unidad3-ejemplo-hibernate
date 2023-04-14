package ar.edu.unq.unidad3.modelo

import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
class Mago() : Personaje() {
    var magia: Int = 0

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }
}
