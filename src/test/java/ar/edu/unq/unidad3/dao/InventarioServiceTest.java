package ar.edu.unq.unidad3.dao;


import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Guerrero;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Mago;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import ar.edu.unq.unidad3.service.ItemsPaginados;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class InventarioServiceTest {

    private InventarioServiceImpl service;
    private Mago maguin;
    private Guerrero debilucho;
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

        maguin = new Mago("Maguin", 10, 70, 200);
        service.guardarPersonaje(maguin);

        debilucho = new Guerrero("Debilucho", 1, 1000, 5);
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
    void itemsPaginados() {
        // Pagina 0
        // Tunica y Baculo creados en el BeforeEach
        List.of(
                // Pagina 1
                new Item("item2", 100),
                new Item("item3", 50),
                // Pagina 2
                new Item("item4", 100),
                new Item("item5", 50),
                // Pagina 3
                new Item("item6", 100),
                new Item("item7", 50),
                // Pagina 4
                new Item("item8", 100),
                new Item("item9", 50)
        ).forEach(item -> service.guardarItem(item));

        // Recupero los elementos en la pagina 0
        ItemsPaginados itemsPagina0 = service.recuperarPaginados(2, 0);
        assertTrue(
                itemsPagina0.items().stream().anyMatch(item -> item.getNombre().equals("Tunica"))
        );
        assertTrue(
                itemsPagina0.items().stream().anyMatch(item -> item.getNombre().equals("Baculo"))
        );

        // Recupero los elementos en la pagina 2
        ItemsPaginados itemsPagina2 = service.recuperarPaginados(2, 2);
        assertTrue(
                itemsPagina2.items().stream().anyMatch(item -> item.getNombre().equals("item4"))
        );
        assertTrue(
                itemsPagina2.items().stream().anyMatch(item -> item.getNombre().equals("item5"))
        );

        // Intento recuperar elementos de una pagina inexistente
        ItemsPaginados itemsPagina5 = service.recuperarPaginados(2, 5);
        assertTrue(itemsPagina5.items().isEmpty());
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }
}
