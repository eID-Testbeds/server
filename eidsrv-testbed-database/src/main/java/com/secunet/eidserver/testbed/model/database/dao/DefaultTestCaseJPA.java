package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.model.entities.DefaultTestCaseEntity;

@Stateless
public class DefaultTestCaseJPA extends GenericJPA<DefaultTestCase> implements DefaultTestCaseDAO
{

	public DefaultTestCaseJPA()
	{
		super("DefaultTestCase");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<DefaultTestCase> findAll()
	{
		Collection<DefaultTestCase> col = entityManager.createQuery("SELECT x FROM DefaultTestCaseEntity x").getResultList();
		return (Set<DefaultTestCase>) createSetFromCollection(col);
	}

	@Override
	public DefaultTestCase findByName(final String name)
	{
		try
		{
			return (DefaultTestCase) entityManager.createQuery("SELECT t FROM DefaultTestCaseEntity t WHERE t.name = :name").setParameter("name", name).getSingleResult();
		}
		catch (NoResultException nre)
		{
			return null;
		}
	}

	@Override
	public int elementCount()
	{
		return entityManager.createQuery("SELECT COUNT(t) FROM DefaultTestCaseEntity t").getFirstResult();
	}

	@Override
	public DefaultTestCase createNew()
	{
		DefaultTestCase instance = new DefaultTestCaseEntity();
		return instance;
	}

}
