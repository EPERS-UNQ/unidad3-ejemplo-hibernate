package ar.edu.unq.unidad3.service.impl;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.persistencia.dao.ItemDAO;
import ar.edu.unq.unidad3.service.ItemService;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

import java.util.Collection;


public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;

    public ItemServiceImpl(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public void guardarItem(Item item) {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.guardar(item);
            return null;
        });
    }
    @Override
    public Collection<Item> allItems() {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getAll());
    }

    @Override
    public void eliminarItem(Item item) {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.eliminar(item);
            return null;
        });
    }

    @Override
    public void eliminarTodos() {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.eliminarTodo();
            return null;
        });
    }

    @Override
    public Item heaviestItem() {
        return HibernateTransactionRunner.runTrx(()
                -> itemDAO.getHeaviestItem());
    }

    @Override
    public Collection<Item> getMasPesdos(int peso) {
        return HibernateTransactionRunner.runTrx(()
                -> itemDAO.getMasPesados(peso));
    }

    @Override
    public Collection<Item> getItemsPersonajesDebiles(int vida) {
        return HibernateTransactionRunner.runTrx(()
                -> itemDAO.getItemsDePersonajesDebiles(vida));
    }
}
