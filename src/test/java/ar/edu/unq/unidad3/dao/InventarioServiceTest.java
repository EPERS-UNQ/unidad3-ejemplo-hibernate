package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.modelo.exception.MuchoPesoException;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import ar.edu.unq.unidad3.service.runner.HibernateSessionFactoryProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import java.util.logging.Logger;

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
    void seRecuperaUnObjetoCacheadoAccediendoSoloUnaVezALaBaseDeDatosDentroDeLaMismaSesion() {
        var logger = Logger.getLogger(this.getClass().getName());
        var sesion = HibernateSessionFactoryProvider.getInstance().createSession();

        logger.info("Recuperando maguito por primera vez");
        Personaje maguito = sesion.get(Personaje.class, maguin.getId());

        logger.info("Recuperando maguito por segunda vez");
        Personaje otroMaguito = sesion.get(Personaje.class, maguin.getId());

        logger.info("Cerramos la sesión");
        sesion.close();
    }

    @Test
    void seRecuperaUnObjetoCacheadoDesdeOtraSesionYTransaccion() {
        var logger = Logger.getLogger(this.getClass().getName());
        logger.info("Recuperando maguito por primera vez");
        var maguito = service.recuperarPersonaje(maguin.getId());

        logger.info("Recuperando maguito por segunda vez");
        var otroMaguito = service.recuperarPersonaje(maguin.getId());
    }

    @Test
    void seRecuperaUnObjetoCacheadoDesdeOtraSesionYTransaccionTrasActualizarSuCollecion() {
        var logger = Logger.getLogger(this.getClass().getName());
        logger.info("Recuperando maguito por primera vez");
        var maguito = service.recuperarPersonaje(maguin.getId());

        logger.info("Recuperando maguito por segunda vez");
        var otroMaguito = service.recuperarPersonaje(maguin.getId());

        logger.info("Actualizamos las relaciones en la DB");
        service.recoger(maguin.getId(), baculo.getId());

        logger.info("Recuperando maguito por tercera vez");
        var otroOtroMaguito = service.recuperarPersonaje(maguin.getId());
    }

    @AfterEach
    void cleanup() {
        service.eliminarTodo();
    }
}
