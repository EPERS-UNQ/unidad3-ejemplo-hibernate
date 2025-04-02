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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsolationLevelUpdatesTest {

    private InventarioServiceImpl service;
    private PersonajeDAO dao;

    private TransactionConcurrencyHelper concurrencyHelper;

    private Personaje maguin;

    private Personaje debilucho;
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
        debilucho = new Personaje("Debilucho", 50, 10);
        service.guardarPersonaje(debilucho);
    }

    @Test
    void repeatableReadIsolation() throws InterruptedException {
        // En PostgreSQL, REPEATABLE READ previene lecturas no repetibles y lecturas fantasma
        // Si intentamos modificar datos que han sido modificados por otra transacción,
        // PostgreSQL lanzará un error de serialización
        
        AtomicBoolean thread1UpdateFailed = new AtomicBoolean(false);

        // Thread 1
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_REPEATABLE_READ, () -> {
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
                            
                            // En PostgreSQL con REPEATABLE_READ, seguimos viendo los datos originales
                            // a pesar de que otra transacción ya hizo commit con sus cambios
                            assertEquals("Maguin", personajeAgain.getNombre());

                            try {
                                // Thread 1 intenta actualizar el personaje
                                personajeAgain.setNombre("Sarazan");
                                System.out.println("Thread 1 - Updateo Maguin a Sarazan");
                                dao.guardar(personajeAgain);
                            } catch (Exception e) {
                                // En PostgreSQL, esta operación fallará con un error de serialización
                                // porque estamos intentando modificar datos que ya fueron modificados
                                System.out.println("Thread 1 - Error al actualizar (esperado en PostgreSQL): " + e.getMessage());
                                thread1UpdateFailed.set(true);
                            }
                }, () -> System.out.println("Thread 1 - Termino")
        );

        // Thread 2
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_REPEATABLE_READ, () -> {
            try {
                concurrencyHelper.waitForThread2ToStart();
                System.out.println("Thread 2 - Primera lectura");
                Personaje personaje2 = dao.recuperar(maguin.getId());

                System.out.println("Thread 2 - Updateando Maguin a MaguinUpdated");
                personaje2.setNombre("MaguinUpdated");
                dao.guardar(personaje2);
            } catch (Exception e) {
                // En teoría esta operación debería funcionar, ya que es la primera en modificar los datos
                System.out.println("Thread 2 - Error inesperado al actualizar: " + e.getMessage());
            }
        }, () -> {
            System.out.println("Thread 2 - Terminando y delockeando thread 1");
            concurrencyHelper.signalThread1ToResume();
        });

        // Esperamos que ambos threads terminen
        concurrencyHelper.shutdown();
        System.out.println("Se terminaron ambas transacciones, continuando...");

        Personaje updatedPersonaje = service.recuperarPersonaje(maguin.getId());
        
        // En PostgreSQL con REPEATABLE READ, cuando Thread 1 intenta modificar datos
        // que fueron modificados por Thread 2, se genera un error de serialización
        // Por lo tanto, el valor final debería ser el que estableció Thread 2
        assertEquals("MaguinUpdated", updatedPersonaje.getNombre());
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
    void serializableIsolation() throws InterruptedException {
        // Este test demuestra la diferencia entre REPEATABLE READ y SERIALIZABLE
        // En este escenario, dos transacciones verifican la suma total de XP de personajes 
        // y luego agregan un nuevo personaje basándose en esa suma.
        // Con REPEATABLE READ, ambas transacciones verían la misma suma inicial, 
        // pero solo una fallará al intentar actualizar.
        // Con SERIALIZABLE, la segunda transacción detectará el conflicto incluso
        // cuando no hay modificación directa de los mismos datos.
        
        // Configuración inicial: limpiamos personajes y creamos dos con una suma de XP específica
        service.eliminarTodo();
        Personaje p1 = new Personaje("Gandalf", 70, 1000);
        service.guardarPersonaje(p1);
        Personaje p2 = new Personaje("Aragorn", 100, 800);
        service.guardarPersonaje(p2);
        
        AtomicBoolean thread1Failed = new AtomicBoolean(false);
        AtomicBoolean thread2Failed = new AtomicBoolean(false);
        
        // Thread 1 - Verificará suma de XP y agregará a Legolas
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            try {
                System.out.println("Thread 1 - Calculando suma de XP");
                
                // Equivalente a: SELECT SUM(xp) FROM personaje
                List<Personaje> personajes = dao.recuperarTodos();
                int totalXP = personajes.stream().mapToInt(Personaje::getVida).sum();
                System.out.println("Thread 1 - Total XP: " + totalXP);
                
                // Comprobamos que la suma es menor que cierto límite (por ejemplo, 3000)
                assertTrue(totalXP < 3000, "La suma de XP debe ser menor a 3000");
                
                System.out.println("Thread 1 - Esperando a que Thread 2 lea...");
                concurrencyHelper.signalThread2ToStart();
                
                // Simulamos un retraso para dar tiempo a que Thread 2 lea y empiece su operación
                Thread.sleep(500);
                
                // Ahora insertamos a Legolas con 900 XP
                System.out.println("Thread 1 - Insertando a Legolas con 900 XP");
                Personaje legolas = new Personaje("Legolas", 80, 900);
                dao.guardar(legolas);
                
                System.out.println("Thread 1 - Legolas agregado con éxito");
            } catch (Exception e) {
                System.out.println("Thread 1 - Error: " + e.getMessage());
                thread1Failed.set(true);
            } finally {
                concurrencyHelper.signalThread1ToResume();
            }
        }, () -> System.out.println("Thread 1 - Transacción finalizada"));
        
        // Thread 2 - Verificará suma de XP y agregará a Gimli
        concurrencyHelper.runInTransaction(Connection.TRANSACTION_SERIALIZABLE, () -> {
            try {
                concurrencyHelper.waitForThread2ToStart();
                System.out.println("Thread 2 - Calculando suma de XP");
                
                // Equivalente a: SELECT SUM(xp) FROM personaje
                List<Personaje> personajes = dao.recuperarTodos();
                int totalXP = personajes.stream().mapToInt(Personaje::getVida).sum();
                System.out.println("Thread 2 - Total XP: " + totalXP);
                
                // Comprobamos que la suma es menor que cierto límite (por ejemplo, 3000)
                assertTrue(totalXP < 3000, "La suma de XP debe ser menor a 3000");
                
                // Damos tiempo para que Thread 1 continúe
                concurrencyHelper.waitForThread1ToResume();
                
                // Ahora insertamos a Gimli con 950 XP
                System.out.println("Thread 2 - Insertando a Gimli con 950 XP");
                Personaje gimli = new Personaje("Gimli", 90, 950);
                dao.guardar(gimli);
                
                System.out.println("Thread 2 - Gimli agregado con éxito");
            } catch (Exception e) {
                System.out.println("Thread 2 - Error: " + e.getMessage());
                thread2Failed.set(true);
            }
        }, () -> System.out.println("Thread 2 - Transacción finalizada"));
        
        // Esperamos que ambos threads terminen
        concurrencyHelper.shutdown();
        System.out.println("Ambas transacciones han finalizado");
        
        // Verificamos resultados usando el servicio en lugar del DAO directamente
        // No necesitamos envolver esto en una transacción explícita porque el servicio ya lo hace
        List<Personaje> personajesFinal = service.recuperarTodosPersonajes();
        System.out.println("Personajes finales: " + personajesFinal.size());
        
        for (Personaje p : personajesFinal) {
            System.out.println("Personaje: " + p.getNombre() + ", XP: " + p.getVida());
        }
        
        // En SERIALIZABLE, al menos una transacción debería fallar
        // porque se detecta el conflicto incluso cuando no hay actualización directa
        // de los mismos datos (phantom reads)
        assertTrue(thread1Failed.get() || thread2Failed.get(), 
            "Con SERIALIZABLE, al menos una transacción debe fallar por violación de serialización");
        
        // Si ambas transacciones se completaran, la suma final de XP superaría 
        // el límite que ambas transacciones verificaron independientemente
        int sumaFinal = personajesFinal.stream().mapToInt(Personaje::getVida).sum();
        System.out.println("Suma final de XP: " + sumaFinal);
        
        // La suma final debe ser 1800 (inicio) + el XP de una sola transacción
        // Si ambas transacciones se completaran (lo que no debería ocurrir con SERIALIZABLE),
        // la suma sería 1800 + 900 + 950 = 3650, violando la restricción
        assertTrue(sumaFinal < 3000, "La suma final de XP debe ser menor a 3000");
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }

}
