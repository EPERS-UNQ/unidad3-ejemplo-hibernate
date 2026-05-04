package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Personaje;

public interface PersonajeService {
    void crear(Personaje personaje);

    void actualizar(Personaje personaje);

    Personaje recuperar(Long personajeId);

    void recoger(Long personajeId, Long itemId);

    void eliminar(Personaje personaje);

    void eliminarTodos();
}
