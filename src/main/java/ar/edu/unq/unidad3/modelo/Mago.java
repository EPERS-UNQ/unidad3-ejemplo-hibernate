package ar.edu.unq.unidad3.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter @Setter @NoArgsConstructor

@Entity
public final class Mago extends Personaje {
    private int magia;

    public Mago(String nombre) {
        this.setNombre(nombre);
    }
}