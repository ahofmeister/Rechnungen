package de.alexanderhofmeister.rechnungen.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {

    INSTANCE;
    private EntityManagerFactory emFactory;

    PersistenceManager() {
        this.emFactory = Persistence.createEntityManagerFactory("rechnungen-pu");
    }

    public EntityManager getEntityManager() {
        return this.emFactory.createEntityManager();
    }

}