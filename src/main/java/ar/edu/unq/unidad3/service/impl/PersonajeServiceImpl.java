package ar.edu.unq.unidad3.service.impl;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.persistencia.dao.ItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.PersonajeDAO;
import ar.edu.unq.unidad3.service.PersonajeService;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

public class PersonajeServiceImpl implements PersonajeService {

    private final PersonajeDAO personajeDAO;
    private final ItemDAO itemDAO;

    public PersonajeServiceImpl(PersonajeDAO personajeDAO, ItemDAO itemDAO) {
        this.personajeDAO = personajeDAO;
        this.itemDAO = itemDAO;
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
        return HibernateTransactionRunner.runTrx(()->
            personajeDAO.recuperar(personajeId));
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
    public void eliminarPersonaje(Personaje personaje) {
        HibernateTransactionRunner.runTrx(()-> {
            personajeDAO.eliminar(personaje);
            return null;
        });
    }

    @Override
    public void eliminarTodos() {
        HibernateTransactionRunner.runTrx(() -> {
            personajeDAO.eliminarTodo();
            return null;
        });
    }
}
