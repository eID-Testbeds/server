package com.secunet.eidserver.testbed.common.interfaces.entities;

import com.secunet.eidserver.testbed.common.ics.IcsTlsClientcertificateType;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;

/**
 * NOTE: This class MUST override hashCode and equals!
 * 
 *
 */
public interface TlsClientCertificate
{
	public String getId();

	public void setId(String id);

	public IcsTlsSignaturealgorithms getSignatureAlgorithm();

	public void setSignatureAlgorithm(IcsTlsSignaturealgorithms signatureAlgorithm);

	public IcsTlsClientcertificateType getCertificateType();

	public void setCertificateType(IcsTlsClientcertificateType certificateType);

}