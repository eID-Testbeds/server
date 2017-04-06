/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.services;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

/**
 * 
 */
@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.TestCaseService")
public class TestCaseServiceImpl implements TestCaseService
{

	// private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private AuthenticationController authenticationController;

	@EJB
	private TestCaseController tcc;

	@Override
	public String createCopyNewCertficates(String candidateId, String defaultTestCaseName, String suffix, List<String> certificateBaseNames, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() && Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.createTestCaseCopy(candidateId, defaultTestCaseName, suffix, true, certificateBaseNames, false, null, null).getId();
	}

	@Override
	public String createCopyNewSteps(String candidateId, String defaultTestCaseName, String suffix, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() && Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.createTestCaseCopy(candidateId, defaultTestCaseName, suffix, false, null, true, testCaseStepNamesToReplace, replacingTestCaseStepXml).getId();
	}

	@Override
	public String createCopy(String candidateId, String defaultTestCaseName, String suffix, List<String> certificateBaseNames, List<String> testCaseStepNamesToReplace,
			List<String> replacingTestCaseStepXml, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() || Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.createTestCaseCopy(candidateId, defaultTestCaseName, suffix, true, certificateBaseNames, true, testCaseStepNamesToReplace, replacingTestCaseStepXml).getId();
	}

	@Override
	public String editCopyCertificates(String testCaseCopyName, List<String> certificateBaseNames, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() || Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.editTestCaseCopy(testCaseCopyName, true, certificateBaseNames, false, null, null);
	}

	@Override
	public String editCopySteps(String testCaseCopyName, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() || Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.editTestCaseCopy(testCaseCopyName, false, null, true, testCaseStepNamesToReplace, replacingTestCaseStepXml);
	}

	@Override
	public String editCopy(String testCaseCopyName, List<String> certificateBaseNames, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() || Role.TESTER != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return tcc.editTestCaseCopy(testCaseCopyName, true, certificateBaseNames, true, testCaseStepNamesToReplace, replacingTestCaseStepXml);
	}

}
