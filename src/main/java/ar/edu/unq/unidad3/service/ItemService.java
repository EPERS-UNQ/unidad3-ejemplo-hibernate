package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Item;

import java.util.Collection;

public interface ItemService {

    void crear(Item item);

    void actualizar(Item item);

    Collection<Item> recuperarTodos();

    void eliminar(Item item);

    void eliminarTodos();

    Item getMasPesado();

    Collection<Item> getMasPesados(int peso);

    Collection<Item> getItemsPersonajesDebiles(int vida);
}
