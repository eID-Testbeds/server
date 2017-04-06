package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.model.entities.LogMessageEntity;

@Stateless
public class LogMessageJPA extends GenericJPA<LogMessage> implements LogMessageDAO
{

	public LogMessageJPA()
	{
		super("LogMessage");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<LogMessage> findAll()
	{
		Collection<LogMessage> col = entityManager.createQuery("SELECT x FROM LogMessageEntity x").getResultList();
		return (Set<LogMessage>) createSetFromCollection(col);
	}

	@Override
	public LogMessage createNew()
	{
		LogMessage instance = new LogMessageEntity();
		return instance;
	}

}
