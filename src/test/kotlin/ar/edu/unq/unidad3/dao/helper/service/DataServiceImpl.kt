package ar.edu.unq.unidad3.dao.helper.service

import ar.edu.unq.unidad3.dao.PersonajeDAO
import ar.edu.unq.unidad3.dao.helper.dao.DataDAO
import ar.edu.unq.unidad3.modelo.Item
import ar.edu.unq.unidad3.modelo.Personaje
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner.runTrx

class DataServiceImpl (
    private val dataDAO: DataDAO
) : DataService {

    override fun cleanAll() {
        runTrx {
            dataDAO.clear()
        }
    }

}
