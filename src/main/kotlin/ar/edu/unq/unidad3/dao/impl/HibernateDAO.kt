package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.service.runner.TransactionRunner

open class HibernateDAO<T>(private val entityType: Class<T>) {

    fun guardar(item: T) {
        val session = TransactionRunner.currentSession
        session.save(item)
    }

    fun recuperar(id: Long?): T {
        val session = TransactionRunner.currentSession
        return session.get(entityType, id)
    }
}
