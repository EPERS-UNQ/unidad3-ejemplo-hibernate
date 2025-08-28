package ar.edu.unq.unidad3.modelo;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.AUTO;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString

@Entity
public final class Item {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String nombre;
    private Integer peso;

    @ManyToOne
    private Personaje owner;

    public Item(@NonNull String nombre,  @NonNull Integer peso) {
        this.nombre = nombre;
        this.peso = peso;
    }
}