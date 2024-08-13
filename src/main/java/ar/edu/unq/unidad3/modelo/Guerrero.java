package ar.edu.unq.unidad3.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter @Setter @NoArgsConstructor

@Entity
public final class Guerrero extends Personaje {

    private Integer fuerza;


    public Guerrero(String nombre) {
        super();
        this.setNombre(nombre);
    }

}