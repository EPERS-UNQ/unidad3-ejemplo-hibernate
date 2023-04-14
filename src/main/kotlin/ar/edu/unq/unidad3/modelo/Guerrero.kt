package ar.edu.unq.unidad3.modelo

import javax.persistence.Entity

@Entity
class Guerrero() : Personaje() {
     var fuerza: Int = 0

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }


}
