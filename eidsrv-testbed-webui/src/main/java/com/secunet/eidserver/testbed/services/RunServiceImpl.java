package com.secunet.eidserver.testbed.services;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.RunController;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

/**
 * The ReportServiceImpl provides the SOAP interface for running tests.
 */
@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.RunService")
public class RunServiceImpl implements RunService
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private RunController runController;

	@EJB
	private AuthenticationController authenticationController;

	public RunServiceImpl()
	{

	}

	@Override
	public String run(final String testName, final String profileId, final String token) throws Exception
	{
		// check if the token allows the creation of a report
//		final Token authToken = authenticationController.findToken(token);
//		if (Role.TESTER != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
//			throw new InvalidRightsException(authToken.getUser().getName());

		final String result = String.format("Run test with name %s in profile: %s", testName, profileId);
		logger.debug(result);

		List<LogMessage> logs = runController.run(testName, profileId);
		Gson gson = new Gson();
		String jsonLogs = gson.toJson(logs);
		return jsonLogs;
	}

	@Override
	public String runAll(final String profileId, final String token) throws Exception
	{
		// check if the token allows the creation of a report
//		final Token authToken = authenticationController.findToken(token);
//		if (Role.TESTER != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
//			throw new InvalidRightsException(authToken.getUser().getName());

		final String result = String.format("Run all test in profile: %s", profileId);
		logger.debug(result);

		Map<TestCase, List<LogMessage>> logs = runController.runAll(profileId);

		Gson gson = new Gson();
		String jsonLogs = gson.toJson(logs);
		return jsonLogs;
	}
}