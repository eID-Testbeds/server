package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;

public class TestRunTestingPojo extends BaseTestingPojo implements Serializable, TestRun
{
	private static final long serialVersionUID = 1L;

	private Date runDate;

	private Date endDate;

	private Set<Log> logs = new HashSet<>();

	public TestRunTestingPojo()
	{
	}

	@Override
	public Set<Log> getLogs()
	{
		return logs;
	}

	@Override
	public void setLogs(Set<Log> logs)
	{
		this.logs = logs;
	}

	@Override
	public Date getStartDate()
	{
		return runDate;
	}

	@Override
	public void setStartDate(Date date)
	{
		this.runDate = date;
	}

	@Override
	public Date getEndDate()
	{
		return endDate;
	}

	@Override
	public void setEndDate(Date date)
	{
		this.endDate = date;
	}

}
