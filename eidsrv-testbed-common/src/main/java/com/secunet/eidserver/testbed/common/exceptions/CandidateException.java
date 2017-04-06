package com.secunet.eidserver.testbed.common.exceptions;

import java.security.InvalidParameterException;

public class CandidateException extends InvalidParameterException
{
	private static final long serialVersionUID = 3440269502621226472L;

	public CandidateException()
	{
		super();
	}

	public CandidateException(final String message)
	{
		super(message);
	}
}
