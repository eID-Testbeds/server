package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

public interface CertificateX509
{

	public X509Certificate getX509certificate();

	public void setX509certificate(X509Certificate x509certificate);

	public String getId();

	public void setId(String id);

	public String getCertificateName();

	public void setCertificateName(String certificateName);

	public KeyPair getKeyPair();

	public void setKeyPair(KeyPair pair);

}
