package com.secunet.eidserver.testbed.services;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
*
*/
@WebService
public interface RunService
{
	@WebMethod
	public String run(final String testId, final String profileId, final String token) throws Exception;

	@WebMethod
	public String runAll(final String profileId, final String token) throws Exception;
}
