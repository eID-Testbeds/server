package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;

@Local
public interface CertificateX509DAO extends GenericDAO<CertificateX509>
{

	public CertificateX509 findByName(final String certName);
}
