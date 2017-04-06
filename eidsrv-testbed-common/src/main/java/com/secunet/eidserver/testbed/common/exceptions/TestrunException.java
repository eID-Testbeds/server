package com.secunet.eidserver.testbed.common.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

public class TestrunException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<LogMessage> logMessages;
	private Exception innerException;

	public TestrunException(List<LogMessage> messages, Exception e)
	{
		super(e);
		logMessages = messages;
		innerException = e;
	}

	public TestrunException(Exception e)
	{
		super(e);
		logMessages = new ArrayList<>();
	}

	/**
	 * Returns the {@link LogMessage}s generated until the exception was thrown.
	 * 
	 * @return
	 */
	public List<LogMessage> getLogMessagesUntilFailure()
	{
		return logMessages;
	}

	/**
	 * Returns the inner exception.
	 * 
	 * @return
	 */
	public Exception getInnerException()
	{
		return innerException;
	}

	/**
	 * Set the LogMessages
	 * 
	 * @param logMessages
	 *            {@link LogMessage} The list of log messages
	 */
	public void setMessages(List<LogMessage> logMessages)
	{
		this.logMessages = logMessages;
	}

	/**
	 * Set the inner Exception.
	 * 
	 * @param innerException
	 *            {@link Exception} The actual exception that caused the error.
	 */
	public void setInnerException(Exception innerException)
	{
		this.innerException = innerException;
	}

}
