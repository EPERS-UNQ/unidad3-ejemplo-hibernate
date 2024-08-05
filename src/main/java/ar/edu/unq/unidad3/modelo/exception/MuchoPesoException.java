package ar.edu.unq.unidad3.modelo.exception;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;

public class MuchoPesoException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Personaje personaje;
    private final Item item;

    public MuchoPesoException(Personaje personaje, Item item) {
        this.personaje = personaje;
        this.item = item;
    }

    @Override
    public String getMessage() {
        return "El personaje [" + personaje + "] no puede recoger [" + item + "] porque cagar mucho peso ya";
    }
}