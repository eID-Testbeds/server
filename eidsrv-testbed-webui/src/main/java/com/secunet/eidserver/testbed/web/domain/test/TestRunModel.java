package com.secunet.eidserver.testbed.web.domain.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

// TODO REPLACE THIS CLASS WITH BACKEND
class TestRunModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	String uuid = UUID.randomUUID().toString();
	TestCandidate candidate;
	Date executionStarted = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(60));
	Date finished = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));
	Map<TestCase, Integer> progress;
	Map<TestCase, Map<TestCaseStep, Boolean>> successMapping;


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

	public Map<TestCase, Map<TestCaseStep, Boolean>> getSuccessMapping()
	{
		return successMapping;
	}

	public void setSuccessMapping(Map<TestCase, Map<TestCaseStep, Boolean>> successMapping)
	{
		this.successMapping = successMapping;
	}

}