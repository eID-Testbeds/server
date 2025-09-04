package com.secunet.eidserver.testbed.model.database.dao;

import com.secunet.eidserver.testbed.common.interfaces.dao.CertificateCVDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.model.entities.CertificateCVEntity;
import com.secunet.eidserver.testbed.model.entities.CertificateX509Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Set;

@Stateless
public class CertificateCVJPA extends GenericJPA<CertificateCV> implements CertificateCVDAO
{
	private static final Logger logger = LogManager.getRootLogger();

	public CertificateCVJPA()
	{
		super("CertificateCV");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CertificateCV> findAll()
	{
		Collection<CertificateCV> col = entityManager.createQuery("SELECT x FROM CertificateCVEntity x").getResultList();
		return (Set<CertificateCV>) createSetFromCollection(col);
	}

	@Override
	public CertificateCV findByName(final String certName)
	{
		try
		{
			return (CertificateCV) entityManager.createQuery("SELECT t FROM CertificateCVEntity t WHERE t.certificateName = :name").setParameter("name", certName).getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.warn(String.format("Could not find CV certificate: %s", certName));
			return null;
		}
	}

	@Override
	public CertificateCV createNew()
	{
        return new CertificateCVEntity();
	}
}
