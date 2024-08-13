package ar.edu.unq.unidad3.dao.impl;

import ar.edu.unq.unidad3.dao.ItemDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collection;

public class HibernateItemDAO extends HibernateDAO<Item> implements ItemDAO {

    public HibernateItemDAO() {
        super(Item.class);
    }

    @Override
    public Collection<Item> getAll() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "select i from Item i order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        return query.getResultList();
    }

    @Override
    public Collection<Item> getMasPesados(int peso) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Item i where i.peso > :unValorDado order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setParameter("unValorDado", peso);
        return query.getResultList();
    }

    @Override
    public Collection<Item> getItemsDePersonajesDebiles(int unaVida) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Item i where i.owner.vida < :unaVida order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setParameter("unaVida", unaVida);
        return query.getResultList();
    }

    @Override
    public Item getHeaviestItem() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "from Item i order by i.peso desc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    @Override
    public Collection<Item> recuperarPaginados(int elementosPorPagina, int pagina) {
        Session session = HibernateTransactionRunner.getCurrentSession();

        String hql = "select i from Item i";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setFirstResult(pagina * elementosPorPagina);
        query.setMaxResults(elementosPorPagina);

        return query.getResultList();
    }
    @Override
    public int contarTodos() {
        Session session = HibernateTransactionRunner.getCurrentSession();

        String hql = "select count(i) from Item i";
        Query<Long> query = session.createQuery(hql, Long.class);

        return query.getSingleResult().intValue();
    }

}