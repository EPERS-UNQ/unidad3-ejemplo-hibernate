package ar.edu.unq.unidad3.modelo

import javax.persistence.*
import java.util.Objects

@Entity
class Item() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var nombre: String? = null
    var peso: Int = 0

    @ManyToOne
    var owner: Personaje? = null

    constructor(nombre: String, peso: Int):this() {
        this.nombre = nombre
        this.peso = peso
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val item = o as Item?
        return id == item!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return nombre!!
    }
}
