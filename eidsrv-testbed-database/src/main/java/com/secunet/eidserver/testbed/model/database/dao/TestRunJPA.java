package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.TestRunDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;
import com.secunet.eidserver.testbed.model.entities.TestRunEntity;

@Stateless
public class TestRunJPA extends GenericJPA<TestRun> implements TestRunDAO
{

	public TestRunJPA()
	{
		super("TestRun");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TestRun> findAll()
	{
		Collection<TestRun> col = entityManager.createQuery("SELECT x FROM TestRunEntity x").getResultList();
		return (Set<TestRun>) createSetFromCollection(col);
	}

	@Override
	public TestRun createNew()
	{
		TestRun instance = new TestRunEntity();
		return instance;
	}

}
