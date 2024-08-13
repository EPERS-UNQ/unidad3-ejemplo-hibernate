package ar.edu.unq.unidad3.modelo;

import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @ToString

@Entity
public class Personaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String nombre;
    private int vida;
    private int pesoMaximo;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Item> inventario = new HashSet<>();

    public Personaje(String nombre) {
        this.nombre = nombre;
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