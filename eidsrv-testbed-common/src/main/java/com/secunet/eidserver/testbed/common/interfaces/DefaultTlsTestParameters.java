package com.secunet.eidserver.testbed.common.interfaces;

import java.util.Vector;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.NamedCurve;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.TlsUtils;

public class DefaultTlsTestParameters implements TlsTestParameters
{

	private ProtocolVersion version = ProtocolVersion.TLSv12;
	private ProtocolVersion selectedVersion = ProtocolVersion.TLSv12;
	private int[] cipherSuites = { CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
			CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256 };
	private int selectedCipherSuite = CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256;
	private int[] ellipticCurves = { NamedCurve.secp256r1, NamedCurve.secp384r1 };
	@SuppressWarnings("rawtypes")
	private Vector supportedSignatureAlgorithms = TlsUtils.getDefaultSupportedSignatureAlgorithms();
	private short alertLevelReceived = -1;
	private boolean clientAuthentication = false;
	private AsymmetricKeyParameter clientPrivateKey = null;
	private Certificate clientCertificate = null;

	private boolean encryptThenMACExtension = false;
	private boolean encryptThenMACExtensionEnabled = false;

	private byte[][] psk = null;

	@Override
	public ProtocolVersion getVersion()
	{
		return version;
	}

	@Override
	public ProtocolVersion getSelectedVersion()
	{
		return selectedVersion;
	}

	@Override
	public int[] getCipherSuites()
	{
		return cipherSuites;
	}

	@Override
	public int getSelectedCipherSuite()
	{
		return selectedCipherSuite;
	}

	@Override
	public int[] getEllipticCurves()
	{
		return ellipticCurves;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Vector getSupportedSignatureAlgorithms()
	{
		return supportedSignatureAlgorithms;
	}

	@Override
	public short getAlertLevelReceived()
	{
		return alertLevelReceived;
	}

	@Override
	public boolean isClientAuthentication()
	{
		return clientAuthentication;
	}
	
	
	@Override
	public AsymmetricKeyParameter getClientPrivateKey()
	{
		return clientPrivateKey;
	}

	@Override
	public Certificate getClientCertificate()
	{
		return clientCertificate;
	}

	@Override
	public byte[][] getPSK()
	{
		return psk;
	}

	public void setVersion(ProtocolVersion version)
	{
		this.version = version;
	}

	public void setSelectedVersion(ProtocolVersion selectedVersion)
	{
		this.selectedVersion = selectedVersion;
	}

	public void setCipherSuites(int[] cipherSuites)
	{
		this.cipherSuites = cipherSuites;
	}

	public void setSelectedCipherSuite(int selectedCipherSuite)
	{
		this.selectedCipherSuite = selectedCipherSuite;
	}

	public void setEllipticCurves(int[] ellipticCurves)
	{
		this.ellipticCurves = ellipticCurves;
	}

	@SuppressWarnings("rawtypes")
	public void setSupportedSignatureAlgorithms(Vector supportedSignatureAlgorithms)
	{
		this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
	}

	public void setAlertLevelReceived(short alertLevelReceived)
	{
		this.alertLevelReceived = alertLevelReceived;
	}

	public void setClientAuthentication(boolean clientAuthentication)
	{
		this.clientAuthentication = clientAuthentication;
	}

	public void setClientPrivateKey(AsymmetricKeyParameter clientPrivateKey)
	{
		this.clientPrivateKey = clientPrivateKey;
	}

	public void setClientCertificate(Certificate clientCertificate)
	{
		this.clientCertificate = clientCertificate;
	}
	
	@Override
	public boolean getEncryptThenMACExtension()
	{
		return this.encryptThenMACExtension;
	}

	@Override
	public boolean getEncryptThenMACExtensionEnabled()
	{
		return this.encryptThenMACExtensionEnabled;
	}

	public void setEncryptThenMACExtension(boolean encryptThenMACExtension)
	{
		this.encryptThenMACExtension = encryptThenMACExtension;
	}

	public void setEncryptThenMACExtensionEnabled(boolean encryptThenMACExtensionEnabled)
	{
		this.encryptThenMACExtensionEnabled = encryptThenMACExtensionEnabled;
	}
	
	public void setPSK(byte[] pskId, byte[] pskKey)
	{
		this.psk = new byte[][] { pskId, pskKey };
	}

}
