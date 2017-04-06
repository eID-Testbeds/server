package com.secunet.eidserver.testbed.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import com.secunet.eidserver.testbed.common.classes.TestReportModel;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.exceptions.LogdataNotFoundException;
import com.secunet.eidserver.testbed.common.exceptions.ProfileNotFoundException;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportController;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.ReportDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;

@Stateless
public class ReportControllerBean implements ReportController
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private ReportGenerator generator;

	@EJB
	private ReportDAO reportDAO;

	@EJB
	private TestCandidateDAO testProfileDAO;

	/**
	 * Returns all reports for the given ID
	 * 
	 * @param profileId
	 * @return
	 */
	@Override
	public String getAllLogs(final String profileId)
	{
		// find profile
		TestCandidate profile = testProfileDAO.findById(profileId);
		String result = "";
		// input data
		if (profile != null)
		{
			Set<TestRun> testRuns = profile.getTestRuns();
			for (TestRun run : testRuns)
			{
				for (Log l : run.getLogs())
				{
					result += l;
					result += (System.getProperty("line.separator") + "##########################################################################################################"
							+ System.getProperty("line.separator"));
				}
			}
			if (result.length() == 0)
			{
				logger.debug("No logs found for profile id " + profileId);
			}
		}
		else
		{
			result = "Profile with id " + profileId + " has not been found";
		}
		// remove all control characters beside LF
		result = result.replaceAll("[\u0000-\u0009]", "");
		result = result.replaceAll("[\u000B-\u001F]", "");
		return result;
	}

	/**
	 * Create a report based on the entire log data available in the
	 * {@link TestCandidate} with the given ID.
	 * 
	 * @param candidateId
	 * @param type
	 * @return
	 */
	@Override
	public String createReport(final String candidateId, final int type)
	{
		// find profile
		TestCandidate candidate = testProfileDAO.findById(candidateId);
		if (candidate != null)
		{
			// input data
			Set<TestRun> testRuns = candidate.getTestRuns();
			if (testRuns != null && testRuns.size() > 0)
			{
				for (TestRun run : testRuns)
				{
					ReportType outType = ReportType.createUsingValue(type);
					// create the report
					byte[] report = null;
					report = generator.generateReport(candidate, run.getLogs(), outType);
					if (report != null)
					{
						Report rep = reportDAO.createNew();
						rep.setReportData(report);
						rep.setReportType(outType);
						// add to profile and update it
						candidate.getReports().add(rep);
						reportDAO.persist(rep);
						testProfileDAO.update(candidate);
						return Base64.toBase64String(report);
					}
					else
					{
						logger.warn("Failed to create report.");
					}
				}
			}
			else
			{
				throw new LogdataNotFoundException(candidateId);
			}
		}
		else
		{
			throw new ProfileNotFoundException(candidateId);
		}
		return GeneralConstants.ELEMENT_NOT_FOUND;
	}

	@Override
	public List<TestReportModel> getTestReportModels(TestCandidate candidate)
	{
		List<TestReportModel> models = new ArrayList<>();
		for (TestRun run : candidate.getTestRuns())
		{
			TestReportModel model = new TestReportModel();
			model.setCandidate(candidate);
			model.setExecutionStarted(run.getStartDate());
			model.setFinished(run.getEndDate());

			Map<String, List<LogMessage>> success = new HashMap<>();
			for (Log log : run.getLogs())
			{
				List<LogMessage> logs = new ArrayList<>();
				for (LogMessage message : log.getLogMessages())
				{
					logs.add(message);
				}
				success.put(log.getTestCase(), logs);
			}
			model.setSuccessMapping(success);
			models.add(model);
		}
		Collections.sort(models);
		return models;
	}
}
