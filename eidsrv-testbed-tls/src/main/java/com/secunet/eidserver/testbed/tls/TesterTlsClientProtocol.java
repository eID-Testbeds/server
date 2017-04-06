package com.secunet.eidserver.testbed.tls;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import org.bouncycastle.crypto.tls.TlsClientProtocol;

class TesterTlsClientProtocol extends TlsClientProtocol
{

	public TesterTlsClientProtocol(InputStream input, OutputStream output, SecureRandom secureRandom)
	{
		super(input, output, secureRandom);
	}

	@Override
	public boolean isClosed()
	{
		return super.isClosed();
	}

}
