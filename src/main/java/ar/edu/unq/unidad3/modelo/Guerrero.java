package ar.edu.unq.unidad3.modelo;

import lombok.*;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public final class Guerrero extends Personaje {

    private Integer fuerza;

    public Guerrero(@NonNull String nombre, @NonNull Integer vida, @NonNull Integer pesoMaximo, @NonNull Integer fuerza) {
        this.setNombre(nombre);
        this.setVida(vida);
        this.setPesoMaximo(pesoMaximo);
        this.setFuerza(fuerza);
    }

}