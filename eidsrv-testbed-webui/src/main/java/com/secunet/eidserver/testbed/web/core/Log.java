package com.secunet.eidserver.testbed.web.core;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Logging Util that wrappes the slf4jLogger Factory.
 *
 */
public class Log
{
	private static final Logger slf4jLogger = LoggerFactory.getLogger(Log.class);


	/**
	 * @param c
	 *            must not be null
	 * @return the return value of {@link LoggerFactory#getLogger(Class)}
	 */
	public static Logger log(Class<?> c)
	{
		return LoggerFactory.getLogger(c);
	}

	/**
	 * @param o
	 *            if null, delegates to {@link #log()}
	 * @return the logger
	 */
	public static Logger log(Object o)
	{
		if (o == null)
			return log();
		else
			return LoggerFactory.getLogger(o.getClass());
	}

	public static Logger log()
	{
		return slf4jLogger;
	}

	/**
	 * @param cause
	 *            must not be null
	 * @return stacktrace
	 */
	public static String getStackTrace(Throwable cause)
	{
		try (Writer result = new StringWriter(); PrintWriter printWriter = new PrintWriter(result))
		{
			cause.printStackTrace(printWriter);

			return result.toString();
		}
		catch (IOException e)
		{
			log().error("failed to create stacktrace {}", e);
			return "";
		}
	}
}
