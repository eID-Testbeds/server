package com.secunet.eidserver.testbed.runner.exceptions;

public class EphemeralKeyNotFoundException extends IllegalArgumentException
{
	private static final long serialVersionUID = 5689783793148572160L;

	public EphemeralKeyNotFoundException()
	{
		super("The server ephemeral key has not been found.");
	}
}
