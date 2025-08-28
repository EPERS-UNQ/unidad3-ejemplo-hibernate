package ar.edu.unq.unidad3.persistencia.dao;

import ar.edu.unq.unidad3.persistencia.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }
}
