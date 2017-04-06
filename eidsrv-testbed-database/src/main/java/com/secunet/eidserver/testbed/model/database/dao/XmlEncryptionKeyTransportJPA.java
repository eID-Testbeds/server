package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyTransportDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.model.entities.XmlEncryptionKeyTransportEntity;

@Stateless
public class XmlEncryptionKeyTransportJPA extends GenericJPA<XmlEncryptionKeyTransport> implements XmlEncryptionKeyTransportDAO
{

	public XmlEncryptionKeyTransportJPA()
	{
		super("XmlEncryptionKeyTransport");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<XmlEncryptionKeyTransport> findAll()
	{
		Collection<XmlEncryptionKeyTransport> col = entityManager.createQuery("SELECT x FROM XmlEncryptionKeyTransportEntity x").getResultList();
		return (Set<XmlEncryptionKeyTransport>) createSetFromCollection(col);
	}

	@Override
	public XmlEncryptionKeyTransport createNew()
	{
		XmlEncryptionKeyTransport instance = new XmlEncryptionKeyTransportEntity();
		return instance;
	}
}
