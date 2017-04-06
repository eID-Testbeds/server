package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;

@Entity
@Table(name = "TEST_RUN")
@NamedQuery(name = "TestRunEntity.findAll", query = "SELECT t FROM TestRunEntity t")
public class TestRunEntity extends BaseEntity implements Serializable, TestRun
{
	private static final long serialVersionUID = 7015384120496972328L;

	@JoinColumn(name = "LOG")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = LogEntity.class)
	private Set<Log> logs = new HashSet<>();

	@Column(name = "START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	public TestRunEntity()
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

	/**
	 * @return the date
	 */
	@Override
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param date
	 *            the start date to set
	 */
	@Override
	public void setStartDate(Date date)
	{
		this.startDate = date;
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
		int result = 1;
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((logs == null) ? 0 : logs.hashCode());
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
		TestRunEntity other = (TestRunEntity) obj;
		if (startDate == null)
		{
			if (other.startDate != null)
				return false;
		}
		else if (!startDate.equals(other.startDate))
			return false;
		if (endDate == null)
		{
			if (other.endDate != null)
				return false;
		}
		else if (!endDate.equals(other.endDate))
			return false;
		if (logs == null)
		{
			if (other.logs != null)
				return false;
		}
		else if (!logs.equals(other.logs))
			return false;
		return true;
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