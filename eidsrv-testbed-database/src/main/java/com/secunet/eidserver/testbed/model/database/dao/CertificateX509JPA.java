package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.dao.CertificateX509DAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.model.entities.CertificateX509Entity;

@Stateless
public class CertificateX509JPA extends GenericJPA<CertificateX509> implements CertificateX509DAO
{
	private static final Logger logger = LogManager.getRootLogger();

	public CertificateX509JPA()
	{
		super("CertificateX509");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CertificateX509> findAll()
	{
		Collection<CertificateX509> col = entityManager.createQuery("SELECT x FROM CertificateX509Entity x").getResultList();
		return (Set<CertificateX509>) createSetFromCollection(col);
	}

	@Override
	public CertificateX509 findByName(final String certName)
	{
		try
		{
			return (CertificateX509) entityManager.createQuery("SELECT t FROM CertificateX509Entity t WHERE t.certificateName = :name").setParameter("name", certName).getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.warn(String.format("Could not find x509 certificate: %s", certName));
			return null;
		}
	}

	@Override
	public CertificateX509 createNew()
	{
		CertificateX509 instance = new CertificateX509Entity();
		return instance;
	}
}
