package ar.edu.unq.unidad3.persistencia.dao;

import ar.edu.unq.unidad3.modelo.Personaje;

public interface PersonajeDAO {
    void crear(Personaje personaje);

    void actualizar(Personaje personaje);

    Personaje recuperar(Long id);

    void eliminar(Personaje personaje);

    void eliminarTodo();
}