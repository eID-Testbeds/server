package com.secunet.eidserver.testbed.model.entities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;

@Entity
@Table(name = "CERTIFICATE_X509")
@NamedQuery(name = "CertificateX509Entity.findAll", query = "SELECT t FROM CertificateX509Entity t")
public class CertificateX509Entity extends BaseEntity implements Serializable, CertificateX509
{
	private static final long serialVersionUID = 1603400191631663139L;

	private static final Logger logger = LogManager.getRootLogger();

	@Column(name = "CERTIFICATE_NAME")
	private String certificateName;

	@Lob
	@Column(length = 500000)
	private byte[] x509certificate;

	@Lob
	@Column(length = 500000)
	private byte[] privateKey;

	@Lob
	@Column(length = 500000)
	private byte[] publicKey;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certificateName == null) ? 0 : certificateName.hashCode());
		result = prime * result + Arrays.hashCode(x509certificate);
		return result;
	}

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
		CertificateX509Entity other = (CertificateX509Entity) obj;
		if (certificateName == null)
		{
			if (other.certificateName != null)
				return false;
		}
		else if (!certificateName.equals(other.certificateName))
			return false;
		if (!Arrays.equals(x509certificate, other.x509certificate))
			return false;
		return true;
	}

	/**
	 * @return the keyPair
	 */
	@Override
	public KeyPair getKeyPair()
	{
		KeyFactory factory;
		try
		{
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream stream = new ByteArrayInputStream(x509certificate);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(stream);
			String sigAlg;
			if (cert.getSigAlgName().contains("RSA"))
			{
				sigAlg = "RSA";
			}
			else if (cert.getSigAlgName().contains("EC"))
			{
				sigAlg = "EC";
			}
			else
			{
				sigAlg = "DSA";
			}
			factory = KeyFactory.getInstance(sigAlg);
			PrivateKey privK = factory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
			return new KeyPair(cert.getPublicKey(), privK);
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException | CertificateException e)
		{
			logger.warn("Could not create key pair: " + e.getMessage());
		}
		return null;
	}

	/**
	 * @param keyPair
	 *            the keyPair to set
	 */
	@Override
	public void setKeyPair(KeyPair keyPair)
	{
		this.privateKey = keyPair.getPrivate().getEncoded();
		this.publicKey = keyPair.getPublic().getEncoded();
	}
}
