package com.secunet.eidserver.testbed.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.exceptions.ComponentNotInitializedException;
import com.secunet.eidserver.testbed.common.exceptions.InvalidObjectException;
import com.secunet.eidserver.testbed.common.exceptions.ObjectNotFoundException;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.CopyTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

@Stateless
public class TestCaseControllerBean implements TestCaseController
{

	@EJB
	private TestGenerator generator;

	@EJB
	private DefaultTestCaseDAO defaultTestCaseDAO;

	@EJB
	private CopyTestCaseDAO copyTestCaseDAO;

	@EJB
	private TestCandidateDAO testCandidateDAO;

	@EJB
	private TestCaseStepDAO testCaseStepDAO;

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
	@Override
	public CopyTestCase createTestCaseCopy(String candidateId, String defaultTestCaseName, String suffix, boolean replaceCertificateBaseNames, List<String> certificateBaseNames,
			boolean replaceTestCaseSteps, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml)
	{
		// get default testCase to be used as template for the copy:
		DefaultTestCase defaultTestCase = defaultTestCaseDAO.findByName(defaultTestCaseName);
		if (defaultTestCase == null)
		{
			throw new ObjectNotFoundException(TestCase.class, defaultTestCaseName);
		}
		TestCandidate profile = testCandidateDAO.findById(candidateId);

		// create a deep copy & persist it
		CopyTestCase cpy = copyTestCaseDAO.createNew();

		if (replaceCertificateBaseNames)
		{
			cpy.setCertificateBaseNames(certificateBaseNames);
		}
		else
		{
			cpy.setCertificateBaseNames(defaultTestCase.getCertificateBaseNames());
		}

		if (replaceTestCaseSteps)
		{
			cpy = replaceTestCaseSteps(cpy, testCaseStepNamesToReplace, replacingTestCaseStepXml);
		}
		else
		{
			cpy.setTestCaseSteps(defaultTestCase.getTestCaseSteps());
		}

		copyTestCaseDAO.persist(cpy);

		// add the relationships
		profile.getTestCases().add(cpy);
		cpy.setDefaultTestcase(defaultTestCase);
		defaultTestCaseDAO.update(defaultTestCase);
		testCandidateDAO.update(profile);

		return cpy;

	}

	@Override
	public String editTestCaseCopy(String testCaseCopyName, boolean replaceCertificateBaseNames, List<String> certificateBaseNames, boolean replaceTestCaseSteps,
			List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml)
	{

		CopyTestCase testCase = copyTestCaseDAO.findByName(testCaseCopyName);
		if (testCase == null)
		{
			throw new ObjectNotFoundException(CopyTestCase.class, testCaseCopyName);
		}

		if (replaceCertificateBaseNames)
		{
			testCase.setCertificateBaseNames(certificateBaseNames);
		}

		if (replaceTestCaseSteps)
		{
			testCase = replaceTestCaseSteps(testCase, testCaseStepNamesToReplace, replacingTestCaseStepXml);
		}

		copyTestCaseDAO.update(testCase);

		return testCaseCopyName;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void persistTestCaseCopy(CopyTestCase copy, boolean sourceDefault)
	{
		if (sourceDefault)
		{
			copy.setName(copy.getName() + "#" + copy.getSuffix());
			copyTestCaseDAO.persist(copy);
			copy.getDefaultTestcase().addCopy(copy);
			defaultTestCaseDAO.update(copy.getDefaultTestcase());
			for (TestCandidate candidate : copy.getCandidates())
			{
				Set<TestCase> tests = candidate.getTestCases();
				tests.add(copy);
				candidate.setTestCases(tests);
				testCandidateDAO.update(candidate);
			}
		}
		else
		{
			copyTestCaseDAO.update(copy);
			for (TestCandidate candidate : (Set<TestCandidate>) testCandidateDAO.findAll())
			{
				if (candidate.getTestCases().contains(copy) && !copy.getCandidates().contains(candidate))
				{
					Set<TestCase> tests = candidate.getTestCases();
					tests.remove(copy);
					candidate.setTestCases(tests);
					testCandidateDAO.update(candidate);
				}
			}
		}
	}

	private CopyTestCase replaceTestCaseSteps(CopyTestCase copyTestCase, List<String> testCaseStepNamesToReplace, List<String> replacingTestCaseStepXml)
	{

		// traverse existing TestCaseSteps and replace those with matching id:
		for (int i = 0; i < testCaseStepNamesToReplace.size(); i++)
		{

			String nameToReplace = testCaseStepNamesToReplace.get(i);
			int indexToReplace = getIndexOfTestCaseStep(nameToReplace, copyTestCase.getTestCaseSteps());
			if (indexToReplace < 0)
			{
				throw new ObjectNotFoundException(TestCaseStep.class, nameToReplace);
			}

			TestCaseStep testCaseStepToReplace = copyTestCase.getTestCaseSteps().get(indexToReplace);
			// list of testCaseSteps may be a mixture of default-steps and
			// copied-steps.
			// If the step to replace is already a copy, then modify the copy.
			// If the step to replace is a default step, then create a copy
			// instead (defaults are never modified):
			if (testCaseStepToReplace.isDefault())
			{
				// create a copy of the default-step and set the new xml


				TestCaseStep testCaseStepCopy = createTestCaseStepCopy(copyTestCase, testCaseStepToReplace, replacingTestCaseStepXml.get(indexToReplace), "SUFFIX");


				copyTestCase.getTestCaseSteps().set(indexToReplace, testCaseStepCopy);
			}
			else
			{
				// apply the new xml to the copied-step w/o creating a new copy:
				testCaseStepToReplace.setMessage(replacingTestCaseStepXml.get(indexToReplace));
				testCaseStepDAO.update(testCaseStepToReplace);
			}
		}

		return copyTestCase;
	}

	private int getIndexOfTestCaseStep(String name, List<TestCaseStep> steps)
	{
		for (int i = 0; i < steps.size(); i++)
		{
			if (name.equals(steps.get(i).getName()))
			{
				return i;
			}
		}
		return -1;
	}

	private TestCaseStep createTestCaseStepCopy(TestCase originalTestCase, TestCaseStep originalTestStep, String newStepXml, String suffix)
	{

		if (!originalTestStep.isDefault())
		{
			throw new InvalidObjectException(originalTestStep, "Cannot create copy of non-default-TestCaseSteps.");
		}

		TestCaseStep copy = testCaseStepDAO.createNew();
		copy.setName(originalTestStep.getName() + suffix);
		copy.setDefault(false);
		copy.setInbound(originalTestStep.getInbound());
		copy.setMessage(newStepXml);

		testCaseStepDAO.merge(copy);
		return copy;

	}

	@Override
	public TestModule getRootTestModule(TestCandidate candidate) throws InvalidObjectException
	{
		if (null == candidate.getTestCases() || candidate.getTestCases().isEmpty())
		{
			throw new InvalidObjectException(candidate, "TestCandidate does not contain any test cases.");
		}
		TestModule root = new TestModule("root");
		for (TestCase tc : candidate.getTestCases())
		{
			TestModule current = root;
			for (String s : tc.getModule())
			{
				TestModule child = current.getSubModule(s);
				if (null == child)
				{
					TestModule newModule = new TestModule(s);
					current.addSubModule(newModule);
					current = newModule;
				}
				else
				{
					current = child;
				}

			}
			current.getTestCases().add(tc);
		}
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TestCaseStep> getAllTestCaseSteps()
	{
		return (Set<TestCaseStep>) testCaseStepDAO.findAll();
	}

	@Override
	public void persistTestCaseStep(TestCaseStep newOrEditedTestCaseStep, String oldSuffix) throws Exception
	{
		if (null == oldSuffix)
		{
			newOrEditedTestCaseStep.setName(newOrEditedTestCaseStep.getName() + "#" + newOrEditedTestCaseStep.getSuffix());
			testCaseStepDAO.persist(newOrEditedTestCaseStep);
		}
		else
		{
			// remove the old suffix from the name of the copy by subtracting the suffix
			String name = newOrEditedTestCaseStep.getName();
			TestCaseStep old = testCaseStepDAO.findByName(name);
			testCaseStepDAO.delete(old);
			name = name.substring(0, (name.length() - oldSuffix.length()));
			name += newOrEditedTestCaseStep.getSuffix();
			newOrEditedTestCaseStep.setName(name);
			testCaseStepDAO.persist(newOrEditedTestCaseStep);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<TestCase> getAllTestCases() throws Exception
	{
		Set<TestCase> tc = new HashSet<>();

		Set<TestCase> dtc = (Set<TestCase>) defaultTestCaseDAO.findAll();
		if (dtc != null && !dtc.isEmpty())
			tc.addAll(dtc);

		Set<TestCase> ctc = (Set<TestCase>) copyTestCaseDAO.findAll();
		if (ctc != null && !ctc.isEmpty())
			tc.addAll(ctc);

		return tc;
	}

	@Override
	public void generateDefaultTestcases() throws Exception
	{
		if (null == generator)
		{
			throw new ComponentNotInitializedException("Test Case Generator");
		}
		// load existing default testcases
		Set<DefaultTestCase> defaultTestcases = null;
		try
		{
			@SuppressWarnings("unchecked")
			Collection<DefaultTestCase> dfltCollection = (Collection<DefaultTestCase>) defaultTestCaseDAO.findAll();
			defaultTestcases = new HashSet<DefaultTestCase>(dfltCollection);
		}
		catch (EntityNotFoundException e)
		{
			defaultTestcases = new HashSet<DefaultTestCase>();
		}
		if (defaultTestcases.size() == 0)
		{
			// no testcases generated yet
			defaultTestcases = generator.generateDefaultTestcases();
			for (DefaultTestCase tc : defaultTestcases)
			{
				List<TestCaseStep> modList = new ArrayList<>();
				// persist steps
				for (TestCaseStep step : tc.getTestCaseSteps())
				{
					TestCaseStep dbStep = testCaseStepDAO.findByName(step.getName());
					if (dbStep == null)
					{
						testCaseStepDAO.persist(step);
						modList.add(step);
					}
					else
					{
						modList.add(dbStep);
					}
				}
				tc.setTestCaseSteps(modList);
				// persist testcase
				defaultTestCaseDAO.persist(tc);
			}
		}
	}

}
