package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.helper.TransactionConcurrencyHelper;
import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IsolationLevelTest {

    private InventarioServiceImpl service;

    private TransactionConcurrencyHelper concurrencyHelper;

    private Personaje maguin;
    private Item baculo;

    @BeforeEach
    void setup() {
        this.service = new InventarioServiceImpl(
                new HibernatePersonajeDAO(),
                new HibernateItemDAO()
        );

        concurrencyHelper = new TransactionConcurrencyHelper();

        baculo = new Item("Baculo", 50);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin", 10, 70);
        service.guardarPersonaje(maguin);
    }

    @Test
    void testReadUncommittedIsolation() throws InterruptedException {
        // El nivel de aislamiento por default es: Connection.TRANSACTION_READ_UNCOMMITTED

        // Thread 1
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje1 = service.recuperarPersonaje(maguin.getId());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Releemos despues de que la otra transaccion comitee
            Personaje personajeAgain = service.recuperarPersonaje(maguin.getId());
            assertEquals("Maguin", personajeAgain.getNombre()); // Debería seguir siendo "Maguin"
        });

        // Thread 2
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());
        });
        // Liberamos al thread 1 que se habia quedo lockeado

        concurrencyHelper.shutdown();

        // Verificamos que el nombre haya sido actualizado fuera de la primera transacción
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @Test
    void testReadCommittedIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            Personaje personaje1 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Initial Read: " + personaje1.getNombre());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Releemos despues de que la otra transaccion comitee
            Personaje personajeAgain = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Re-read after modification: " + personajeAgain.getNombre());
            assertEquals("Maguin", personajeAgain.getNombre()); // Debería seguir siendo "Maguin"
        });

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());
        });
        //Liberamos al thread 1 que se habia quedo lockeado

        concurrencyHelper.shutdown();

        // Verificamos que el nombre haya sido actualizado fuera de la primera transacción
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @Test
    void testRepeatableReadIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_REPEATABLE_READ, () -> {
            Personaje personaje1 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Initial Read: " + personaje1.getNombre());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Releemos despues de que la otra transaccion comitee
            Personaje personajeAgain = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Re-read after modification: " + personajeAgain.getNombre());
            assertEquals("Maguin", personajeAgain.getNombre()); // Debería seguir siendo "Maguin"
        });

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_REPEATABLE_READ, () -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());
        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2

        concurrencyHelper.shutdown();

        // Verificamos que el nombre haya sido actualizado fuera de la primera transacción
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @Test
    void testSerializableIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            Personaje personaje1 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Initial Read: " + personaje1.getNombre());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Releemos despues de que la otra transaccion comitee
            Personaje personajeAgain = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 1 - Re-read after modification: " + personajeAgain.getNombre());
            assertEquals("Maguin", personajeAgain.getNombre()); // Debería seguir siendo "Maguin"
        });

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());

        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2

        concurrencyHelper.shutdown();

        // Verificamos que el nombre haya sido actualizado fuera de la primera transacción
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }

}
