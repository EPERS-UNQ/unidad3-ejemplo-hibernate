package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.modelo.Item;
import java.util.Collection;

public interface ItemDAO {
    Collection<Item> getAll();
    Item getHeaviestItem();
    void guardar(Item item);
    Item recuperar(Long id);
    Collection<Item> getMasPesados(int peso);
    Collection<Item> getItemsDePersonajesDebiles(int unaVida);
    Collection<Item> recuperarPaginados(int elementosPorPagina, int pagina);
    int contarTodos();
}