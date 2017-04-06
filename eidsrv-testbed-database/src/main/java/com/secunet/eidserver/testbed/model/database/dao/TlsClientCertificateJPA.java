package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.TlsClientCertificateDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;
import com.secunet.eidserver.testbed.model.entities.TlsClientCertificateEntity;

@Stateless
public class TlsClientCertificateJPA extends GenericJPA<TlsClientCertificate> implements TlsClientCertificateDAO
{

	public TlsClientCertificateJPA()
	{
		super("TlsClientCertificate");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TlsClientCertificate> findAll()
	{
		Collection<TlsClientCertificate> col = entityManager.createQuery("SELECT x FROM TlsClientCertificateEntity x").getResultList();
		return (Set<TlsClientCertificate>) createSetFromCollection(col);
	}

	@Override
	public TlsClientCertificate createNew()
	{
		TlsClientCertificate instance = new TlsClientCertificateEntity();
		return instance;
	}

}
