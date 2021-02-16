package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;

import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

public class LogMessageTestingPojo extends BaseTestingPojo implements Serializable, LogMessage
{
	private static final long serialVersionUID = 1L;

	private String message;

	private boolean success;

	private String testStepName;

	private Long testStepNumber;

	public LogMessageTestingPojo()
	{
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

	@Override
	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public boolean getSuccess()
	{
		return this.success;
	}

	@Override
	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	@Override
	public String toString()
	{
		String message = (getSuccess()) ? "Successful" : "Unsuccessful";
		return (message + " step " + getTestStepName() + ": " + System.getProperty("line.separator") + getMessage());
	}

	@Override
	public String getTestStepName()
	{
		return testStepName;
	}

	@Override
	public void setTestStepName(String name)
	{
		this.testStepName = name;
	}

	@Override
	public Long getTestStepNumber()
	{
		return testStepNumber;
	}

	@Override
	public void setTestStepNumber(Long number)
	{
		this.testStepNumber = number;
	}

}