package ar.edu.unq.unidad3.wop.dao;

import java.util.Collection;

import ar.edu.unq.unidad3.wop.modelo.Item;

/**
 * Tiene la responsabilidad de guardar y recuperar items desde
 * el medio persistente
 */
public interface ItemDAO {

	void guardar(Item personaje);
	
	Item recuperar(Long id);

	Collection<Item> getAll();

	Collection<Item> getMasPesados(int peso);

	Collection<Item> getItemsDePersonajesDebiles(int unaVida);

	Item getHeaviestItem();
	
}
