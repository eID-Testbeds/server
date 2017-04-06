package com.secunet.eidserver.testbed.model.database.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.dao.GenericDAO;

public abstract class GenericJPA<T> implements GenericDAO<T>
{
	private static final Logger logger = LogManager.getRootLogger();

	@SuppressWarnings("rawtypes")
	private final Class entityClass;

	@PersistenceContext(unitName = "EIDSTB-PU")
	protected EntityManager entityManager;

	public GenericJPA(String name)
	{
		Class<?> clazz = null;
		try
		{
			clazz = Class.forName("com.secunet.eidserver.testbed.model.entities." + name + "Entity");
		}
		catch (ClassNotFoundException e)
		{
			logger.log(Level.ERROR, "Could not find persistance class name " + name);
		}
		if (clazz == null)
			entityClass = null;
		else
		{
			entityClass = clazz;
		}
	}

	@Override
	public void persist(final T entity)
	{
		entityManager.persist(entity);
		entityManager.flush();
	}

	@Override
	public void merge(final T entity)
	{
		entityManager.merge(entity);
		entityManager.flush();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findById(final Serializable id)
	{
		return (T) entityManager.find(entityClass, id);
	}

	@Override
	public T update(final T entity)
	{
		return entityManager.merge(entity);
	}

	@Override
	public void delete(final T entity)
	{
		entityManager.remove(entityManager.merge(entity));
		entityManager.flush();
	}

	@Override
	public abstract Set<?> findAll();

	protected Set<?> createSetFromCollection(Collection<?> collection)
	{
		if (collection != null)
		{
			return new HashSet<>(collection);
		}
		else
		{
			return new HashSet<>();
		}
	}
}
