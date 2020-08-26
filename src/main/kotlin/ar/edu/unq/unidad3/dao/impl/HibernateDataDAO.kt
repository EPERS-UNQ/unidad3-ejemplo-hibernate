package ar.edu.unq.unidad3.dao.impl

import ar.edu.unq.unidad3.dao.DataDAO
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner

open class HibernateDataDAO : DataDAO {

    override fun clear() {
        val session = HibernateTransactionRunner.currentSession
        val nombreDeTablas = session.createNativeQuery("show tables").resultList
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()
        nombreDeTablas.forEach { result ->
            var tabla = ""
            when(result){
                is String -> tabla = result
                is Array<*> -> tabla= result[0].toString()
            }
            session.createNativeQuery("truncate table $tabla").executeUpdate()
        }
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
    }
}
