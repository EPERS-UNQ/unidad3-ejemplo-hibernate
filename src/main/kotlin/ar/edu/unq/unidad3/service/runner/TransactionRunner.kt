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
        return session.use {
            val result = session!!.useTransaction{
                bloque()
            }
            session = null
            result
        }
    }
}


public inline fun <T : EntityTransaction, R> T.useTransaction(block: () -> R): R {
    try {
        //codigo de negocio
        val resultado = block()
        commit()
        return resultado
    } catch (e: RuntimeException) {
        rollback()
        throw e
    }
}


public inline fun <T : Session, R> T.useTransaction(block: () -> R): R {
    use {
        val tx =  beginTransaction()
        return tx.useTransaction(block)
    }
}