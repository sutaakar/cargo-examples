package com.github.sutaakar.cargo.service;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.github.sutaakar.cargo.service.model.PersistableCar;

/**
 * Service providing car functionality. 
 */
public class CarService {

    private EntityManager em;

    public CarService() {
        Map<String, String> persistenceProperties = getPersistenceProperties();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.hibernate.car.jpa", persistenceProperties);
        em = emf.createEntityManager();
    }

    public PersistableCar getCarByOwner(String owner) {
        return em.find(PersistableCar.class, owner);
    }

    public void putCar(PersistableCar car) {
        em.persist(car);
    }

    public void removeCar(String owner) {
        PersistableCar car = em.find(PersistableCar.class, owner);
        em.remove(car);
    }

    /* Helper methods */

    private Map<String, String> getPersistenceProperties() {
        String dataSourceJndi = System.getProperty("javax.persistence.nonJtaDataSource", "java:comp/env/car/Example");
        if(dataSourceJndi == null) {
            throw new RuntimeException("System property \"javax.persistence.nonJtaDataSource\" has to be defined!");
        }

        Map<String, String> persistenceProperties = new HashMap<String, String>();
        persistenceProperties.put("javax.persistence.nonJtaDataSource", dataSourceJndi);

        return persistenceProperties;
    }
}
