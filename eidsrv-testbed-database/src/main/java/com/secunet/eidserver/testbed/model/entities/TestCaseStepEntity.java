package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;

@Entity
@Table(name = "TEST_CASE_STEPS")
@NamedQuery(name = "TestCaseStepEntity.findAll", query = "SELECT t FROM TestCaseStepEntity t")
public class TestCaseStepEntity implements Serializable, TestCaseStep
{
	private static final long serialVersionUID = 1296513915449273642L;

	@Id
	@Column(name = "NAME")
	private String name;

	private boolean inbound;

	private String message;

	private boolean optional;

	private String xsd;

	private String suffix;

	private TargetInterfaceType target;

	public TestCaseStepEntity()
	{
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	private boolean isDefault;

	@Override
	public boolean getInbound()
	{
		return this.inbound;
	}

	@Override
	public void setInbound(boolean inbound)
	{
		this.inbound = inbound;
	}

	@Override
	public boolean isDefault()
	{
		return isDefault;
	}

	@Override
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	@Override
	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public boolean isOptional()
	{
		return optional;
	}

	@Override
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	/**
	 * @return the xsd
	 */
	@Override
	public String getXsd()
	{
		return xsd;
	}

	/**
	 * @param xsd
	 *            the xsd to set
	 */
	@Override
	public void setXsd(String xsd)
	{
		this.xsd = xsd;
	}

	@Override
	public void setSuffix(String newSuffix)
	{
		this.suffix = newSuffix;
	}

	@Override
	public String getSuffix()
	{
		return suffix;
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
		result = prime * result + (inbound ? 1231 : 1237);
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (optional ? 1231 : 1237);
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((xsd == null) ? 0 : xsd.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		String result = (isDefault) ? "Default" : "Copy";
		result += (inbound) ? " inbound" : " outbound";
		result += " testcase " + name;
		result += (optional) ? " (optional)" : " (mandatory)";
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
		TestCaseStepEntity other = (TestCaseStepEntity) obj;
		if (inbound != other.inbound)
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (optional != other.optional)
			return false;
		if (suffix == null)
		{
			if (other.suffix != null)
				return false;
		}
		else if (!suffix.equals(other.suffix))
			return false;
		if (xsd == null)
		{
			if (other.xsd != null)
				return false;
		}
		else if (!xsd.equals(other.xsd))
			return false;
		return true;
	}

	@Override
	public TargetInterfaceType getTarget()
	{
		return target;
	}

	@Override
	public void setTarget(TargetInterfaceType target)
	{
		this.target = target;
	}

}