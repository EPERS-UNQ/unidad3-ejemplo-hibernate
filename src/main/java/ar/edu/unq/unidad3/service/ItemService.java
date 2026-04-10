package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Item;

import java.util.Collection;

public interface ItemService {

    void guardarItem(Item item);
    Collection<Item> allItems();
    void eliminarItem(Item item);
    void eliminarTodos();
    Item heaviestItem();
    Collection<Item> getMasPesdos(int peso);
    Collection<Item> getItemsPersonajesDebiles(int vida);

}


