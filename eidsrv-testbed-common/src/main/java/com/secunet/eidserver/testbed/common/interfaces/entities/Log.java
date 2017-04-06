package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.Date;
import java.util.List;

public interface Log
{

	public String getId();

	public void setId(String id);

	public Date getRunDate();

	public void setRunDate(Date runDate);

	public List<LogMessage> getLogMessages();

	public void setLogMessages(List<LogMessage> logMessages);

	public String getTestCase();

	public void setTestCase(String testCase);

	public void setSuccess(boolean success);

	public boolean isSuccess();
}
