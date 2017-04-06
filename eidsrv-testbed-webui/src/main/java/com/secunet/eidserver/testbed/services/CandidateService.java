package com.secunet.eidserver.testbed.services;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;

import com.secunet.eidserver.testbed.common.exceptions.CandidateException;

/**
 *
 */
@WebService
public interface CandidateService
{
	@WebMethod
	public String createTestCandidate(final String candidateData, final String token) throws JAXBException, InterruptedException, ExecutionException, CandidateException, MalformedURLException;

	@WebMethod
	public void deleteTestCandidate(final String id, final String token) throws CandidateException;
}
