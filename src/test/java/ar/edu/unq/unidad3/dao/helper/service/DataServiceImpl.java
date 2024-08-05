package ar.edu.unq.unidad3.dao.helper.service;

import ar.edu.unq.unidad3.dao.helper.dao.DataDAO;
import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

public class DataServiceImpl implements DataService {

    private final DataDAO dataDAO;

    public DataServiceImpl(DataDAO dataDAO) {
        this.dataDAO = dataDAO;
    }

    @Override
    public void cleanAll() {
        HibernateTransactionRunner.runTrx(() -> {
            dataDAO.clear();
            return null;
        });
    }
}