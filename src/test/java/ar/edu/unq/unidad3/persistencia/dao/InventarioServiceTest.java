package ar.edu.unq.unidad3.persistencia.dao;

import ar.edu.unq.unidad3.persistencia.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import ar.edu.unq.unidad3.service.impl.ItemServiceImpl;
import ar.edu.unq.unidad3.service.impl.PersonajeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioServiceTest {

    private PersonajeServiceImpl personajeService;
    private ItemServiceImpl itemService;
    private Personaje maguin;
    private Personaje debilucho;
    private Item baculo;
    private Item tunica;

    @BeforeEach
    void prepare() {
        this.personajeService = new PersonajeServiceImpl(
                new HibernatePersonajeDAO(),
                new HibernateItemDAO()
        );
        this.itemService = new ItemServiceImpl(
                new HibernateItemDAO()
        );

        maguin = new Personaje("Maguin",  10, 70);
        personajeService.guardarPersonaje(maguin);

        debilucho = new Personaje("Debilucho", 1, 1000);
        personajeService.guardarPersonaje(debilucho);

        tunica = new Item("Tunica", 100);
        baculo = new Item("Baculo", 50);

        itemService.guardarItem(tunica);
        itemService.guardarItem(baculo);




    }

    @Test
    void testRecoger() {
        personajeService.recoger(maguin.getId(), baculo.getId());

        Personaje maguito = personajeService.recuperarPersonaje(maguin.getId());
        assertEquals("Maguin", maguito.getNombre());

        assertEquals(1, maguito.getInventario().size());

        Item baculo = maguito.getInventario().iterator().next();
        assertEquals("Baculo", baculo.getNombre());

        assertEquals(baculo.getOwner(), maguito);
    }

    @Test
    void testGetAll() {
        var items = itemService.allItems();

        assertEquals(2, items.size());
        assertTrue(items.contains(baculo));
    }

    @Test
    void testGetMasPesados() {
        var items = itemService.getMasPesdos(10);
        assertEquals(2, items.size());

        var items2 = itemService.getMasPesdos(80);
        assertEquals(1, items2.size());
    }

    @Test
    void testGetItemsDebiles() {
        var items = itemService.getItemsPersonajesDebiles(5);
        assertEquals(0, items.size());

        personajeService.recoger(maguin.getId(), baculo.getId());
        personajeService.recoger(debilucho.getId(), tunica.getId());

        items = itemService.getItemsPersonajesDebiles(5);
        assertEquals(1, items.size());
        assertEquals("Tunica", items.iterator().next().getNombre());
    }

    @Test
    void testGetMasPesado() {
        Item item = itemService.heaviestItem();
        assertEquals("Tunica", item.getNombre());
    }

    @Test
    void siUnPersonajeAgarraMasPesoDelQuePuedeLlevarSeLanzaMuchoPesoException () {
        assertThrows(MuchoPesoException.class, () -> personajeService.recoger(maguin.getId(), tunica.getId()));
    }

    @AfterEach
    void cleanup() {

        itemService.eliminarTodos();
        personajeService.eliminarTodos();
    }
}
