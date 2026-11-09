package com.secunet.eidserver.testbed.common.interfaces.dao;

import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;

import javax.ejb.Local;

@Local
public interface CryptoFileDAO extends GenericDAO<CryptoFile>
{

	public CryptoFile findByFilename(final String filename);
}
