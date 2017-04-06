package com.secunet.eidserver.testbed.common.interfaces.entities;

public interface LogMessage
{

	public String getId();

	public void setId(String logid);

	public String getMessage();

	public void setMessage(String message);

	public boolean getSuccess();

	public void setSuccess(boolean success);

	public String getTestStepName();

	public void setTestStepName(String name);
}
