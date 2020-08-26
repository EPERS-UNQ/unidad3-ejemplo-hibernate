package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.dao.ItemDAO
import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner

open class HibernateItemDAO : HibernateDAO<Item>(Item::class.java),
    ItemDAO {

    override val all: Collection<Item>
        get() {
        val session = HibernateTransactionRunner.currentSession

        val hql = "select i from Item i order by i.peso asc"

        val query = session.createQuery(hql, Item::class.java)

        return query.resultList
    }

    override fun getMasPesados(peso: Int): Collection<Item> {
        val session = HibernateTransactionRunner.currentSession

        val hql = """
                    from Item i
                    where i.peso > :unValorDado 
                    order by i.peso asc
        """

        val query = session.createQuery(hql, Item::class.java)
        query.setParameter("unValorDado", peso)

        return query.resultList
    }

    override fun getItemsDePersonajesDebiles(unaVida: Int): Collection<Item> {
        val session = HibernateTransactionRunner.currentSession

        val hql = ("from Item i "
                + "where i.owner.vida < :unaVida "
                + "order by i.peso asc")

        val query = session.createQuery(hql, Item::class.java)
        query.setParameter("unaVida", unaVida)

        return query.resultList
    }

    override val heaviestItem: Item
        get() {
        val session = HibernateTransactionRunner.currentSession

        val hql = "from Item i order by i.peso desc"

        val query = session.createQuery(hql, Item::class.java)
        query.maxResults = 1

        return query.singleResult
    }

}
