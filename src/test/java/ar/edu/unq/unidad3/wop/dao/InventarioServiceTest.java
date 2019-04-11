package ar.edu.unq.unidad3.wop.dao;

import ar.edu.unq.unidad3.wop.dao.impl.HibernateDataDAO;
import ar.edu.unq.unidad3.wop.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.wop.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.wop.modelo.Item;
import ar.edu.unq.unidad3.wop.modelo.Personaje;
import ar.edu.unq.unidad3.wop.service.InventarioService;
import ar.edu.unq.unidad3.wop.service.runner.SessionFactoryProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class InventarioServiceTest {
	
	private InventarioService service;

	private Personaje maguin;
	private Personaje debilucho;

	private Item baculo;
	private Item tunica;
	
	@Before
	public void prepare() {
		this.service = new InventarioService(new HibernatePersonajeDAO(), new HibernateItemDAO(), new HibernateDataDAO());

		tunica = new Item("Tunica", 100);
		baculo = new Item("Baculo", 50);

		this.service.guardarItem(tunica);
		this.service.guardarItem(baculo);

		maguin = new Personaje("Maguin");
		maguin.setPesoMaximo(70);
		maguin.setVida(10);
		this.service.guardarPersonaje(maguin);
		
		debilucho = new Personaje("Debilucho");
		debilucho.setPesoMaximo(1000);
		debilucho.setVida(1);
		this.service.guardarPersonaje(debilucho);
	}
	
	@After
	public void cleanup() {
		//Destroy cierra la session factory y fuerza a que, la proxima vez, una nueva tenga
		//que ser creada.
		//
		//Al tener hibernate configurado con esto <property name="hibernate.hbm2ddl.auto">create-drop</property>
		//al crearse una nueva session factory todo el schema será destruido y creado desde cero.
        SessionFactoryProvider.destroy();
//		service.clear();
	}
	
	
	@Test
	public void test_recoger() {
		this.service.recoger(maguin.getId(), baculo.getId());
		
		Personaje maguito = this.service.recuperarPersonaje(maguin.getId());
		Assert.assertEquals("Maguin", maguito.getNombre());

		Assert.assertEquals(1, maguito.getInventario().size());

		Item baculo = maguito.getInventario().iterator().next();
		Assert.assertEquals("Baculo", baculo.getNombre());

		Assert.assertSame(baculo.getOwner(), maguito);
	}
	
	@Test
	public void test_get_all() {
		Collection<Item> items = this.service.getAllItems();

		Assert.assertEquals(2, items.size());

		Assert.assertTrue(items.contains(baculo));
	}
	
	@Test
	public void test_get_mas_pesados() {
		Collection<Item> items = this.service.getMasPesdos(10);
		Assert.assertEquals(2, items.size());

		Collection<Item> items2 = this.service.getMasPesdos(80);
		Assert.assertEquals(1, items2.size());
	}
	
	@Test
	public void test_get_items_debiles() {
		Collection<Item> items = this.service.getItemsPersonajesDebiles(5);
		Assert.assertEquals(0, items.size());

		this.service.recoger(maguin.getId(), baculo.getId());
		this.service.recoger(debilucho.getId(), tunica.getId());
		
		items = this.service.getItemsPersonajesDebiles(5);
		Assert.assertEquals(1, items.size());
		Assert.assertEquals("Tunica", items.iterator().next().getNombre());

	}

	@Test
	public void test_get_mas_pesado() {
		Item item = this.service.getHeaviestItem();
		Assert.assertEquals("Tunica", item.getNombre());
	}
	
}
