package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyAgreementDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.model.entities.XmlEncryptionKeyAgreementEntity;

@Stateless
public class XmlEncryptionKeyAgreementJPA extends GenericJPA<XmlEncryptionKeyAgreement> implements XmlEncryptionKeyAgreementDAO
{

	public XmlEncryptionKeyAgreementJPA()
	{
		super("XmlEncryptionKeyAgreement");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<XmlEncryptionKeyAgreement> findAll()
	{
		Collection<XmlEncryptionKeyAgreement> col = entityManager.createQuery("SELECT x FROM XmlEncryptionKeyAgreementEntity x").getResultList();
		return (Set<XmlEncryptionKeyAgreement>) createSetFromCollection(col);
	}

	@Override
	public XmlEncryptionKeyAgreement createNew()
	{
		XmlEncryptionKeyAgreement instance = new XmlEncryptionKeyAgreementEntity();
		return instance;
	}

}
