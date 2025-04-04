package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import ar.edu.unq.unidad3.service.runner.HibernateSessionFactoryProvider;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.PersistentObjectException;
import org.hibernate.TransientObjectException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.hibernate.exception.ConstraintViolationException;
import jakarta.persistence.PersistenceException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class InventarioServiceTest {

    private InventarioServiceImpl service;
    private Personaje maguin;
    private Personaje debilucho;
    private Item baculo;
    private Item tunica;

    @BeforeEach
    void prepare() {
        this.service = new InventarioServiceImpl(
                new HibernatePersonajeDAO(),
                new HibernateItemDAO()
        );

        tunica = new Item("Tunica", 100);
        baculo = new Item("Baculo", 50);

        service.guardarItem(tunica);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin",  10, 70);
        service.guardarPersonaje(maguin);

        debilucho = new Personaje("Debilucho", 1, 1000);
        service.guardarPersonaje(debilucho);
    }

    @Test
    void testRecoger() {
        service.recoger(maguin.getId(), baculo.getId());

        Personaje maguito = service.recuperarPersonaje(maguin.getId());
        assertEquals("Maguin", maguito.getNombre());

        assertEquals(1, maguito.getInventario().size());

        Item baculo = maguito.getInventario().iterator().next();
        assertEquals("Baculo", baculo.getNombre());

        assertSame(baculo.getOwner(), maguito);
    }

    @Test
    void testGetAll() {
        var items = service.allItems();

        assertEquals(2, items.size());
        assertTrue(items.contains(baculo));
    }

    @Test
    void testGetMasPesados() {
        var items = service.getMasPesdos(10);
        assertEquals(2, items.size());

        var items2 = service.getMasPesdos(80);
        assertEquals(1, items2.size());
    }

    @Test
    void testGetItemsDebiles() {
        var items = service.getItemsPersonajesDebiles(5);
        assertEquals(0, items.size());

        service.recoger(maguin.getId(), baculo.getId());
        service.recoger(debilucho.getId(), tunica.getId());

        items = service.getItemsPersonajesDebiles(5);
        assertEquals(1, items.size());
        assertEquals("Tunica", items.iterator().next().getNombre());
    }

    @Test
    void testGetMasPesado() {
        Item item = service.heaviestItem();
        assertEquals("Tunica", item.getNombre());
    }

    @Test
    void siUnPersonajeAgarraMasPesoDelQuePuedeLlevarSeLanzaMuchoPesoException () {
        assertThrows(MuchoPesoException.class, () -> service.recoger(maguin.getId(), tunica.getId()));
    }

    @Test
    void testEstadoPersistentYSincronizacion() {
        HibernateTransactionRunner.runTrx(() -> {
            Session session = HibernateTransactionRunner.getCurrentSession();
            
            // Recuperamos una entidad existente (está en estado "persistent" en esta sesión)
            Personaje persistentPersonaje = session.get(Personaje.class, maguin.getId());
            
            // Modificamos la entidad
            persistentPersonaje.setNombre("Nombre modificado");
            
            // No necesitamos llamar a save/update explícitamente, Hibernate hace flush automático
            // al final de la transacción
            
            return null;
        });
        
        // Verificamos que los cambios se guardaron al final de la transacción
        Personaje personajeRecuperado = service.recuperarPersonaje(maguin.getId());
        assertEquals("Nombre modificado", personajeRecuperado.getNombre(), 
                    "Los cambios deben persistirse automáticamente al final de la transacción");
    }

    @Test
    void testProblemaConSaveYEntidadesDetached() {
        Long maguinId = maguin.getId();

        // Cambiamos el nombre en la entidad detached
        maguin.setNombre("Maguin Modificado");

        // Intentamos guardar la entidad modificada usando save()
        service.guardarPersonaje(maguin);

        // Verificamos que el cambio NO se guardó en la base de datos
        // Esto demuestra que save() no actualiza correctamente entidades detached
        Personaje verificacionPostSave = service.recuperarPersonaje(maguinId);
        assertEquals("Maguin", verificacionPostSave.getNombre(),
                "save() no debe actualizar entidades detached, el nombre debe seguir siendo el original");
    }

    @Test
    void testProblemaConCascadeYEntidadesDetach() {
        // Se persisten otro item en otra sesión, dejandolos a todos en Detach, pero disponibles a nivel instancia
        var espada = new Item("Contrato Virtuoso", 29);
        service.guardarItem(espada);

        // Transient
        Personaje otroPersonaje = new Personaje("Magatito",  10, 70);

        // Se añaden instancias en Detach al inventario de la entidad Transient a nivel de memoria
        otroPersonaje.setInventario(Set.of(baculo, tunica, espada));

        service.guardarPersonaje(otroPersonaje);
        Personaje otroPersonajeRecuperado = service.recuperarPersonaje(otroPersonaje.getId());

        assertTrue(otroPersonajeRecuperado.getInventario().isEmpty());
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }
}
