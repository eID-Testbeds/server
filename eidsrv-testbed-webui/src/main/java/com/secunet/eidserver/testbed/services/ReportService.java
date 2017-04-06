package com.secunet.eidserver.testbed.services;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 */
@WebService
public interface ReportService
{
	@WebMethod
	public String createReport(final String profileId, final int type, final String token);

	@WebMethod
	public String createSpecificReport(final String testCaseName, final String profileId, final int type, final String token);

	@WebMethod
	public String dumpLog(final String profileId, final String token);
}
