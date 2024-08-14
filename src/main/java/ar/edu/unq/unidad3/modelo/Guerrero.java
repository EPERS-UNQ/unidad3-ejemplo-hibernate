package ar.edu.unq.unidad3.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public final class Guerrero extends Personaje {

    private Integer fuerza;


    public Guerrero(String nombre) {
        super();
        this.setNombre(nombre);
    }

}