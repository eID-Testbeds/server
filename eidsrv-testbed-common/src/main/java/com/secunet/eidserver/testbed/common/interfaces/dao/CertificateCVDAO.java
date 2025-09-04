package com.secunet.eidserver.testbed.common.interfaces.dao;

import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;

import javax.ejb.Local;

@Local
public interface CertificateCVDAO extends GenericDAO<CertificateCV>
{

	public CertificateCV findByName(final String certName);
}
