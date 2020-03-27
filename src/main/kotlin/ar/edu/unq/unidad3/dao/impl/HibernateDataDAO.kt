package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.dao.DataDAO
import ar.edu.unq.unidad3.service.runner.TransactionRunner
import org.hibernate.Session

open class HibernateDataDAO : DataDAO {

    override fun clear() {
        val session = TransactionRunner.currentSession
        val nombreDeTablas = session.createNativeQuery("show tables").resultList
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()
        nombreDeTablas.forEach { tabla -> session.createNativeQuery("truncate table $tabla").executeUpdate() }
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
    }
}
