package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.ics.IcsTlsClientcertificateType;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;

@Entity
@Table(name = "TLS_CLIENT_CERTIFICATE")
@NamedQuery(name = "TlsClientCertificateEntity.findAll", query = "SELECT t FROM TlsClientCertificateEntity t")
public class TlsClientCertificateEntity extends BaseEntity implements Serializable, TlsClientCertificate
{
	private static final long serialVersionUID = 2304701762643340089L;

	@Column(name = "SIGNATURE_ALGORITHM")
	@Enumerated(EnumType.STRING)
	private IcsTlsSignaturealgorithms signatureAlgorithm;

	@Column(name = "CERTIFICATE_TYPE")
	@Enumerated(EnumType.STRING)
	private IcsTlsClientcertificateType certificateType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TlsClientCertificateEntity other = (TlsClientCertificateEntity) obj;
		if (certificateType != other.certificateType)
			return false;
		if (signatureAlgorithm != other.signatureAlgorithm)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * ((certificateType == null) ? 0 : certificateType.hashCode());
		result = prime * result + ((signatureAlgorithm == null) ? 0 : signatureAlgorithm.hashCode());
		return result;
	}

	@Override
	public IcsTlsSignaturealgorithms getSignatureAlgorithm()
	{
		return this.signatureAlgorithm;
	}

	@Override
	public void setSignatureAlgorithm(IcsTlsSignaturealgorithms signatureAlgorithm)
	{
		this.signatureAlgorithm = signatureAlgorithm;
	}

	@Override
	public IcsTlsClientcertificateType getCertificateType()
	{
		return this.certificateType;
	}

	@Override
	public void setCertificateType(IcsTlsClientcertificateType certificateType)
	{
		this.certificateType = certificateType;
	}

}
