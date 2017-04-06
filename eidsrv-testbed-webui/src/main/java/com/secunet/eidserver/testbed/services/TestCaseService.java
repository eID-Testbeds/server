/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.services;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *  
 */
@WebService
public interface TestCaseService
{

	@WebMethod
	String createCopyNewCertficates(String profileId, String defaultTestCaseName, String suffix, List<String> certificateBaseNames, String token);

	@WebMethod
	String createCopyNewSteps(String profileId, String defaultTestCaseName, String suffix, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token);

	@WebMethod
	String createCopy(String profileId, String defaultTestCaseName, String suffix, List<String> certificateBaseNames, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml,
			String token);

	@WebMethod
	String editCopyCertificates(String testCaseCopyName, List<String> certificateBaseNames, String token);

	@WebMethod
	String editCopySteps(String testCaseCopyName, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token);

	@WebMethod
	String editCopy(String testCaseCopyName, List<String> certificateBaseNames, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml, String token);


}

