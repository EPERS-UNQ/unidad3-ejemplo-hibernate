package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;

public interface PersonajeDAO {
    void guardar(Personaje personaje);
    Personaje recuperar(Long id);
    void eliminar(Personaje personaje);

    void eliminarTodo();
}