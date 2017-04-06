package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.XmlSignatureAlgorithmDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.model.entities.XmlSignatureEntity;

@Stateless
public class XmlSignatureAlgorithmJPA extends GenericJPA<XmlSignature> implements XmlSignatureAlgorithmDAO
{

	public XmlSignatureAlgorithmJPA()
	{
		super("XmlSignature");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<XmlSignature> findAll()
	{
		Collection<XmlSignature> col = entityManager.createQuery("SELECT x FROM XmlSignatureEntity x").getResultList();
		return (Set<XmlSignature>) createSetFromCollection(col);
	}

	@Override
	public XmlSignature createNew()
	{
		XmlSignature instance = new XmlSignatureEntity();
		return instance;
	}

}
