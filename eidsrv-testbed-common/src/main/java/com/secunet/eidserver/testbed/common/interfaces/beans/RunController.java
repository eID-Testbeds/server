package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.classes.TestRunModel;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;

@Local
public interface RunController
{

	public List<LogMessage> run(final String testName, final String canidateId) throws Exception;

	public Map<TestCase, List<LogMessage>> runAll(final String canidateId) throws Exception;

	public List<String> getTestCaseNames(final String canidateId);

	public void startTestCasesForProfile(String profileName, List<String> testCaseNames) throws Exception;

	/**
	 * 
	 * 
	 * @param model
	 * @throws Exception
	 */
	public void startTestrun(TestRunModel model) throws Exception;

	/**
	 * Return a {@link TestRunModel} for the given profile.
	 * 
	 * @param candidate The TestCandidate that is checked for running tests 
	 * @return an instance of TestRunModel or null, if no tests are running
	 */
	public TestRunModel getTestRunModel(TestCandidate candidate);

}
