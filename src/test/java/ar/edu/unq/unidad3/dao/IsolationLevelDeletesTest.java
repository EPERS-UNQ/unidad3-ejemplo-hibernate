package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.helper.TransactionConcurrencyHelper;
import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IsolationLevelDeletesTest {

    private InventarioServiceImpl service;
    private PersonajeDAO dao;

    private TransactionConcurrencyHelper concurrencyHelper;

    private Personaje maguin;
    private Item baculo;

    @BeforeEach
    void setup() {
        this.dao = new HibernatePersonajeDAO();
        this.service = new InventarioServiceImpl(
                dao,
                new HibernateItemDAO()
        );

        concurrencyHelper = new TransactionConcurrencyHelper();

        baculo = new Item("Baculo", 50);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin", 10, 70);
        service.guardarPersonaje(maguin);
    }

    @Test
    void repeatableReadIsolation() throws InterruptedException {
        // Por default, las transacciones de hibernate usan Connection.TRANSACTION_REPEATABLE_READ

        // Thread 1
        concurrencyHelper.runInTransaction(() -> {
                    System.out.println("Thread 1 - Primera lectura");

                    Personaje personaje1 = dao.recuperar(maguin.getId());
                    assertEquals("Maguin", personaje1.getNombre());

                    System.out.println("Thread 1 - Lockeandose");
                    // De-lockeamos thread 2, y esperamos a que termine
                    concurrencyHelper.signalThread2ToStart();
                    concurrencyHelper.waitForThread1ToResume();

                    ////// A partir de aca leer despues de que thread 2 haya terminado
                            System.out.println("Thread 1 - De-Lockeado");

                            // Limpiamos el cache L1, asi no molesta y probamos el aislamiento con la DB
                            HibernateTransactionRunner.getCurrentSession().clear();

                            // DESPUES DE THREAD 2 TERMINADO
                            System.out.println("Thread 1 - Releyendo");
                            Personaje personajeAgain = dao.recuperar(maguin.getId());
                            assertNull(personajeAgain);
                }, () -> System.out.println("Thread 1 - Termino")
        );

        // Thread 2
        concurrencyHelper.runInTransaction(() -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Eliminando Maguin a MaguinUpdated");
            dao.eliminar(personaje2);
        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        // Esperamos que ambos threads terminen
        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertNull(updatedPersonaje);
    }

    @Test
    void readCommittedIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            System.out.println("Thread 1 - Primera lectura");

            Personaje personaje1 = dao.recuperar(maguin.getId());
            assertEquals("Maguin", personaje1.getNombre());

            System.out.println("Thread 1 - Lockeandose");
            concurrencyHelper.signalThread2ToStart();
            concurrencyHelper.waitForThread1ToResume();
            System.out.println("Thread 1 - De-Lockeado");

            // Limpiamos el cache L1, asi no molesta y probamos el aislamiento con la DB
            HibernateTransactionRunner.getCurrentSession().clear();

            System.out.println("Thread 1 - Releyendo");
            Personaje personajeAgain = dao.recuperar(maguin.getId());

            assertNull(personajeAgain);
        }, () -> System.out.println("Thread 1 - Termino"));

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            dao.eliminar(personaje2);
        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje deletedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertNull(deletedPersonaje);
    }

    @Test
    void readUncommittedIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
            System.out.println("Thread 1 - Primera lectura");

            Personaje personaje1 = dao.recuperar(maguin.getId());
            assertEquals("Maguin", personaje1.getNombre());

            System.out.println("Thread 1 - Lockeandose");
            concurrencyHelper.signalThread2ToStart();
            concurrencyHelper.waitForThread1ToResume();
            System.out.println("Thread 1 - De-Lockeado");

            // Limpiamos el cache L1, asi no molesta y probamos el aislamiento con la DB
            HibernateTransactionRunner.getCurrentSession().clear();

            System.out.println("Thread 1 - Releyendo");
            Personaje personajeAgain = dao.recuperar(maguin.getId());

            // En READ_UNCOMMITTED, Thread 1 deberia ver los cambios no comiteados por el thread 2
            assertNull(personajeAgain);
            concurrencyHelper.signalThread2ToResume(); // le avisamos a thread 2 que siga y comitee
        }, () -> {
            System.out.println("Thread 1 - Termino");
        });

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");

            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            dao.eliminar(personaje2);

            // Se envian los cambios NO comiteados a la base de datos
            HibernateTransactionRunner.getCurrentSession().flush();

            // Le avisamos al thread 1 que continue, mientras nosotros esperamos a que termine para comitear
            concurrencyHelper.signalThread1ToResume();
            concurrencyHelper.waitForThread2ToResume();
        }, () -> {
            System.out.println("Thread 2 - Terminando");
        });

        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje deletedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertNull(deletedPersonaje);
    }

    @Test
    void serializableIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            System.out.println("Thread 1 - Primera lectura");

            Personaje personaje1 = dao.recuperar(maguin.getId());
            assertEquals("Maguin", personaje1.getNombre());
            dao.eliminar(personaje1);

            System.out.println("Thread 1 - Lockeandose");
            concurrencyHelper.signalThread2ToStart();
            concurrencyHelper.waitForThread1ToResume();
            System.out.println("Thread 1 - De-Lockeado");
            // Thread 1 no se deslockea hasta que thread 2 lo haga
        }, () -> System.out.println("Thread 1 - Termino"));

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            personaje2.setNombre("MaguinUpdated");
            dao.guardar(personaje2);
            concurrencyHelper.signalThread1ToResume();
        }, () -> {
            System.out.println("Thread 2 - Terminando");
        });

        // Esperamos a que ambos threads terminen
        concurrencyHelper.shutdown();
        Personaje deletedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertNull(deletedPersonaje);
    }


    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }

}