package com.secunet.eidserver.testbed.common.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

public class TestReportModel implements Serializable, Comparable<TestReportModel>
{
	private static final long serialVersionUID = 1L;

	protected String uuid = UUID.randomUUID().toString();

	protected TestCandidate candidate;
	protected Date executionStarted = null;
	protected Date finished = null;

	protected Map<String, List<LogMessage>> successMapping;


	public TestReportModel()
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
		TestReportModel other = (TestReportModel) obj;
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

	public Map<String, List<LogMessage>> getSuccessMapping()
	{
		return successMapping;
	}

	public void setSuccessMapping(Map<String, List<LogMessage>> successMapping)
	{
		this.successMapping = successMapping;
	}

	@Override
	public int compareTo(TestReportModel o)
	{
		return this.finished.compareTo(o.finished);
	}

}