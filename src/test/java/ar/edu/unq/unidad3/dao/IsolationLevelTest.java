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

public class IsolationLevelTest {

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
                            assertEquals("Maguin", personajeAgain.getNombre());

                            // Thread 1 updates the personaje again
                            personajeAgain.setNombre("Sarazan");
                            System.out.println("Thread 1 - Updateo Maguin a Sarazan");
                            dao.guardar(personajeAgain);
                }, () -> System.out.println("Thread 1 - Termino")
        );

        // Thread 2
        concurrencyHelper.runInTransaction(() -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            personaje2.setNombre("MaguinUpdated");
            dao.guardar(personaje2);
        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        // Esperamos que ambos threads terminen
        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertEquals("Sarazan", updatedPersonaje.getNombre());
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
            // En READ_COMMITED, Thread 1 ve el cambio que thread 2 ya commiteo
            assertEquals("MaguinUpdated", personajeAgain.getNombre());

            personajeAgain.setNombre("Sarazan");
            System.out.println("Thread 1 - Updateando Maguin a Sarazan");
            dao.guardar(personajeAgain);
        }, () -> System.out.println("Thread 1 - Termino"));

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            personaje2.setNombre("MaguinUpdated");
            dao.guardar(personaje2);
        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertEquals("Sarazan", updatedPersonaje.getNombre());
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
            assertEquals("MaguinUpdated", personajeAgain.getNombre());

            personajeAgain.setNombre("Sarazan");
            System.out.println("Thread 1 - Updateando Maguin a Sarazan");
            dao.guardar(personajeAgain);
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
            personaje2.setNombre("MaguinUpdated");
            dao.guardar(personaje2);

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

        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        assertEquals("Sarazan", updatedPersonaje.getNombre());
    }

    // Este test nunca temrina porque el nivel de aislamiento SERIALIZABLE bloquea la escritura de Thread 2
    // y Thread 1 no puede terminar hasta que Thread 2 termine. Deadlock!
//    @Test
    void serializableIsolation() throws InterruptedException {
        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            System.out.println("Thread 1 - Primera lectura");

            Personaje personaje1 = dao.recuperar(maguin.getId());
            assertEquals("Maguin", personaje1.getNombre());

            System.out.println("Thread 1 - Lockeandose");
            concurrencyHelper.signalThread2ToStart();
            concurrencyHelper.waitForThread1ToResume();
            System.out.println("Thread 1 - De-Lockeado");
            // Thread 1 nunca llega a des-lockearse

        }, () -> System.out.println("Thread 1 - Termino"));

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            concurrencyHelper.waitForThread2ToStart();
            System.out.println("Thread 2 - Primera lectura");
            Personaje personaje2 = dao.recuperar(maguin.getId());

            System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
            personaje2.setNombre("MaguinUpdated");
            dao.guardar(personaje2);

            // Thread 2 se lockea al intentar guardar el personaje y la base de datos no lo liberara
            // hasta que termine el thread 1.

        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        // Esperamos a que ambos threads terminen
        concurrencyHelper.shutdown();
    }


    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }

}
