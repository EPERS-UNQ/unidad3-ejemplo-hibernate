package ar.edu.unq.unidad3.modelo

import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
class Personaje() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false, length = 500)
    var nombre: String? = null
    var vida: Int = 0
    var pesoMaximo: Int = 0

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var inventario: MutableSet<Item> = HashSet()

    val pesoActual: Int
        get() = inventario.sumBy { it.peso }

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }

    fun recoger(item: Item) {
        val pesoActual = this.pesoActual
        if (pesoActual + item.peso > this.pesoMaximo) {
            throw MuchoPesoException(this, item)
        }

        this.inventario.add(item)
        item.owner = this
    }

    override fun toString(): String {
        return nombre!!
    }

}
