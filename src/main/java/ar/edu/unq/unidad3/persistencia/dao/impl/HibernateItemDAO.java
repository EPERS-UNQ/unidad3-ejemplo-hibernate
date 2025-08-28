package ar.edu.unq.unidad3.persistencia.dao.impl;

import ar.edu.unq.unidad3.persistencia.dao.ItemDAO;
import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.service.runner.HibernateSessionContext;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collection;

public class HibernateItemDAO extends HibernateDAO<Item> implements ItemDAO {

    public HibernateItemDAO() {
        super(Item.class);
    }

    @Override
    public Collection<Item> getAll() {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "select i from Item i order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        return query.getResultList();
    }

    @Override
    public Collection<Item> getMasPesados(int peso) {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "from Item i where i.peso > :unValorDado order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setParameter("unValorDado", peso);
        return query.getResultList();
    }

    @Override
    public Collection<Item> getItemsDePersonajesDebiles(int unaVida) {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "from Item i where i.owner.vida < :unaVida order by i.peso asc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setParameter("unaVida", unaVida);
        return query.getResultList();
    }

    @Override
    public Item getHeaviestItem() {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "from Item i order by i.peso desc";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
}