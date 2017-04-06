package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

public class LogTestingPojo extends BaseTestingPojo implements Serializable, Log
{
	private static final long serialVersionUID = 1L;

	private Date runDate;

	private String testCase;

	private List<LogMessage> logMessages = new ArrayList<>();

	private boolean success;

	public LogTestingPojo()
	{
	}

	@Override
	public Date getRunDate()
	{
		return runDate;
	}

	@Override
	public void setRunDate(Date runDate)
	{
		this.runDate = runDate;
	}

	@Override
	public List<LogMessage> getLogMessages()
	{
		return this.logMessages;
	}

	@Override
	public void setLogMessages(List<LogMessage> logMessages)
	{
		this.logMessages = logMessages;
	}

	@Override
	public String getTestCase()
	{
		return testCase;
	}

	@Override
	public void setTestCase(String testCase)
	{
		this.testCase = testCase;
	}

	@Override
	public String toString()
	{
		String rep = "Log data for testcase " + getTestCase() + ", run date " + runDate.toString() + System.getProperty("line.separator");
		List<String> failedSteps = new ArrayList<String>();
		for (LogMessage message : logMessages)
		{
			if (!message.getSuccess())
			{
				failedSteps.add(message.getTestStepName());
			}
			rep += message.toString() + System.getProperty("line.separator");
			rep += "------------------------------------------------------------------------------" + System.getProperty("line.separator");
		}
		if (failedSteps.size() == 0)
		{
			rep += "Test was successful";
		}
		else
		{
			String fail = "";
			for (String step : failedSteps)
			{
				fail += (step + ", ");
			}
			rep += ("Test failed due to error in test step(s) " + fail.substring(0, fail.length() - 2));
		}
		return rep;
	}

	@Override
	public boolean isSuccess()
	{
		return success;
	}

	@Override
	public void setSuccess(boolean success)
	{
		this.success = success;
	}

}
