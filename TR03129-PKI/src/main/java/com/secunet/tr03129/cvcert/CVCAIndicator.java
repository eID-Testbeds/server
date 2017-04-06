package com.secunet.tr03129.cvcert;

import java.security.InvalidParameterException;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum CVCAIndicator
{

	CA_1, CA_2;

	static Logger log = LogManager.getLogger(CVCAIndicator.class);

	public static CVCAIndicator getCVCAIndicator(final String contextPath) throws InvalidParameterException
	{
		if (contextPath == null)
		{
			throw new InvalidParameterException("The context path was not provided");
		}

		log.info("Context path is: " + contextPath);

		if (contextPath.toUpperCase().contains("CA1"))
			return CA_1;
		else if (contextPath.toUpperCase().contains("CA2"))
			return CA_2;
		else
			throw new InvalidParameterException("An invalid context path was provided. The path must either contain the string 'CA1' or 'CA2'. The context was: " + contextPath);
	}

	public static CVCAIndicator getCVCAIndicator(final ServletContext context) throws InvalidParameterException
	{
		if (context == null)
		{
			throw new InvalidParameterException("The context was not provided");
		}
		return getCVCAIndicator(context.getContextPath());
	}
}
