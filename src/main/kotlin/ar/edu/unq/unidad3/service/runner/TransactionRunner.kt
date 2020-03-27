package ar.edu.unq.unidad3.service.runner

import org.hibernate.Session
import org.hibernate.Transaction

import java.util.function.Supplier

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
        var tx: Transaction? = null
        try {
            session = SessionFactoryProvider.instance.createSession()
            tx = session!!.beginTransaction()

            //codigo de negocio
            val resultado = bloque()

            tx!!.commit()
            return resultado
        } catch (e: RuntimeException) {
            //solamente puedo cerrar la transaccion si fue abierta antes,
            //puede haberse roto el metodo ANTES de abrir una transaccion
            if (tx != null && tx.isActive) {
                tx.rollback()
            }
            throw e
        } finally {
            if (session != null) {
                session!!.close()
                session = null
            }
        }
    }


}
