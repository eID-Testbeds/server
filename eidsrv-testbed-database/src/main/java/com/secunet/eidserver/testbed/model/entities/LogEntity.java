package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

@Entity
@Table(name = "LOG")
@NamedQuery(name = "LogEntity.findAll", query = "SELECT t FROM LogEntity t")
public class LogEntity extends BaseEntity implements Serializable, Log
{
	private static final long serialVersionUID = 1L;

	@Column(name = "RUN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date runDate;

	@Column(name = "TEST_CASE")
	private String testCase;

	@Column(name = "SUCCESS")
	private boolean success;

	@JoinColumn(name = "LOG_MESSAGE")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = LogMessageEntity.class)
	private List<LogMessage> logMessages = new ArrayList<>();

	public LogEntity()
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
		Collections.sort(this.logMessages, new Comparator<LogMessage>()
		{
			public int compare(LogMessage o1, LogMessage o2)
			{
				return (int)(o1.getTestStepNumber().longValue() - o2.getTestStepNumber().longValue());
			}
		});

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * ((logMessages == null) ? 0 : logMessages.hashCode());
		result = prime * result + ((runDate == null) ? 0 : runDate.hashCode());
		result = prime * result + ((testCase == null) ? 0 : testCase.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEntity other = (LogEntity) obj;
		if (logMessages == null)
		{
			if (other.logMessages != null)
				return false;
		}
		else if (!logMessages.equals(other.logMessages))
			return false;
		if (runDate == null)
		{
			if (other.runDate != null)
				return false;
		}
		else if (!runDate.equals(other.runDate))
			return false;
		if (testCase == null)
		{
			if (other.testCase != null)
				return false;
		}
		else if (!testCase.equals(other.testCase))
			return false;
		return true;
	}

}
