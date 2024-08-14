package ar.edu.unq.unidad3.modelo;

import lombok.*;

import jakarta.persistence.*;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString

@Entity
public final class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer peso;

    @ManyToOne
    private Personaje owner;

    public Item(String nombre, int peso) {
        this.nombre = nombre;
        this.peso = peso;
    }

}