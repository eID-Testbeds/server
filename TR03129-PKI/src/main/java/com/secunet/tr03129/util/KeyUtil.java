package com.secunet.tr03129.util;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.testbedutils.cvc.cvcertificate.IPrivateKeySource;
import com.secunet.testbedutils.cvc.cvcertificate.PrivateKeySource;
import com.secunet.tr03129.CertType;

public class KeyUtil
{
	public static IPrivateKeySource getPrivateKeySource(final CertType type) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] raw = type.getRaw();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(raw);
		KeyFactory fact = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
		PrivateKey pKey = fact.generatePrivate(spec);

		return new PrivateKeySource(pKey);
	}
}
