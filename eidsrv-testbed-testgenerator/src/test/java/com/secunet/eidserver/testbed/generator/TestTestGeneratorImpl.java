package com.secunet.eidserver.testbed.generator;

import static org.mockito.Mockito.when;

import java.util.Set;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.interfaces.dao.CopyTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.testing.CopyTestCaseTestingPojo;
import com.secunet.eidserver.testbed.testing.DefaultTestCaseTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCaseStepTestingPojo;
import com.secunet.testbedutils.helperclasses.Pair;

public class TestTestGeneratorImpl
{
	@InjectMocks
	private TestGeneratorBean tg = new TestGeneratorBean();

	@Mock
	private TestCaseStepDAO tcsDAO;

	@Mock
	private DefaultTestCaseDAO dtcDAO;

	@Mock
	private CopyTestCaseDAO ctcDAO;

	@BeforeClass
	private void initMocks()
	{
		// mocks themselves
		MockitoAnnotations.initMocks(this);

		// empty POJOs
		TestCaseStep emptyStep = new TestCaseStepTestingPojo();
		when(tcsDAO.createNew()).thenReturn(emptyStep);
		when(dtcDAO.createNew()).thenAnswer(new Answer<DefaultTestCaseTestingPojo>() {
			@Override
			public DefaultTestCaseTestingPojo answer(InvocationOnMock invocation)
			{
				return new DefaultTestCaseTestingPojo();
			}
		});
		when(ctcDAO.createNew()).thenAnswer(new Answer<CopyTestCaseTestingPojo>() {
			@Override
			public CopyTestCaseTestingPojo answer(InvocationOnMock invocation)
			{
				return new CopyTestCaseTestingPojo();
			}
		});
	}

	@Test
	public void testGenerateDefaultTestcases() throws Exception
	{
		Set<DefaultTestCase> defaultTestCases = tg.generateDefaultTestcases();
		Assert.assertNotNull(defaultTestCases);
		Assert.assertTrue(defaultTestCases.size() > 0);
	}

	@Test
	public void testRelevantSteps()
	{
		Set<DefaultTestCase> defaultTestCases = tg.generateDefaultTestcases();
		for (DefaultTestCase testCase : defaultTestCases)
		{
			if (testCase.getName().equals("EID_SERVER_A1_1_01_AcademicTitle"))
			{
				Assert.assertEquals(testCase.getRelevantSteps().size(), 2);
				Pair<String, String> communicationInitSteps = testCase.getRelevantSteps().get(0);
				Assert.assertEquals(communicationInitSteps.getA(), "OUT_USEID_REQUEST");
				Assert.assertEquals(communicationInitSteps.getB(), "IN_USEID_RESPONSE");
				Pair<String, String> fetchResultsSteps = testCase.getRelevantSteps().get(1);
				Assert.assertEquals(fetchResultsSteps.getA(), "OUT_GETRESULT_REQUEST");
				Assert.assertEquals(fetchResultsSteps.getB(), "IN_GETRESULT_RESPONSE");
				break;
			}
		}
	}

}
