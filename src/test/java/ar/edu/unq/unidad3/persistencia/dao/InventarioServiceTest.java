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
        personajeService.crear(maguin);

        debilucho = new Personaje("Debilucho", 1, 1000);
        personajeService.crear(debilucho);

        tunica = new Item("Tunica", 100);
        baculo = new Item("Baculo", 50);

        itemService.crear(tunica);
        itemService.crear(baculo);
    }

    @Test
    void testRecoger() {
        personajeService.recoger(maguin.getId(), baculo.getId());

        Personaje maguito = personajeService.recuperar(maguin.getId());
        assertEquals("Maguin", maguito.getNombre());

        assertEquals(1, maguito.getInventario().size());

        Item baculo = maguito.getInventario().iterator().next();
        assertEquals("Baculo", baculo.getNombre());

        assertEquals(baculo.getPoseedor(), maguito);
    }

    @Test
    void testRecuperarTodos() {
        var items = itemService.recuperarTodos();

        assertEquals(2, items.size());
        assertTrue(items.stream().mapToLong(Item::getId).anyMatch(id -> id == baculo.getId()));
    }

    @Test
    void testGetMasPesados() {
        var items = itemService.getMasPesados(10);
        assertEquals(2, items.size());

        var items2 = itemService.getMasPesados(80);
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
        Item item = itemService.getMasPesado();
        assertEquals("Tunica", item.getNombre());
    }

    @Test
    void siUnPersonajeAgarraMasPesoDelQuePuedeLlevarSeLanzaMuchoPesoException () {
        assertThrows(MuchoPesoException.class, () -> personajeService.recoger(maguin.getId(), tunica.getId()));
    }

    @Test
    void testActualizarPersonaje() {
        maguin.setNombre("Maguito");
        maguin.setVida(99);
        personajeService.actualizar(maguin);

        Personaje recuperado = personajeService.recuperar(maguin.getId());
        assertEquals("Maguito", recuperado.getNombre());
        assertEquals(99, recuperado.getVida());
    }

    @Test
    void testActualizarItem() {
        baculo.setNombre("Baculo Magico");
        baculo.setPeso(10);
        itemService.actualizar(baculo);

        Item recuperado = itemService.recuperar(baculo.getId());
        assertEquals("Baculo Magico", recuperado.getNombre());
        assertEquals(10, recuperado.getPeso());
    }

    @AfterEach
    void cleanup() {
        itemService.eliminarTodos();
        personajeService.eliminarTodos();
    }
}
