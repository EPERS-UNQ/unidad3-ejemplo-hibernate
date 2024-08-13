package ar.edu.unq.unidad3.modelo;

import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//InheritanceType.SINGLE_TABLE
//InheritanceType.TABLE_PER_CLASS
//InheritanceType.JOINED
public abstract class Personaje {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(nullable = false, length = 500)
    private String nombre;

    //Not null by default
    private Integer vida;
    private Integer pesoMaximo;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Item> inventario = new HashSet<>();

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