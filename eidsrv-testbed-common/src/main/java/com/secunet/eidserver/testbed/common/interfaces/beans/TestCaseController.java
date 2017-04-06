package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.exceptions.InvalidObjectException;
import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

@Local
public interface TestCaseController
{
	/**
	 * Creates a copy of a default test case. Note that if the test case is already a copy, this method will throw an error
	 * 
	 * @param candidateId
	 * @param defaultTestCaseName
	 * @param suffix
	 * @param replaceCertificateBaseNames
	 * @param certificateBaseNames
	 * @param replaceTestCaseSteps
	 * @param testCaseStepNamesToReplace
	 * @param replacingTestCaseStepXml
	 * @return
	 */
	public CopyTestCase createTestCaseCopy(String candidateId, String defaultTestCaseName, String suffix, boolean replaceCertificateBaseNames, List<String> certificateBaseNames,
			boolean replaceTestCaseSteps, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml);


	public String editTestCaseCopy(String testCaseCopyName, boolean replaceCertificateBaseNames, List<String> certificateBaseNames, boolean replaceTestCaseSteps,
			List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml);

	/**
	 * Persist the given test case copy. This either overrides an existing test case copy or creates a new one, depending on whether a copy or a default test case has been selected as source.
	 * 
	 * @param copy
	 * @param sourceDefault
	 */
	public void persistTestCaseCopy(CopyTestCase copy, boolean sourceDefault);

	/**
	 * @param candidate
	 *            the candidate to build get the testmodule root element with its tree
	 * @return the root node of the test module tree
	 * @throws InvalidObjectException
	 */
	public TestModule getRootTestModule(TestCandidate candidate) throws InvalidObjectException; // TODO exceptions

	/**
	 * @return never null, could be empty
	 */
	public Set<TestCaseStep> getAllTestCaseSteps() throws EJBException;// TODO exceptions

	/**
	 * Persist a test case step copy
	 * 
	 * @param newOrEditredTestCaseStep
	 *            {@link TestCaseStep} The test case step which shall be persisted
	 * @param oldSuffix
	 *            {@link String}
	 * @throws Exception
	 */
	public void persistTestCaseStep(TestCaseStep newOrEditredTestCaseStep, String oldSuffix) throws Exception; // TODO exceptions

	/**
	 * @return never null, could be empty
	 */
	public Set<TestCase> getAllTestCases() throws Exception; // TODO exceptions

	/**
	 * 
	 */
	public void generateDefaultTestcases() throws Exception;

}
