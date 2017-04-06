/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.services;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.CandidateException;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

/**
 *
 */
@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.CandidateService")
public class CandidateServiceImpl implements CandidateService
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private CandidateController candidateController;

	@EJB
	private AuthenticationController authenticationController;

	@Override
	public String createTestCandidate(final String candidateData, final String token) throws JAXBException, InterruptedException, ExecutionException, CandidateException, MalformedURLException
	{
		logger.trace(String.format("CreateTestProfile %s >>", candidateData));

		final Token authToken = authenticationController.findToken(token);

		if (Role.PROFILE_CREATOR != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		final Future<TestCandidate> testCandidate = candidateController.createCandidate(candidateData);
		TestCandidate candidate = testCandidate.get();
		candidateController.saveCandidate(candidate);
		Gson gson = new Gson();
		return gson.toJson(candidate);

	}

	@Override
	public void deleteTestCandidate(final String id, final String token) throws CandidateException
	{
		logger.trace(String.format("DeleteTestCandidate %s >>", id));

		final Token authToken = authenticationController.findToken(token);

		if (Role.PROFILE_CREATOR != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		candidateController.deleteCandidateById(id);
	}
}
