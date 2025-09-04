package com.secunet.eidserver.testbed.model.database.dao;

import com.secunet.eidserver.testbed.common.interfaces.dao.BlacklistDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.CryptoFileDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Blacklist;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;
import com.secunet.eidserver.testbed.model.entities.BlacklistEntity;
import com.secunet.eidserver.testbed.model.entities.CryptoFileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import java.util.Collection;
import java.util.Set;

@Stateless
public class CryptoFileJPA extends GenericJPA<CryptoFile> implements CryptoFileDAO
{
	private static final Logger logger = LogManager.getRootLogger();

	public CryptoFileJPA()
	{
		super("CryptoFile");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CryptoFile> findAll()
	{
		Collection<CertificateCV> col = entityManager.createQuery("SELECT x FROM CryptoFileEntity x").getResultList();
		return (Set<CryptoFile>) createSetFromCollection(col);
	}

	@Override
	public CryptoFileEntity createNew()
	{
        return new CryptoFileEntity();
	}

	@Override
	public CryptoFile findByFilename(String filename) {
		return (CryptoFile) entityManager.createQuery("SELECT t FROM CryptoFileEntity t WHERE t.filename = :filename").setParameter("filename", filename).getSingleResult();
	}
}
