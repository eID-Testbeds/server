package com.secunet.eidserver.testbed.services;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportController;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

/**
 * The ReportServiceImpl provides the SOAP interface for the reporting
 * functionality reports.
 */
@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.ReportService")
public class ReportServiceImpl implements ReportService
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private AuthenticationController authenticationController;

	@EJB
	private ReportController reportController;

	@Override
	public String createReport(final String profileId, final int type, String token)
	{
		// check if the token allows the creation of a report
		final Token authToken = authenticationController.findToken(token);
		if (Role.PROFILE_CREATOR != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		final String result = String.format("CreateTestReport for pofileId: %d", profileId);
		logger.trace(result);
		reportController.createReport(profileId, type);
		return result;
	}

	@Override
	public String createSpecificReport(String testCaseName, String profileId, final int type, String token)
	{
		// // check if the token allows the creation of a report
		// final Token authToken = authenticationController.findToken(token);
		// if (Role.PROFILE_CREATOR != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
		// throw new InvalidRightsException(authToken.getUser().getName());
		//
		// final String result = String.format("CreateTestReport for pofileId: %d", profileId);
		// logger.trace(result);
		// reportController.createReport(testCaseName, profileId, type);
		// return result;
		return "Not implemented yet";
	}

	@Override
	public String dumpLog(String profileId, String token)
	{
		// check if the token allows the creation of a report
		final Token authToken = authenticationController.findToken(token);
		if (Role.TESTER != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return reportController.getAllLogs(profileId);
	}
}
