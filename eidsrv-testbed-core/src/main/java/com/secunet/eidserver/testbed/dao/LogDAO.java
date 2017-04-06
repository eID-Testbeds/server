package com.secunet.eidserver.testbed.dao;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Named;

import com.secunet.eidserver.testbed.common.interfaces.entities.Log;

@Named
@Stateless
public class LogDAO extends GenericDAO<Log, String>
{

	public LogDAO()
	{
		super(Log.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Collection findAll()
	{
		return entityManager.createQuery("SELECT m FROM LogEntity m").getResultList();
	}

}
