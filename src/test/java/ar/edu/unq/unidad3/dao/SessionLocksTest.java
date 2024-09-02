package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.helper.PersonajeLockService;
import ar.edu.unq.unidad3.dao.helper.TransactionConcurrencyHelper;
import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SessionLocksTest {

    private InventarioServiceImpl service;
    private PersonajeLockService testService;

    private TransactionConcurrencyHelper concurrencyHelper;

    private Personaje maguin;
    private Item baculo;
    private PersonajeDAO personajeDAO;

    @BeforeEach
    void setup() {
        personajeDAO = new HibernatePersonajeDAO();
        this.service = new InventarioServiceImpl(
                personajeDAO,
                new HibernateItemDAO()
        );
        testService = new PersonajeLockService(personajeDAO);
        concurrencyHelper = new TransactionConcurrencyHelper();
        baculo = new Item("Baculo", 50);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin", 10, 70);
        service.guardarPersonaje(maguin);
    }

    @Test
    void testPessimisticWriteLockFailure() throws InterruptedException {
        // Thread 1: Inicia transacción y pone el bloqueo PESSIMISTIC_WRITE
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje1 = testService.findPersonajeWithLock(maguin.getId(), LockModeType.PESSIMISTIC_WRITE);
            System.out.println("Thread 1 - Initial Read with PESSIMISTIC_WRITE: " + personaje1.getNombre());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();
        });

        // Thread 2: Intenta modificar el nombre del Personaje, pero falla por que esta lockeado
        concurrencyHelper.runInTransaction(() -> {
            assertThrows(Exception.class, () -> {
                Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
                System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
                personaje2.setNombre("MaguinUpdated");
                service.guardarPersonaje(personaje2);
            });
        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2

        concurrencyHelper.shutdown();

        // Verifica que el nombre no fue actualizado
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("Maguin", updatedPersonaje.getNombre());
    }

    @Test
    void testPessimisticReadLock() throws InterruptedException {
        // Thread 1: Inicia transacción y pone el bloqueo PESSIMISTIC_READ
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje1 = testService.findPersonajeWithLock(maguin.getId(), LockModeType.PESSIMISTIC_READ);
            System.out.println("Thread 1 - Initial Read with PESSIMISTIC_READ: " + personaje1.getNombre());
            assertEquals("Maguin", personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();
        });

        // Thread 2: Intenta modificar el nombre del Personaje, pero falla por que esta lockeado
        concurrencyHelper.runInTransaction(() -> {
            assertThrows(Exception.class, () -> {
                Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
                System.out.println("Thread 2 - Before Modification: " + personaje2.getNombre());
                personaje2.setNombre("MaguinUpdated");
                service.guardarPersonaje(personaje2);
            });
            System.out.println("Thread 2 - Caught expected exception during modification");
        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2

        concurrencyHelper.shutdown();

        // Verifica que el nombre no fue actualizado
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("Maguin", updatedPersonaje.getNombre());
    }

    @Test
    void testOptimisticLockFailure() throws InterruptedException {
        // Thread 1: Inicia una transacción y pone el bloqueo OPTIMISTIC
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje1 = testService.findPersonajeWithLock(maguin.getId(), LockModeType.OPTIMISTIC);
            personaje1.setNombre("MaguinThread1");
            System.out.println("Thread 1 - Initial Read with OPTIMISTIC: " + personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Intenta hacer commit de la transacción, esperando que falle debido a un conflicto de versión
            Exception exception = assertThrows(Exception.class, () -> {
                service.guardarPersonaje(personaje1);
            });
            System.out.println("Thread 1 - Caught expected exception: " + exception.getMessage());
        });

        // Thread 2: Modifica el nombre del Personaje, lo que casua un incremento de versión
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());

        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2

        concurrencyHelper.shutdown();

        // Verifica el estado final
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @Test
    void testOptimisticForceIncrementLockFailure() throws InterruptedException {
        // Thread 1: Inicia una transacción y pone el bloqueo OPTIMISTIC_FORCE_INCREMENT
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje1 = testService.findPersonajeWithLock(maguin.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            personaje1.setNombre("MaguinThread1");
            System.out.println("Thread 1 - Initial Read with OPTIMISTIC_FORCE_INCREMENT: " + personaje1.getNombre());

            concurrencyHelper.waitForOtherTransactions();

            // Intenta hacer commit de la transacción, esperando que falle debido a un conflicto de versión
            Exception exception = assertThrows(Exception.class, () -> {
                service.guardarPersonaje(personaje1);
            });
            System.out.println("Thread 1 - Caught expected exception: " + exception.getMessage());
        });

        // Thread 2: Modifica el nombre del Personaje, lo que causa un incremento de versión
        concurrencyHelper.runInTransaction(() -> {
            Personaje personaje2 = service.recuperarPersonaje(maguin.getId());
            personaje2.setNombre("MaguinUpdated");
            service.guardarPersonaje(personaje2);
            System.out.println("Thread 2 - After Modification: " + personaje2.getNombre());

        });
        //Liberamos al thread 1 que se habia quedo lockeado cuando termine el thread 2


        concurrencyHelper.shutdown();

        // Verifica el estado final
        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        System.out.println("Main Thread - After both transactions: " + updatedPersonaje.getNombre());
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }

}