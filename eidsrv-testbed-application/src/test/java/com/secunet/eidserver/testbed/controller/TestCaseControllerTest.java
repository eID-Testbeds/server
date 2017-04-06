/*
package com.secunet.eidserver.testbed.controller;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.exceptions.InvalidObjectException;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestProfile;
import com.secunet.eidserver.testbed.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.dao.TestProfileDAO;
import com.secunet.eidserver.testbed.model.entities.DefaultTestCaseEntity;
import com.secunet.eidserver.testbed.model.entities.TestCaseStepEntity;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;

public class TestCaseControllerTest
{

	private static final String TEST_CASE_NAME = "JUNIT_A1_01";
	private static final String TEST_CASE_STEP_NAME = "TCS_NAME";
	private static final String TEST_MESSAGE_ONE = "Testmessage One";
	private static final String TEST_MESSAGE_TWO = "Testmessage Two";
	private static String TEST_PROFILE_NAME = "TestProfile";
	private static String profileID = "0";

	DefaultTestCaseDAO testCaseDAO;
	TestCaseStepDAO testCaseStepDAO;
	TestCaseControllerBean testCaseController;
	TestProfileDAO testProfileDAO;

	@BeforeClass
	public void setUp() throws Exception
	{

		testCaseDAO = (DefaultTestCaseDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/DefaultTestCaseDAO");
		assertNotNull(testCaseDAO);
		testCaseStepDAO = (TestCaseStepDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestCaseStepDAO");
		testCaseController = (TestCaseControllerBean) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestCaseController");
		assertNotNull(testCaseController);
		testProfileDAO = (TestProfileDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestProfileDAO");
		assertNotNull(testProfileDAO);

		// create a TestCase to be used in test-methods:
		TestCase testCase = new DefaultTestCaseEntity();
		testCase.setName(TEST_CASE_NAME);
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		testCase.setTestCaseSteps(steps);

		TestCaseStep testCaseStep = new TestCaseStepEntity();
		testCaseStep.setDefault(true);
		assertNotNull(testCaseStep);
		testCaseStep.setName(TEST_CASE_STEP_NAME);
		testCaseStep.setInbound(false);
		testCaseStep.setMessage(TEST_MESSAGE_ONE);
		testCaseStepDAO.persist((TestCaseStepEntity) testCaseStep);

		steps.add(testCaseStep);
		// persist
		testCaseDAO.merge((DefaultTestCaseEntity) testCase);

		// find a
		@SuppressWarnings("unchecked")
		Collection<TestProfile> profiles = testProfileDAO.findAll();
		assertNotNull(profiles);
		TestProfile profile = profiles.iterator().next();
		profileID = profile.getId();
		TEST_PROFILE_NAME = profile.getName() + "/" + profile.getVersionMajor() + "." + profile.getVersionMinor() + "." + profile.getVersionSubminor();

		testCase = testCaseDAO.findByName(TEST_CASE_NAME);
		assertNotNull(testCase);
		assertEquals(1, testCase.getTestCaseSteps().size());

	}

	private TestCase createDefaultTestCaseRandomName() throws Exception
	{
		TestCase testCase = new DefaultTestCaseEntity();
		testCase.setName("JUNIT" + (new SecureRandom()).nextInt());

		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_CVCA_1", "CERT_CV_DV_1_A" });
		testCase.setCertificateBaseNames(certificateBaseNames);

		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		testCase.setTestCaseSteps(steps);
		TestCaseStep testCaseStep = testCaseStepDAO.findByName(TEST_CASE_STEP_NAME);
		if (testCaseStep == null)
		{
			testCaseStep = new TestCaseStepEntity();
			testCaseStep.setName(TEST_CASE_STEP_NAME);
			testCaseStep.setDefault(true);
			assertNotNull(testCaseStep);
			testCaseStep.setInbound(false);
			testCaseStep.setMessage(TEST_MESSAGE_ONE);
			testCaseStepDAO.persist((TestCaseStepEntity) testCaseStep);
		}
		steps.add(testCaseStep);

		// persist
		testCaseDAO.merge((DefaultTestCaseEntity) testCase);

		// re-fetch, assert not null
		testCase = testCaseDAO.findByName(testCase.getName());
		assertNotNull(testCase);

		// 2 certificateBaseNames:
		assertEquals(2, testCase.getCertificateBaseNames().size());
		assertEquals(true, testCase.getCertificateBaseNames().contains("CERT_CV_CVCA_1"));
		assertEquals(true, testCase.getCertificateBaseNames().contains("CERT_CV_DV_1_A"));

		// steps:
		assertEquals(1, testCase.getTestCaseSteps().size());
		assertNotNull(testCase.getTestCaseSteps().get(0).getMessage());

		return testCase;
	}

	@Test
	public void testCreateTestCaseCopyReplaceCerts() throws Exception
	{

		TestCase baseTestCase = createDefaultTestCaseRandomName();
		String baseTestCaseName = baseTestCase.getName();
		int baseTestCaseNumSteps = baseTestCase.getTestCaseSteps().size();

		// create copy and do assertions
		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_TERM_1_A" });
		String testCaseResult = testCaseController.createTestCaseCopy(profileID, baseTestCaseName, "SUFFIX", true, certificateBaseNames, false, null, null);
		assertNotNull(testCaseResult);
		TestCase testCase = testCaseDAO.findByName(getExpectedTestCaseCopyName(baseTestCase, 1));
		assertNotNull(testCase);

		// assert new certs:
		assertEquals(1, testCase.getCertificateBaseNames().size());
		assertEquals(true, testCase.getCertificateBaseNames().contains("CERT_CV_TERM_1_A"));
		// steps are still the same:
		assertEquals(baseTestCaseNumSteps, testCase.getTestCaseSteps().size());

		// re-fetch original and assert it is not changed:
		TestCase baseRefetch = testCaseDAO.findByName(baseTestCaseName);
		assertNotNull(baseRefetch);
		assertEquals(true, (baseRefetch instanceof DefaultTestCase));
	}

	@Test
	public void testCreateTestCaseCopyReplaceSteps() throws Exception
	{

		TestCase baseTestCase = createDefaultTestCaseRandomName();
		int baseTestCaseNumCerts = baseTestCase.getCertificateBaseNames().size();
		int baseTestCaseNumSteps = baseTestCase.getTestCaseSteps().size();
		TestCaseStep baseTestCaseStep = baseTestCase.getTestCaseSteps().get(0);

		List<String> testCaseStepNamesToReplace = Arrays.asList(new String[] { baseTestCaseStep.getName() });
		List<String> replacingXml = Arrays.asList(new String[] { TEST_MESSAGE_TWO });

		String testCaseResult = testCaseController.createTestCaseCopy(profileID, baseTestCase.getName(), "SUFFIX", false, null, true, testCaseStepNamesToReplace, replacingXml);
		assertNotNull(testCaseResult);
		TestCase testCase = testCaseDAO.findByName(getExpectedTestCaseCopyName(baseTestCase, 1));
		assertNotNull(testCase);

		// certs are still the same:
		assertEquals(baseTestCaseNumCerts, testCase.getCertificateBaseNames().size());
		// num steps are still the same:
		assertEquals(baseTestCaseNumSteps, testCase.getTestCaseSteps().size());
		// but step has changed: message from TEST_MESSAGE_ONE to
		// TEST_MESSAGE_TWO
		String expectedMessageName = getExpectedTestMessageCopyName(testCase, baseTestCaseStep);
		assertEquals(expectedMessageName, testCase.getTestCaseSteps().get(0).getName());
		assertEquals(replacingXml.get(0), testCase.getTestCaseSteps().get(0).getMessage());
		// new step must be a copy-object, not a default-object anymore:
		assertEquals(false, testCase.getTestCaseSteps().get(0).isDefault());
		// is message replaced?
		assertEquals(false, baseTestCaseStep.getName().equals(testCase.getTestCaseSteps().get(0).getName()));
		assertEquals(false, baseTestCaseStep.getMessage().equals(testCase.getTestCaseSteps().get(0).getMessage()));

	}

	private TestCase createTestCaseCopy(TestCase defaultTestCase) throws Exception
	{
		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_TERM_1_A" });
		String testCaseName = testCaseController.createTestCaseCopy(profileID, defaultTestCase.getName(), "SUFFIX", true, certificateBaseNames, false, null, null);
		// check if the copy has been added to the profile
		TestProfile profile = testProfileDAO.findById(profileID);
		assertNotNull(profile);
		assertNotNull(profile.getTestCases());
		assertTrue(profile.getTestCases().size() > 0);
		boolean hadTest = false;
		TestCase retval = null;
		for (TestCase tc : profile.getTestCases())
		{
			if (tc.getName().equals(testCaseName))
			{
				hadTest = true;
				retval = tc;
				break;
			}
		}
		assertEquals(true, hadTest);
		return retval;
	}

	@Test
	public void testEditTestCaseCopyReplaceCerts() throws Exception
	{

		// create a default test:
		TestCase defaultTestCase = createDefaultTestCaseRandomName();
		// create a copy of it:
		TestCase testCaseCopy = createTestCaseCopy(defaultTestCase);
		String testCaseCopyName = testCaseCopy.getName();

		// edit the copy
		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_CVCA_1" });
		String copyName = testCaseController.editTestCaseCopy(testCaseCopyName, true, certificateBaseNames, false, null, null);
		// assert name is unchanged:
		assertEquals(testCaseCopyName, copyName);

		testCaseCopy = testCaseDAO.findByName(copyName);
		assertEquals(1, testCaseCopy.getCertificateBaseNames().size());
		assertEquals(true, testCaseCopy.getCertificateBaseNames().contains("CERT_CV_CVCA_1"));
		// re-fetch
		testCaseCopy = testCaseDAO.findByName(testCaseCopyName);
		assertEquals(1, testCaseCopy.getCertificateBaseNames().size());
		assertEquals(true, testCaseCopy.getCertificateBaseNames().contains("CERT_CV_CVCA_1"));

	}

	@Test
	public void testEditTestCaseCopyReplaceSteps() throws Exception
	{

		// create a default test:
		TestCase defaultTestCase = createDefaultTestCaseRandomName();
		// create a copy of it:
		TestCase testCaseCopy = createTestCaseCopy(defaultTestCase);
		String testCaseCopyName = testCaseCopy.getName();
		TestCaseStep defaultTestCaseStep = defaultTestCase.getTestCaseSteps().get(0);
		// this copy at first refers to a default-test-step:
		assertEquals(true, testCaseCopy.getTestCaseSteps().get(0).isDefault());

		int totalNumOfTestCases = testCaseDAO.findAll().size();

		// edit the copy, replacing steps: in the first run, a new step-copy
		// will be created because the current TestCaseCopy has a default step
		// (see above):
		List<String> testCaseStepNamesToReplace = Arrays.asList(new String[] { defaultTestCaseStep.getName() });
		List<String> replacingXml = Arrays.asList(new String[] { "CLIENT_HELLO" });
		String newName = testCaseController.editTestCaseCopy(testCaseCopyName, false, null, true, testCaseStepNamesToReplace, replacingXml);
		// assert name is unchanged:
		assertEquals(testCaseCopyName, newName);
		// the step should have been replaced by a copy, i.e. it is now
		// 1) non-default 2) num-of-steps should be increased by one
		testCaseCopy = testCaseDAO.findByName(testCaseCopyName);
		assertEquals(false, testCaseCopy.getTestCaseSteps().get(0).isDefault());

		// edit the copy again, replacing steps:
		// in the second run, the step (already a copy now) must be kept/edited,
		// i.e. do NOT
		// create a new object (there is only one single message-copy allowed
		// per testcase)
		String stepName = testCaseCopy.getTestCaseSteps().get(0).getName();
		testCaseStepNamesToReplace = Arrays.asList(new String[] { stepName });
		replacingXml = Arrays.asList(new String[] { "SERVER_HELLO" });
		newName = testCaseController.editTestCaseCopy(testCaseCopyName, false, null, true, testCaseStepNamesToReplace, replacingXml);
		assertEquals(testCaseCopyName, newName);
		// assertions: see above: step-object must still be the same,
		// num-of-msg-objects must be the same:
		testCaseCopy = testCaseDAO.findByName(testCaseCopyName);
		assertEquals(stepName, testCaseCopy.getTestCaseSteps().get(0).getName());

		// num of testcases must not have been changed during complete test:
		assertEquals(totalNumOfTestCases, testCaseDAO.findAll().size());

	}

	// @Test(expectedExceptions = InvalidObjectException.class)
	@Test
	public void testCreateCopyOfCopy() throws Exception
	{

		Exception exception = null;

		// create a default test:
		TestCase defaultTestCase = createDefaultTestCaseRandomName();
		// create a copy of it:
		TestCase testCaseCopy = createTestCaseCopy(defaultTestCase);

		// try to create a copy of the copy (which is NOT allowed):
		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_TERM_1_A" });
		try
		{
			String testCaseName = testCaseController.createTestCaseCopy(profileID, testCaseCopy.getName(), "SUFFIX", true, certificateBaseNames, false, null, null);
			// this should not be reached
			System.out.println("Edited default testcase " + testCaseName);
		}
		catch (Exception e)
		{
			exception = e;
		}

		assertEquals(true, isCause(InvalidObjectException.class, exception));
	}

	@Test
	public void testEditDefaultTestCase() throws Exception
	{

		Exception exception = null;

		// create a default test:
		TestCase defaultTestCase = createDefaultTestCaseRandomName();

		// try to edit a default TC (which is NOT allowed):
		List<String> certificateBaseNames = Arrays.asList(new String[] { "CERT_CV_TERM_1_A" });
		try
		{
			String testCaseName = testCaseController.editTestCaseCopy(defaultTestCase.getName(), true, certificateBaseNames, false, null, null);
			// this should never be reached
			System.out.println("Edited default testcase " + testCaseName);
		}
		catch (Exception e)
		{
			exception = e;
		}

		assertEquals(true, isCause(InvalidObjectException.class, exception));
	}

	private String getExpectedTestCaseCopyName(TestCase original, int number)
	{

		String expectedName = original.getName() + '#' + TEST_PROFILE_NAME + '#' + "1";
		return expectedName;
	}

	private String getExpectedTestMessageCopyName(TestCase testCaseCopy, TestCaseStep originalTestMessage)
	{
		String expectedName = originalTestMessage.getName() + '#' + TEST_PROFILE_NAME + '#' + testCaseCopy.getName();
		return expectedName;
	}

	private boolean isCause(Class<? extends Throwable> expected, Throwable exc)
	{
		return expected.isInstance(exc) || (exc != null && isCause(expected, exc.getCause()));
	}

}
*/
