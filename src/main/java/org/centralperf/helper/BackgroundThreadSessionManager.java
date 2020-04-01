package org.centralperf.helper;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Allows to open and close Hibernate session for background threads
 */
@Component
public class BackgroundThreadSessionManager {

    private final EntityManagerFactory entityManagerFactory;

    public BackgroundThreadSessionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }
    }

    public void unbindSession() {
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
}
