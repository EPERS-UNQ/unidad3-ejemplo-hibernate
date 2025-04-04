package ar.edu.unq.unidad3.modelo;

import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import lombok.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @ToString

//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//InheritanceType.SINGLE_TABLE
//InheritanceType.TABLE_PER_CLASS
//InheritanceType.JOINED
@Entity
public abstract class Personaje {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 500)
    private String nombre;

    private Integer vida;
    private Integer pesoMaximo;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Item> inventario = new HashSet<>();



    public Personaje(@NonNull String nombre, @NonNull Integer vida,  @NonNull Integer pesoMaximo) {
        this.nombre = nombre;
        this.vida = vida;
        this.pesoMaximo = pesoMaximo;
    }

    public int getPesoActual() {
        return inventario.stream().mapToInt(Item::getPeso).sum();
    }

    public void recoger(Item item) {
        int pesoActual = this.getPesoActual();
        if (pesoActual + item.getPeso() > this.pesoMaximo) {
            throw new MuchoPesoException(this, item);
        }
        this.inventario.add(item);
        item.setOwner(this);
    }

}