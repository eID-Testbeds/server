package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;

import com.secunet.eidserver.testbed.common.ics.IcsTlsClientcertificateType;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;

public class TlsClientCertificateTestingPojo extends BaseTestingPojo implements Serializable, TlsClientCertificate
{
	private static final long serialVersionUID = 2304701762643340089L;

	private IcsTlsSignaturealgorithms signatureAlgorithm;

	private IcsTlsClientcertificateType certificateType;

	@Override
	public boolean equals(Object object)
	{
		if (object == null || this.getClass() != object.getClass())
		{
			return false;
		}
		final TlsClientCertificateTestingPojo other = (TlsClientCertificateTestingPojo) object;
		if ((this.getSignatureAlgorithm() == null) ? (other.getSignatureAlgorithm() != null) : !this.getSignatureAlgorithm().equals(other.getSignatureAlgorithm()))
		{
			return false;
		}
		if ((this.getCertificateType() == null) ? (other.getCertificateType() != null) : !this.getCertificateType().equals(other.getCertificateType()))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 53 * hash + (this.getSignatureAlgorithm() != null ? this.getSignatureAlgorithm().hashCode() : 0);
		hash = 53 * hash + (this.getCertificateType() != null ? this.getCertificateType().hashCode() : 0);
		return hash;
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
