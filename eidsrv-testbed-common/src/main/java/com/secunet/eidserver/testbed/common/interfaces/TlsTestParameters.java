package com.secunet.eidserver.testbed.common.interfaces;

import java.util.Vector;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.NamedCurve;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.crypto.tls.TlsUtils;

public interface TlsTestParameters
{
	// public Set<BitLength> getBitLengthsRsa();

	// public Set<BitLength> getBitLengthsDsa();

	// public Set<BitLength> getBitLengthsDhe();


	/**
	 * The test client offers this TLS version to the server.
	 * 
	 * @return an object from {@link ProtocolVersion}
	 */
	public ProtocolVersion getVersion();

	/**
	 * Input for a check, whether a server selected the correct TLS version
	 * 
	 * @return an object from {@link ProtocolVersion}, or null to skip this check
	 */
	public ProtocolVersion getSelectedVersion();

	/**
	 * The test client offers these cipher suites to the server.
	 * 
	 * @return an array of int from {@link CipherSuite}
	 */
	public int[] getCipherSuites();

	/**
	 * Input for a check, whether a server selected the correct cipher suite
	 * 
	 * @return an int from {@link CipherSuite}, or -1 to skip this check
	 */
	public int getSelectedCipherSuite();

	/**
	 * The test client offers these elliptic curves to the server.
	 * 
	 * @return an array of int from {@link NamedCurve}
	 */
	public int[] getEllipticCurves();

	/**
	 * The test client offers these signature algorithms to the server.
	 * 
	 * @return a {@link Vector} of {@link SignatureAndHashAlgorithm}
	 * @see TlsUtils#getDefaultSupportedSignatureAlgorithms()
	 */
	@SuppressWarnings("rawtypes")
	public Vector getSupportedSignatureAlgorithms();

	/**
	 * Input for a check, whether a server sent the correct alert level
	 * 
	 * @return a short from {@link AlertLevel}, 0 for no alert (= completed handshake) or -1 to skip this check
	 */
	public short getAlertLevelReceived();

	public boolean isClientAuthentication();

	public AsymmetricKeyParameter getClientPrivateKey();

	public Certificate getClientCertificate();
	
	/**
	 * Input for a check, whether a client uses EncryptThenMACExtension
	 * 
	 * @return true if EncryptThenMACExtension is enabled; else false
	 */
	public boolean getEncryptThenMACExtension();

	/**
	 * Input for a check, whether a server enabled EncryptThenMACExtension
	 * 
	 * @return true if EncryptThenMACExtension is enabled; else false
	 */
	public boolean getEncryptThenMACExtensionEnabled();

	/**
	 * The test client uses these PSK parameters to establish a PSK connection to the server.
	 * 
	 * @return two arrays of byte with array[0] containing PSK_ID and array[1] containing PSK_KEY, or null for no PSK
	 */
	public byte[][] getPSK();
	
}
