package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.helper.dao.HibernateDataDAO;
import ar.edu.unq.unidad3.dao.helper.service.DataService;
import ar.edu.unq.unidad3.dao.helper.service.DataServiceImpl;
import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class InventarioServiceTest {

    private InventarioServiceImpl service;
    private DataService dataService;
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
        this.dataService = new DataServiceImpl(
                new HibernateDataDAO()
        );
        tunica = new Item("Tunica", 100);
        baculo = new Item("Baculo", 50);

        service.guardarItem(tunica);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin");
        maguin.setPesoMaximo(70);
        maguin.setVida(10);
        service.guardarPersonaje(maguin);

        debilucho = new Personaje("Debilucho");
        debilucho.setPesoMaximo(1000);
        debilucho.setVida(1);
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

    @AfterEach
    void cleanup() {
        // Destroy cierra la session factory y fuerza a que, la proxima vez, una nueva tenga que ser creada.
        dataService.cleanAll();
    }
}
