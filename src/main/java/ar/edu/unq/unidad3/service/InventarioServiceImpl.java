package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.dao.ItemDAO;
import ar.edu.unq.unidad3.dao.PersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

import java.util.Collection;
import java.util.List;

public class InventarioServiceImpl implements InventarioService {

    private final PersonajeDAO personajeDAO;
    private final ItemDAO itemDAO;

    public InventarioServiceImpl(PersonajeDAO personajeDAO, ItemDAO itemDAO) {
        this.personajeDAO = personajeDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public Collection<Item> allItems() {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getAll());
    }

    @Override
    public Item heaviestItem() {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getHeaviestItem());
    }

    @Override
    public void guardarItem(Item item) {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.guardar(item);
            return null;
        });
    }

    @Override
    public void guardarPersonaje(Personaje personaje) {
        HibernateTransactionRunner.runTrx(() -> {
            personajeDAO.guardar(personaje);
            return null;
        });
    }

    @Override
    public Personaje recuperarPersonaje(Long personajeId) {
        return HibernateTransactionRunner.runTrx(() -> personajeDAO.recuperar(personajeId));
    }

    @Override
    public void recoger(Long personajeId, Long itemId) {
        HibernateTransactionRunner.runTrx(() -> {
            Personaje personaje = personajeDAO.recuperar(personajeId);
            Item item = itemDAO.recuperar(itemId);
            personaje.recoger(item);
            personajeDAO.guardar(personaje);
            return null;
        });
    }

    @Override
    public Collection<Item> getMasPesdos(int peso) {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getMasPesados(peso));
    }

    @Override
    public Collection<Item> getItemsPersonajesDebiles(int vida) {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getItemsDePersonajesDebiles(vida));
    }

    @Override
    public void eliminarTodo(){
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.eliminarTodo();
            personajeDAO.eliminarTodo();
            return null;
        });
    }
    
    @Override
    public List<Personaje> recuperarTodosPersonajes() {
        return HibernateTransactionRunner.runTrx(() -> personajeDAO.recuperarTodos());
    }
}