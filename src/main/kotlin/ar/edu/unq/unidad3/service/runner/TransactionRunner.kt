package ar.edu.unq.unidad3.service.runner

import org.hibernate.Session
import javax.persistence.EntityTransaction

object TransactionRunner {
    private var session: Session? = null

    val currentSession: Session
        get() {
            if (session == null) {
                throw RuntimeException("No hay ninguna session en el contexto")
            }
            return session!!
        }


    fun <T> runTrx(bloque: ()->T): T {
        session = SessionFactoryProvider.instance.createSession()
        session.use {
            val tx =  session!!.beginTransaction()
            try {
                //codigo de negocio
                val resultado = bloque()
                tx!!.commit()
                return resultado
            } catch (e: RuntimeException) {
                tx.rollback()
                throw e
            }
        }
        session = null
    }
}
