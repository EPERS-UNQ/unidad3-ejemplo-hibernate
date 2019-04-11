package ar.edu.unq.unidad3.wop.modelo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import ar.edu.unq.unidad3.wop.modelo.exception.MuchoPesoException;

@Entity
public class Personaje {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(name="hist", length=100000)
	private String historia;
	
	private int pesoMaximo;
	private int xp;
	private int vida;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Item> inventario = new HashSet<>();
	
	protected Personaje() {
	}
	
	public Personaje(String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setHistoria(String historia) {
		this.historia = historia;
	}
	
	public int getPesoMaximo() {
		return this.pesoMaximo;
	}
	public void setPesoMaximo(int pesoMaximo) {
		this.pesoMaximo = pesoMaximo;
	}
	
	public int getXp() {
		return this.xp;
	}
	public void setXp(int xp) {
		this.xp = xp;
	}
	
	public int getVida() {
		return this.vida;
	}
	public void setVida(int vida) {
		this.vida = vida;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Item> getInventario() {
		return this.inventario;
	}
	public void setInventario(Set<Item> inventario) {
		this.inventario = inventario;
	}

	public void recoger(Item item) {
		int pesoActual = this.getPesoActual();
		if (pesoActual + item.getPeso() > this.pesoMaximo) {
			throw new MuchoPesoException(this, item);
		}
		
		this.inventario.add(item);
		item.setOwner(this);
	}

	private int getPesoActual() {
		int pesoActual = 0;
		for (Item item : this.inventario) {
			pesoActual += item.getPeso();
		}
		return pesoActual;
	}
	
	@Override
	public String toString() {
		return this.getNombre();
	}

}
