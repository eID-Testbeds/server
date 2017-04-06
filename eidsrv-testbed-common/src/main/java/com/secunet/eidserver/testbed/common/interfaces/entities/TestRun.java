package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.Date;
import java.util.Set;

public interface TestRun
{

	/**
	 * Get the Logs created for this test run
	 * 
	 * @return
	 */
	public Set<Log> getLogs();

	/**
	 * Set the logs for a given test run.
	 */
	public void setLogs(Set<Log> logs);

	/**
	 * Get the start date of the given test run. This date contains the exact time the service was called in order to run the test cases.
	 * 
	 * @return
	 */
	public Date getStartDate();

	/**
	 * Set the start date of the given test run. This date contains the exact time the service was called in order to run the test cases.
	 */
	public void setStartDate(Date date);

	/**
	 * Get the end date of the given test run. This date contains the exact time the selected test run has finished.
	 * 
	 * @return
	 */
	public Date getEndDate();

	/**
	 * Set the end date of the given test run. This date contains the exact time the selected test run has finished.
	 */
	public void setEndDate(Date date);


}
