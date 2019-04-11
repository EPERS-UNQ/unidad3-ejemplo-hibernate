package ar.edu.unq.unidad3.wop.modelo;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	
	private int peso;

	@ManyToOne
	private Personaje owner;
	
	protected Item() {
	}
	
	public Item(String nombre, int peso) {
		this.nombre = nombre;
		this.peso = peso;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public int getPeso() {
		return this.peso;
	}
	
	public Personaje getOwner() {
		return this.owner;
	}
	
	public void setOwner(Personaje owner) {
		this.owner = owner;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.getNombre();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Item item = (Item) o;
		return Objects.equals(id, item.id);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
}
