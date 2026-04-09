package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;

import java.util.Collection;

public interface PersonajeService {
    void guardarPersonaje(Personaje personaje);
    Personaje recuperarPersonaje(Long personajeId);
    void recoger(Long personajeId, Long itemId);
    void eliminarPersonaje(Personaje personaje);
    void eliminarTodos();



}
