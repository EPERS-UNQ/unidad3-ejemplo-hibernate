package ar.edu.unq.unidad3.wop.service;

import ar.edu.unq.unidad3.wop.dao.DataDAO;
import ar.edu.unq.unidad3.wop.dao.ItemDAO;
import ar.edu.unq.unidad3.wop.dao.PersonajeDAO;
import ar.edu.unq.unidad3.wop.modelo.Item;
import ar.edu.unq.unidad3.wop.modelo.Personaje;
import static ar.edu.unq.unidad3.wop.service.runner.TransactionRunner.run;

import java.util.Collection;

public class InventarioService {
	
	private PersonajeDAO personajeDAO;
	private ItemDAO itemDAO;
	private DataDAO dataDAO;
	
	public InventarioService(PersonajeDAO personajeDAO, ItemDAO itemDAO, DataDAO dataDAO) {
		this.personajeDAO = personajeDAO;
		this.itemDAO = itemDAO;
		this.dataDAO = dataDAO;
	}

	public void guardarItem(Item item) {
		run(() -> {
			this.itemDAO.guardar(item);
		});
	}

	public void guardarPersonaje(Personaje personaje) {
		run(() -> {
			this.personajeDAO.guardar(personaje);
		});
	}

	public Personaje recuperarPersonaje(Long personajeId) {
		return run(() -> this.personajeDAO.recuperar(personajeId));
	}

	public void recoger(Long personajeId, Long itemId) {
		run(() -> {
			Personaje personaje = this.personajeDAO.recuperar(personajeId);
			Item item = this.itemDAO.recuperar(itemId);
			personaje.recoger(item);
		});
	}
	
	public Collection<Item> getAllItems() {
		return run(() -> this.itemDAO.getAll());
	}

	public Collection<Item> getMasPesdos(int peso) {
		return run(() -> this.itemDAO.getMasPesados(peso));
	}
	
	public Collection<Item> getItemsPersonajesDebiles(int vida) {
		return run(() -> this.itemDAO.getItemsDePersonajesDebiles(vida));
	}
	
	public Item getHeaviestItem() {
		return run(() -> this.itemDAO.getHeaviestItem());
	}


	public void clear() {
		run(() -> this.dataDAO.clear());
	}
	
	
}
