package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.TlsDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.model.entities.TlsEntity;

@Stateless
public class TlsJPA extends GenericJPA<Tls> implements TlsDAO
{

	public TlsJPA()
	{
		super("Tls");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Tls> findAll()
	{
		Collection<Tls> col = entityManager.createQuery("SELECT x FROM TlsEntity x").getResultList();
		return (Set<Tls>) createSetFromCollection(col);
	}

	@Override
	public Tls createNew()
	{
		Tls instance = new TlsEntity();
		return instance;
	}

}
