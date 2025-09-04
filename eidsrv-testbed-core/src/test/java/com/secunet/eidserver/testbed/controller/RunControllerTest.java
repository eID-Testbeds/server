package com.secunet.eidserver.testbed.controller;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.quality.Strictness;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.*;

import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.testing.DefaultTestCaseTestingPojo;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCaseStepTestingPojo;
import de.bsi.testbedutils.helperclasses.Pair;

@Listeners(MockitoTestNGListener.class)
@MockitoSettings(strictness = Strictness.WARN)
public class RunControllerTest
{
	@InjectMocks
	private RunControllerBean runController = new RunControllerBean();

	@Mock
	private DefaultTestCaseDAO defaultTestcaseDAOMock;

	@BeforeMethod
	public void setup() throws Exception
	{
		// empty POJOs
		DefaultTestCase testCase = new DefaultTestCaseTestingPojo();
		when(defaultTestcaseDAOMock.createNew()).thenReturn(testCase);
	}

	@Test(enabled = true)
	public void testSkipIrrelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();
		boolean success = runController.wasSuccessful(testCase, messages);
		assertTrue(success);
	}

	@Test(enabled = true)
	public void testNegativeSkipIrrelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();
		LogMessage failedRelevant = new LogMessageTestingPojo();
		failedRelevant.setTestStepName("FAILED");
		failedRelevant.setSuccess(false);
		messages.add(1, failedRelevant);
		boolean success = runController.wasSuccessful(testCase, messages);
		assertFalse(success);
	}

	@Test(enabled = true)
	public void testIncludeTlsInRelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();

		LogMessage tlsMessage = new LogMessageTestingPojo();
		tlsMessage.setTestStepName("FIRST");
		tlsMessage.setSuccess(false);
		messages.add(0, tlsMessage);

		boolean success = runController.wasSuccessful(testCase, messages);
		assertFalse(success);
	}

	private TestCase prepareRelevantTestCase()
	{
		TestCase testCase = new DefaultTestCaseTestingPojo();

		List<Pair<String, String>> relevantSteps = new ArrayList<>();
		Pair<String, String> firstRelevant = new Pair<>("FIRST", "MID");
		Pair<String, String> secondRelevant = new Pair<>("MID2", "END");
		relevantSteps.add(firstRelevant);
		relevantSteps.add(secondRelevant);
		testCase.setRelevantSteps(relevantSteps);

		TestCaseStep first = new TestCaseStepTestingPojo();

		// testCase.setTestCaseSteps(testCaseSteps);
		return testCase;
	}

	private List<LogMessage> prepareRelevantTestMessages()
	{
		List<LogMessage> messages = new ArrayList<>();

		LogMessage first = new LogMessageTestingPojo();
		first.setTestStepName("FIRST");
		first.setSuccess(true);

		LogMessage second = new LogMessageTestingPojo();
		second.setTestStepName("MID");
		second.setSuccess(true);

		LogMessage irrelevantFailure = new LogMessageTestingPojo();
		irrelevantFailure.setTestStepName("FAILURE");
		irrelevantFailure.setSuccess(true);

		LogMessage relevantAgain = new LogMessageTestingPojo();
		relevantAgain.setTestStepName("MID2");
		relevantAgain.setSuccess(true);

		LogMessage last = new LogMessageTestingPojo();
		last.setTestStepName("END");
		last.setSuccess(true);

		messages.add(first);
		messages.add(second);
		messages.add(irrelevantFailure);
		messages.add(relevantAgain);
		messages.add(last);

		return messages;
	}

}

