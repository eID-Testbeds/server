package com.secunet.eidserver.testbed.common.interfaces.entities;

import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;

public interface TestCaseStep
{

	public String getName();

	public void setName(String name);

	public boolean getInbound();

	public void setInbound(boolean inbound);

	public boolean isDefault();

	public void setDefault(boolean isDefault);

	public String getMessage();

	public void setMessage(String message);

	public boolean isOptional();

	public void setOptional(boolean optional);

	public void setSuffix(String newSuffix);

	public String getSuffix();

	public String getXsd();

	public void setXsd(String xsd);

	public TargetInterfaceType getTarget();

	public void setTarget(TargetInterfaceType target);
}
