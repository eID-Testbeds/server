package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.List;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.classes.TestReportModel;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

@Local
public interface ReportController
{
	/**
	 * Returns all reports for the given ID
	 * 
	 * @param canidateId
	 * @return
	 */
	public String getAllLogs(final String canidateId);

	/**
	 * Create a report based on the entire log data available in the
	 * {@link TestCandidate} with the given ID.
	 * 
	 * @param profileId
	 * @param type
	 * @return
	 */
	public String createReport(final String candidateId, final int type);


	public List<TestReportModel> getTestReportModels(TestCandidate candidate);


}
