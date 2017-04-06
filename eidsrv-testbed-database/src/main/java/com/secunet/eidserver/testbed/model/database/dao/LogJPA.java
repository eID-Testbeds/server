package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.LogDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.model.entities.LogEntity;

@Stateless
public class LogJPA extends GenericJPA<Log> implements LogDAO
{

	public LogJPA()
	{
		super("Log");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Log> findAll()
	{
		Collection<Log> col = entityManager.createQuery("SELECT x FROM LogEntity x").getResultList();
		return (Set<Log>) createSetFromCollection(col);
	}

	@Override
	public Log createNew()
	{
		Log instance = new LogEntity();
		return instance;
	}

}
