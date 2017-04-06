package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.model.entities.TestCaseStepEntity;

@Stateless
public class TestCaseStepJPA extends GenericJPA<TestCaseStep> implements TestCaseStepDAO
{

	public TestCaseStepJPA()
	{
		super("TestCaseStep");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TestCaseStep> findAll()
	{
		Collection<TestCaseStep> col = entityManager.createQuery("SELECT x FROM TestCaseStepEntity x").getResultList();
		return (Set<TestCaseStep>) createSetFromCollection(col);
	}

	@Override
	public TestCaseStep findByName(final String name)
	{
		try
		{
			return (TestCaseStep) entityManager.createQuery("SELECT t FROM TestCaseStepEntity t WHERE t.name = :name").setParameter("name", name).getSingleResult();
		}
		catch (NoResultException nre)
		{
			return null;
		}
	}

	@Override
	public TestCaseStep createNew()
	{
		TestCaseStep instance = new TestCaseStepEntity();
		return instance;
	}

}
