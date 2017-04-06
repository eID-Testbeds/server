package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.Query;

import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.model.entities.TestCandidateEntity;

@Stateless
public class TestCandidateJPA extends GenericJPA<TestCandidate> implements TestCandidateDAO
{
	@Override
	public TestCandidate createNew()
	{
		TestCandidate instance = new TestCandidateEntity();
		return instance;
	}

	public TestCandidateJPA()
	{
		super("TestCandidate");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TestCandidate> findAll()
	{
		Collection<TestCandidate> col = entityManager.createQuery("SELECT x FROM TestCandidateEntity x").getResultList();
		return (Set<TestCandidate>) createSetFromCollection(col);
	}

	@Override
	public boolean isCandidateNameAlreadyTaken(String candidateName)
	{
		// entityManager.createQuery("SELECT COUNT(1) FROM TestCandidateEntity x WHERE x.profileName == '" + candidateName + "'").getSingleResult();
		Query query = entityManager.createNamedQuery("TestCandidateEntity.exists");
		query.setParameter("name", candidateName);
		long val = (long) query.getSingleResult();
		return (val == 0) ? false : true;
		// if (val instanceof Long)
		// {
		// long longVal = (long) val;
		// return (longVal == 0) ? false : true;
		// }
		// else
		// {
		// return false;
		// }
	}
}
