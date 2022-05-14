package ar.edu.unq.unidad3.dao

import ar.edu.unq.unidad3.dao.impl.HibernateDataDAO
import ar.edu.unq.unidad3.dao.impl.HibernateItemDAO
import ar.edu.unq.unidad3.dao.impl.HibernatePersonajeDAO
import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje
import ar.edu.unq.unidad3.service.InventarioService
import ar.edu.unq.unidad3.service.InventarioServiceImp
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class InventarioServiceTest {

    lateinit var service: InventarioService
    lateinit var maguin: Personaje
    lateinit var debilucho: Personaje
    lateinit var baculo: Item
    lateinit var tunica: Item

    @BeforeEach
    fun prepare() {
        this.service = InventarioServiceImp(
            HibernatePersonajeDAO(),
            HibernateItemDAO(),
            HibernateDataDAO()
        )

        tunica = Item("Tunica", 100)
        baculo = Item("Baculo", 50)

        service.guardarItem(tunica)
        service.guardarItem(baculo)

        maguin = Personaje("Maguin")
        maguin.pesoMaximo = 70
        maguin.vida = 10
        service.guardarPersonaje(maguin)


        debilucho = Personaje("Debilucho")
        debilucho.pesoMaximo = 1000
        debilucho.vida = 1
        service.guardarPersonaje(debilucho)
    }

    @Test
    fun testRecoger() {
        service.recoger(maguin.id, baculo.id)

        val maguito = service.recuperarPersonaje(maguin.id)
        Assertions.assertEquals("Maguin", maguito.nombre)

        Assertions.assertEquals(1, maguito.inventario.size.toLong())

        val baculo = maguito.inventario.iterator().next()
        Assertions.assertEquals("Baculo", baculo.nombre)

        Assertions.assertSame(baculo.owner, maguito)
    }

    @Test
    fun testGetAll() {
        val items = service.allItems()

        Assertions.assertEquals(2, items.size.toLong())
        Assertions.assertTrue(items.contains(baculo))
    }

    @Test
    fun testGetMasPesados() {
        val items = service.getMasPesdos(10)
        Assertions.assertEquals(2, items.size.toLong())

        val items2 = service.getMasPesdos(80)
        Assertions.assertEquals(1, items2.size.toLong())
    }

    @Test
    fun testGetItemsDebiles() {
        var items = service.getItemsPersonajesDebiles(5)
        Assertions.assertEquals(0, items.size.toLong())

        service.recoger(maguin.id, baculo.id)
        service.recoger(debilucho.id, tunica.id)

        items = service.getItemsPersonajesDebiles(5)
        Assertions.assertEquals(1, items.size.toLong())
        Assertions.assertEquals("Tunica", items.iterator().next().nombre)

    }

    @Test
    fun testGetMasPesado() {
        val item = service.heaviestItem()
        Assertions.assertEquals("Tunica", item.nombre)
    }

    @AfterEach
    fun cleanup() {
        //Destroy cierra la session factory y fuerza a que, la proxima vez, una nueva tenga
        //que ser creada.
        service.clear()
    }

}
