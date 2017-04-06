package com.secunet.eidserver.testbed.tlsclient;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.crypto.tls.TlsPSKIdentity;

public class TLSPSKClientIdentity implements TlsPSKIdentity
{

	private static final Logger CLASS_LOGGER = Logger.getLogger(TLSPSKClientIdentity.class.getName());

	private final byte[] PSKIdentity;
	private final byte[] PreSharedKey;

	// Initialize the class with ClientIdentity and PSK
	TLSPSKClientIdentity(byte[] Identity, byte[] PSK)
	{
		PSKIdentity = Identity;
		PreSharedKey = PSK;
	}

	// Method to receive the PSK Identity Hint - is currently not set
	@Override
	public void notifyIdentityHint(final byte[] psk_identity_hint)
	{
		CLASS_LOGGER.log(Level.FINEST, "notifyIdentityHint() called");
	}

	// Method to receive the PSK Identity
	@Override
	public byte[] getPSKIdentity()
	{
		CLASS_LOGGER.log(Level.FINEST, "getPSKIdentity() called");
		return PSKIdentity;
	}

	// Method to receive the PSK
	@Override
	public byte[] getPSK()
	{
		CLASS_LOGGER.log(Level.FINEST, "getPSK() called");
		return PreSharedKey;
	}

	// Method to skip the PSK Identity Hint - no further action necessary
	@Override
	public void skipIdentityHint()
	{
		CLASS_LOGGER.log(Level.FINEST, "skipIdentityHint() called");
	}

}
