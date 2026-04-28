package ar.edu.unq.unidad3.service.impl;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
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
    public void crear(Item item) {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.crear(item);
            return null;
        });
    }

    @Override
    public void actualizar(Item item) {
        HibernateTransactionRunner.runTrx(() -> {
            itemDAO.actualizar(item);
            return null;
        });
    }

    @Override
    public Item recuperar(Long itemId) {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.recuperar(itemId));
    }

    @Override
    public Collection<Item> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.recuperarTodos());
    }

    @Override
    public void eliminar(Item item) {
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
    public Item getMasPesado() {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getMasPesado());
    }

    @Override
    public Collection<Item> getMasPesados(int peso) {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getMasPesados(peso));
    }

    @Override
    public Collection<Item> getItemsPersonajesDebiles(int vida) {
        return HibernateTransactionRunner.runTrx(() -> itemDAO.getItemsDePersonajesDebiles(vida));
    }
}
