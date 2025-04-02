package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import java.util.Collection;

public interface InventarioService {
    Collection<Item> allItems();
    Item heaviestItem();
    void guardarItem(Item item);
    void guardarPersonaje(Personaje personaje);
    Personaje recuperarPersonaje(Long personajeId);
    void recoger(Long personajeId, Long itemId);

    Collection<Item> getMasPesdos(int peso);
    Collection<Item> getItemsPersonajesDebiles(int vida);
    ItemsPaginados recuperarPaginados(int elementosPorPagina, int pagina);
    void eliminarTodo();
}