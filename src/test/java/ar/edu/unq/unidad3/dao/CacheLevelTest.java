package ar.edu.unq.unidad3.dao;

import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.service.InventarioServiceImpl;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Logger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheLevelTest {

    private InventarioServiceImpl service;
    private HibernatePersonajeDAO personajeDAO;
    private Personaje maguin;
    private Item baculo;

    @BeforeEach
    void prepare() {
        this.personajeDAO = new HibernatePersonajeDAO();
        this.service = new InventarioServiceImpl(
                personajeDAO,
                new HibernateItemDAO()
        );

        baculo = new Item("Baculo", 50);
        service.guardarItem(baculo);

        maguin = new Personaje("Maguin",  10, 70);
        service.guardarPersonaje(maguin);
    }

    @Test
    void seRecuperaUnObjetoCacheadoAccediendoSoloUnaVezALaBaseDeDatosDentroDeLaMismaSesion() {
        var logger = Logger.getLogger(this.getClass().getName());
        HibernateTransactionRunner.runTrx(() -> {
            logger.info("Recuperando maguito por primera vez");
            Personaje maguito = personajeDAO.recuperar(maguin.getId());

            logger.info("Recuperando maguito por segunda vez");
            Personaje otroMaguito = personajeDAO.recuperar(maguin.getId());

            return null;
        });
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
}
