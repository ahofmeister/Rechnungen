package de.alexanderhofmeister.rechnungen.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {

    INSTANCE;
    private EntityManagerFactory emFactory;

    private PersistenceManager() {
        this.emFactory = Persistence.createEntityManagerFactory("rechnungen-pu");
    }

    public void close() {
        this.emFactory.close();
    }

    public EntityManager getEntityManager() {
        return this.emFactory.createEntityManager();
    }
}