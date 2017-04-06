package com.secunet.eidserver.testbed.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 */
public abstract class GenericDAO<T, PK extends Serializable> {
	@SuppressWarnings("rawtypes")
    private final Class entityClass;

    @PersistenceContext(unitName = "EIDSTB-PU")
    protected EntityManager entityManager;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void persist(final T entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }
    
    public void merge(final T entity) {
        entityManager.merge(entity);
        entityManager.flush();
    }

    @SuppressWarnings("unchecked")
    public T findById(final PK id) {
        return (T) entityManager.find(entityClass, id);
    }

    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    public void delete(final T entity) {
        entityManager.remove(entityManager.merge(entity));
        entityManager.flush();
    }

    @SuppressWarnings("rawtypes")
    public abstract Collection findAll();
}
