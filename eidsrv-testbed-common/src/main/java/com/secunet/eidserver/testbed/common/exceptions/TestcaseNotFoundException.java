package com.secunet.eidserver.testbed.common.exceptions;

public class TestcaseNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 7028502079972459821L;

	public TestcaseNotFoundException(String message)
	{
		super(message);
	}

}
