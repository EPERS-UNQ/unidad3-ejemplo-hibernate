package ar.edu.unq.unidad3.modelo;

import lombok.*;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public final class Mago extends Personaje {
    private Integer magia;

    public Mago(@NonNull String nombre, @NonNull Integer vida, @NonNull Integer pesoMaximo, @NonNull Integer magia) {
        this.setNombre(nombre);
        this.setVida(vida);
        this.setPesoMaximo(pesoMaximo);
        this.setMagia(magia);
    }
}