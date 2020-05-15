package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.service.runner.TransactionRunner

open class HibernateDAO<T>(private val entityType: Class<T>) {

    open fun guardar(entity: T) {
        val session = TransactionRunner.currentSession
        session.save(entity)
    }

    fun recuperar(id: Long?): T {
        val session = TransactionRunner.currentSession
        return session.get(entityType, id)
    }
}
