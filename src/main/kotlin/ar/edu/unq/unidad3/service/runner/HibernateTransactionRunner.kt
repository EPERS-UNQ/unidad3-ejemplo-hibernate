package ar.edu.unq.unidad3.service.runner

import org.hibernate.Session
import javax.persistence.EntityTransaction

// Clase que corre el codigo dado en transaccion. Para ello, consigue una sesion del factoryProvider
// y pone dicha sesion a disposision del bloque de codigo corriendo en transaccion. (Usada por ejemplo, en los DAO)
object HibernateTransactionRunner {
    // Se guarda la sesion en un thread local, para que por cada accesso a servicio maneje
    // su propia sesion.
    private var sessionThreadLocal: ThreadLocal<Session?> = ThreadLocal()

    //Cuando se pide la sesion, si no hay (por que nunca se arranco la transaccion) rompe.
    val currentSession: Session
        get() {
            if (sessionThreadLocal.get() == null) {
                throw RuntimeException("No hay ninguna session en el contexto")
            }
            return sessionThreadLocal.get()!!
        }

    fun <T> runTrx(bloque: ()->T): T {
        val session = HibernateSessionFactoryProvider.instance.createSession()
        sessionThreadLocal.set(session)
            val tx =  session.beginTransaction()
            try {
                //codigo de negocio
                val resultado = bloque()
                tx.commit()
                return resultado
            } catch (e: RuntimeException) {
                tx.rollback()
                throw e
            }finally {
                session.close()
                sessionThreadLocal.set(null)
            }
    }
}
