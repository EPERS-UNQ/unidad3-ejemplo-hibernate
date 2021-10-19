package ar.edu.unq.unidad3.service.runner

interface Transaction {
    fun start()
    fun commit()
    fun rollback()
}

object TransactionRunner {
    private var transactions:MutableList<Transaction> = mutableListOf()

    fun <T> runTrx(bloque: ()->T): T {
        transactions.forEach{ it.start()}
        try {
            val resultado = bloque()
            transactions.forEach{ it.commit()}
            return resultado
        } catch (e: RuntimeException) {
            transactions.forEach{ it.rollback()}
            throw e
        }finally {
            transactions = mutableListOf()
        }
    }

    fun addTransaction(transaction: Transaction){
        transactions.add(transaction)
    }
}

