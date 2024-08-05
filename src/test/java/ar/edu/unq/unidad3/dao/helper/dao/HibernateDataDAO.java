package ar.edu.unq.unidad3.dao.helper.dao;

import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;
import org.hibernate.Session;

import java.util.List;

public class HibernateDataDAO implements DataDAO {

    @Override
    public void clear() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        List<?> nombreDeTablas = session.createNativeQuery("show tables").getResultList();
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate();
        for (Object result : nombreDeTablas) {
            String tabla = "";
            if (result instanceof String) {
                tabla = (String) result;
            } else if (result instanceof Object[]) {
                tabla = ((Object[]) result)[0].toString();
            }
            session.createNativeQuery("truncate table " + tabla).executeUpdate();
        }
        session.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate();
    }
}