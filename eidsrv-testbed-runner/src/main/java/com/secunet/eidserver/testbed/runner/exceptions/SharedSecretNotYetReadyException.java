package com.secunet.eidserver.testbed.runner.exceptions;

public class SharedSecretNotYetReadyException extends RuntimeException
{
	private static final long serialVersionUID = -388542116124939407L;

	public SharedSecretNotYetReadyException()
	{
		super("The shared secret has not been computed yet or it is not available. Validate the singnature received in EAC2 first.");
	}

}
