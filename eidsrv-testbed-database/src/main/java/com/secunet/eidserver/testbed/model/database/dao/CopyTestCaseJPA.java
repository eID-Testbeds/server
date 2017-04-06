package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import com.secunet.eidserver.testbed.common.interfaces.dao.CopyTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.model.entities.CopyTestCaseEntity;

@Stateless
public class CopyTestCaseJPA extends GenericJPA<CopyTestCase> implements CopyTestCaseDAO
{

	public CopyTestCaseJPA()
	{
		super("CopyTestCase");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CopyTestCase> findAll()
	{
		Collection<CopyTestCase> col = entityManager.createQuery("SELECT x FROM CopyTestCaseEntity x").getResultList();
		return (Set<CopyTestCase>) createSetFromCollection(col);
	}

	@Override
	public CopyTestCase findByName(final String name)
	{
		try
		{
			return (CopyTestCase) entityManager.createQuery("SELECT t FROM CopyTestCaseEntity t WHERE t.name = :name").setParameter("name", name).getSingleResult();
		}
		catch (NoResultException nreInner)
		{
			return null;
		}
	}

	@Override
	public int elementCount()
	{
		return entityManager.createQuery("SELECT COUNT(t) FROM CopyTestCaseEntity t").getFirstResult();
	}

	@Override
	public CopyTestCase createNew()
	{
		CopyTestCase instance = new CopyTestCaseEntity();
		return instance;
	}

}
