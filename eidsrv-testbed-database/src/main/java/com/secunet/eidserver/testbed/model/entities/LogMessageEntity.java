package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

@Entity
@Table(name = "LOG_MESSAGES")
@NamedQuery(name = "LogMessageEntity.findAll", query = "SELECT l FROM LogMessageEntity l")
public class LogMessageEntity extends BaseEntity implements Serializable, LogMessage
{
	private static final long serialVersionUID = 1L;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "SUCCESS")
	private boolean success;

	@Column(name = "TEST_STEP_NAME")
	private String name;

	public LogMessageEntity()
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
		return name;
	}

	@Override
	public void setTestStepName(String name)
	{
		this.name = name;
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
		int result = prime * ((message == null) ? 0 : message.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		LogMessageEntity other = (LogMessageEntity) obj;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (success != other.success)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}