package com.secunet.eidserver.testbed.common.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;

public class TestRunModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected String uuid = UUID.randomUUID().toString();

	protected TestCandidate candidate;
	protected Date executionStarted = null;
	protected Date finished = null;

	protected Map<TestCase, Integer> progress;

	protected Set<Log> logs;


	public TestRunModel()
	{
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRunModel other = (TestRunModel) obj;
		if (uuid == null)
		{
			if (other.uuid != null)
				return false;
		}
		else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	public TestCandidate getCandidate()
	{
		return candidate;
	}

	public void setCandidate(TestCandidate candidate)
	{
		this.candidate = candidate;
	}

	public Date getExecutionStarted()
	{
		return executionStarted;
	}

	public void setExecutionStarted(Date executionStarted)
	{
		this.executionStarted = executionStarted;
	}

	public Date getFinished()
	{
		return finished;
	}

	public void setFinished(Date finished)
	{
		this.finished = finished;
	}

	public Map<TestCase, Integer> getProgress()
	{
		return progress;
	}

	public void setProgress(Map<TestCase, Integer> progress)
	{
		this.progress = progress;
	}

	/**
	 * @return the logs
	 */
	public Set<Log> getLogs()
	{
		return logs;
	}

	/**
	 * @param logs
	 *            the logs to set
	 */
	public void setLogs(Set<Log> logs)
	{
		this.logs = logs;
	}

}