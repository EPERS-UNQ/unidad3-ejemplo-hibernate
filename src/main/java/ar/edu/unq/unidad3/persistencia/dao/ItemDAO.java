package ar.edu.unq.unidad3.persistencia.dao;

import ar.edu.unq.unidad3.modelo.Item;

import java.util.Collection;

public interface ItemDAO {
    void crear(Item item);

    void actualizar(Item item);

    Item recuperar(Long id);

    Collection<Item> recuperarTodos();

    Item getMasPesado();

    Collection<Item> getMasPesados(int peso);

    Collection<Item> getItemsDePersonajesDebiles(int unaVida);

    void eliminar(Item item);

    void eliminarTodo();
}