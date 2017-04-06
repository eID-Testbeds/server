package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;

public class TestCaseStepTestingPojo implements Serializable, TestCaseStep
{
	private static final long serialVersionUID = 1296513915449273642L;

	private String name;

	private boolean inbound;

	private String message;

	private boolean optional;

	private String xsd;

	private String suffix;

	private TargetInterfaceType target;

	public TestCaseStepTestingPojo()
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