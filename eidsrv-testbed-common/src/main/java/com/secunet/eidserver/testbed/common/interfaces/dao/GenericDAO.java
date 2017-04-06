package com.secunet.eidserver.testbed.common.interfaces.dao;

import java.io.Serializable;
import java.util.Set;

public interface GenericDAO<T>
{
	public abstract T createNew();

	public void persist(final T entity);

	public void merge(final T entity);

	public T findById(final Serializable id);

	public T update(final T entity);

	public void delete(final T entity);

	public abstract Set<?> findAll();
}
