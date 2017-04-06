package com.secunet.eidserver.testbed.testing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;

public class CertificateX509TestingPojo extends BaseTestingPojo implements Serializable, CertificateX509
{
	private static final long serialVersionUID = 1603400191631663139L;

	private static final Logger logger = LogManager.getRootLogger();

	private String certificateName;

	private byte[] x509certificate;

	private KeyPair pair;

	@Override
	public X509Certificate getX509certificate()
	{
		CertificateFactory certFactory;
		try
		{
			certFactory = CertificateFactory.getInstance("X.509");
			InputStream stream = new ByteArrayInputStream(x509certificate);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(stream);
			return cert;
		}
		catch (CertificateException e)
		{
			logger.warn("Could not read x509 certificate: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setX509certificate(X509Certificate x509certificate)
	{
		try
		{
			this.x509certificate = x509certificate.getEncoded();
		}
		catch (CertificateEncodingException e)
		{
			logger.warn("Could not save x509 certificate: " + e.getMessage());
		}
	}

	@Override
	public String getCertificateName()
	{
		return certificateName;
	}

	@Override
	public void setCertificateName(String certificateName)
	{
		this.certificateName = certificateName;
	}

	@Override
	public KeyPair getKeyPair()
	{
		return pair;
	}

	@Override
	public void setKeyPair(KeyPair pair)
	{
		this.pair = pair;

	}
}
