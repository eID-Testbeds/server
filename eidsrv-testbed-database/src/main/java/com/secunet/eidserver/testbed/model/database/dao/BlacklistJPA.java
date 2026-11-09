package com.secunet.eidserver.testbed.model.database.dao;

import com.secunet.eidserver.testbed.common.interfaces.dao.BlacklistDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.CertificateCVDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Blacklist;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import com.secunet.eidserver.testbed.model.entities.BlacklistEntity;
import com.secunet.eidserver.testbed.model.entities.CertificateCVEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Set;

@Stateless
public class BlacklistJPA extends GenericJPA<Blacklist> implements BlacklistDAO
{
	private static final Logger logger = LogManager.getRootLogger();

	public BlacklistJPA()
	{
		super("Blacklist");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CertificateCV> findAll()
	{
		Collection<CertificateCV> col = entityManager.createQuery("SELECT x FROM CertificateCVEntity x").getResultList();
		return (Set<CertificateCV>) createSetFromCollection(col);
	}

	@Override
	public BlacklistEntity createNew()
	{
        return new BlacklistEntity();
	}

	@Override
	public Blacklist findByEservice(char eserviceID) {
		return (Blacklist) entityManager.createQuery("SELECT t FROM BlacklistEntity t WHERE t.eserviceID = :eservice").setParameter("eservice", eserviceID).getSingleResult();
	}
}
