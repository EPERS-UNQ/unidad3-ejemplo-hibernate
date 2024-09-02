package ar.edu.unq.unidad3.dao.helper;

import ar.edu.unq.unidad3.dao.PersonajeDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Personaje;
import jakarta.persistence.LockModeType;

public class PersonajeLockService {

    private final PersonajeDAO personajeDAO;

    public PersonajeLockService(PersonajeDAO personajeDAO) {
        this.personajeDAO = personajeDAO;
    }

    public Personaje findPersonajeWithLock(Long id, LockModeType lockModeType) {
        return personajeDAO.findByIdWithLock(id, lockModeType);
    }
}
