package com.secunet.eidserver.testbed.generator;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.CertName;
import com.secunet.eidserver.testbed.common.types.testcase.CertNames;
import com.secunet.eidserver.testbed.common.types.testcase.Profile;
import com.secunet.eidserver.testbed.common.types.testcase.ReferenceType;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepType;
import com.secunet.eidserver.testbed.common.types.testcase.StepsType;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.common.types.testcase.TestcaseDefinition;
import com.secunet.eidserver.testbed.common.types.testcase.Variables;
import com.secunet.testbedutils.helperclasses.Pair;
import com.secunet.testbedutils.utilities.JaxBUtil;

@Named
@Singleton
public class TestGeneratorBean implements TestGenerator
{
	private static final Logger logger = LogManager.getLogger(TestGeneratorBean.class);

	private Map<String, TestCaseStep> stepDefinitions;

	private Map<String, TestcaseDefinition> testcaseDefinitions;

	@EJB
	private DefaultTestCaseDAO defaultTestCaseDAO;

	@EJB
	private TestCaseStepDAO testCaseStepDAO;

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator#generateDefaultTestcases()
	 */
	@Override
	public Set<DefaultTestCase> generateDefaultTestcases()
	{
		// load known testcase definitions
		testcaseDefinitions = new HashMap<>();
		File testcaseFolder = new File(Thread.currentThread().getContextClassLoader().getResource("defaultTests").getFile());
		processTestcaseFile(testcaseFolder);

		// known testcase steps
		loadStepsFromResources();

		// process all testcase definitions
		Set<DefaultTestCase> testCaseSet = new HashSet<>();
		for (TestcaseDefinition testDefinition : testcaseDefinitions.values())
		{
			DefaultTestCase testcase = defaultTestCaseDAO.createNew();
			testcase.setName(testDefinition.getName());

			// load all steps
			logger.debug("Loading steps and targets for " + testDefinition.getName());
			ProcessedSteps processedSteps = getStepsAndTargets(testDefinition.getSteps(), null);
			testcase.setTestCaseSteps(processedSteps.getSteps());

			// add certificate names
			CertNames certNames = testDefinition.getCertificateBaseNames();
			if (certNames != null)
			{
				List<String> currNames = new ArrayList<>();
				for (CertName certName : certNames.getCertificate())
				{
					if (null != certName)
					{
						currNames.add(certName.toString());
					}
				}
				testcase.setCertificateBaseNames(currNames);
			}

			// save profiles
			Set<IcsOptionalprofile> optionalProfiles = new HashSet<>();
			Set<IcsMandatoryprofile> mandatoryProfiles = new HashSet<>();
			for (Profile profile : testDefinition.getProfiles().getProfile())
			{
				for (IcsOptionalprofile o : IcsOptionalprofile.values())
				{
					if (o.toString().equals(profile.toString()))
					{
						optionalProfiles.add(o);
						break;
					}
				}
				for (IcsMandatoryprofile m : IcsMandatoryprofile.values())
				{
					if (m.toString().equals(profile.toString()))
					{
						mandatoryProfiles.add(m);
						break;
					}
				}
			}
			testcase.setMandatoryProfiles(mandatoryProfiles);
			testcase.setOptionalProfiles(optionalProfiles);

			// save variables
			// first the nested ones, that may be overwritten afterwards by the ones from the referencing test case
			Map<String, String> variables = new HashMap<>();
			if (null != processedSteps.getNestedVariables() && null != processedSteps.getNestedVariables().getVariable())
			{
				processedSteps.getNestedVariables().getVariable().forEach(v -> variables.put(v.getName(), v.getValue()));
			}
			if (null != testDefinition.getVariables() && null != testDefinition.getVariables().getVariable())
			{
				testDefinition.getVariables().getVariable().forEach(v -> variables.put(v.getName(), v.getValue()));
			}
			if (!variables.isEmpty())
			{
				testcase.setVariables(variables);
			}

			// create the module
			List<String> module = new ArrayList<>();
			module.addAll(testDefinition.getModulePath().getNode());
			testcase.setModule(module);

			// add the description
			testcase.setDescription(testDefinition.getDescription());

			// add the eservice
			testcase.setEservice(testDefinition.getEservice());

			// add the card
			testcase.setCard(testDefinition.getEidcard());

			// add the single client explanation
			testcase.setClientExplanation(testDefinition.getClientExplanation());

			// add the manual flag
			testcase.setManualExplanation((null != testDefinition.getManualExplanation()) ? testDefinition.getManualExplanation() : new String());

			// set the relevant testcases
			testcase.setRelevantSteps(processedSteps.getRelevantSteps());

			testCaseSet.add(testcase);
		}
		return testCaseSet;
	}

	/**
	 * Return a list of {@link TestCaseStep}s, along with a map of their {@link TargetInterfaceType}s. If a target interface type is provided, only steps matching that interface are loaded. If all interfaces shall be processed, provide <i>null</i>.
	 * 
	 * @param stepsType
	 * @param tit
	 * @return
	 */
	private ProcessedSteps getStepsAndTargets(StepsType stepsType, TargetInterfaceType tit)
	{
		List<TestCaseStep> steps = new ArrayList<>();
		Variables nestedVariables = null;
		List<Pair<String, String>> relevantSteps = new ArrayList<>();
		String firstRelevant = null;
		String lastRelevant = null;
		for (Object o : stepsType.getStepOrReference())
		{
			if (o instanceof TestStepType)
			{
				TestStepType s = (TestStepType) o;
				if (null != s.value())
				{
					TestCaseStep tcs = null;
					String name = null;

					if (stepDefinitions.containsKey(s.value()))
					{
						tcs = stepDefinitions.get(s.value());
						name = s.value();
					}
					if (firstRelevant == null)
					{
						firstRelevant = name;
					}
					lastRelevant = name;
					// if the target interface was not specified or if it does match the step interface type
					if (null == tit || tit.equals(tcs.getTarget()))
					{
						steps.add(tcs);
					}
				}
				// test step has not been found
				else
				{
					logger.error("Default test step was empty. The step XML may not have been provided.");
				}
			}
			else
			{
				// found a reference to another test case, not a single step
				if (firstRelevant != null)
				{
					relevantSteps.add(new Pair<>(firstRelevant, lastRelevant));
					firstRelevant = null;
					lastRelevant = null;
				}
				ReferenceType r = (ReferenceType) o;
				if (testcaseDefinitions.containsKey(r.getValue()))
				{
					TestcaseDefinition referencedCase = testcaseDefinitions.get(r.getValue());
					nestedVariables = referencedCase.getVariables();
					ProcessedSteps sub = getStepsAndTargets(referencedCase.getSteps(), r.getTargetInterface());
					steps.addAll(sub.getSteps());
				}
				else
				{
					logger.error("The referenced default testcase " + r.getValue() + " was not loaded. There is a high probability that the test suite will not be able to run properly.");
				}
			}
		}
		if (firstRelevant != null)
		{
			relevantSteps.add(new Pair<>(firstRelevant, lastRelevant));
		}
		return new ProcessedSteps(steps, nestedVariables, relevantSteps);
	}

	/**
	 * Load the default test step definitions from the resource path (file system)
	 * 
	 */
	private void loadStepsFromResources()
	{
		stepDefinitions = new HashMap<>();
		// unmarshall all test case steps
		File stepsFolder = new File(Thread.currentThread().getContextClassLoader().getResource("steps").getFile());
		for (File f : stepsFolder.listFiles())
		{
			try
			{
				Step s = (Step) JAXBContext.newInstance(Step.class).createUnmarshaller().unmarshal(f);
				if (s != null)
				{
					String name = f.getName().substring(0, f.getName().length() - 4);
					TestCaseStep currentStep = testCaseStepDAO.createNew();
					currentStep.setTarget(s.getTargetInterface());
					currentStep.setDefault(true);
					currentStep.setName(name);
					currentStep.setMessage(JaxBUtil.marshallWithoutReplacing(s).replaceAll("(\r)?\n", "\r\n"));
					currentStep.setInbound(name.startsWith("IN"));
					if (null == s.isOptional() || !s.isOptional())
					{
						currentStep.setOptional(false);
					}
					else
					{
						currentStep.setOptional(true);
					}
					stepDefinitions.put(name, currentStep);
				}
				else
				{
					logger.error("Error while unmarshalling file: '" + f.getName() + "'. Will be skipped.");
				}
			}
			catch (JAXBException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.warn("Could not load default testcase step from file '" + f.getName() + "': " + System.getProperty("line.separator") + trace.toString());
			}
		}
	}

	/**
	 * Recursively process all files below the given parent directory
	 * 
	 * @param file
	 *            The file or directory to process
	 */
	private void processTestcaseFile(File file)
	{
		for (File f : file.listFiles())
		{
			if (f.isDirectory())
			{
				processTestcaseFile(f);
			}
			else
			{
				loadTestcase(f);
			}
		}
	}

	/**
	 * Load the given XML testcase
	 * 
	 * @param xmlFile
	 *            The testcase which is to be loaded
	 */
	private void loadTestcase(File xmlFile)
	{
		if (!FilenameUtils.getExtension(xmlFile.getName()).equals("xml"))
		{
			logger.warn("Only XML testcases are supported. Ignoring file " + xmlFile.getName());
		}
		try
		{
			TestcaseDefinition tcdef = (TestcaseDefinition) JAXBContext.newInstance(TestcaseDefinition.class).createUnmarshaller().unmarshal(xmlFile);
			if (tcdef != null)
			{
				testcaseDefinitions.put(xmlFile.getName().substring(0, xmlFile.getName().length() - 4), tcdef);
				// logger.debug("Loading file: " + f.getName());
				if (!xmlFile.getName().substring(0, xmlFile.getName().length() - 4).equals(tcdef.getName()))
				{
					logger.error("Name mismatch in file: " + xmlFile.getName().substring(0, xmlFile.getName().length()));
				}
				String previousStep = null;
				for (Object o : tcdef.getSteps().getStepOrReference())
				{
					if (o instanceof StepType)
					{
						StepType s = (StepType) o;
						if (null != s.getValue())
						{
							String name = null;
							if (null != s.getValue().toString())
							{
								name = s.getValue().toString();
							}
							else if (null != s.getValue().value())
							{
								name = s.getValue().value();
							}
							if (null == name)
							{
								logger.error("Error while reading steps from file: " + xmlFile.getName() + ". Previous correct step: " + previousStep);
								continue;
							}
							previousStep = s.getValue().toString();
						}
						else
						{
							logger.error("Error while reading steps from file: " + xmlFile.getName() + ". Previous correct step: " + previousStep);
						}
					}
				}
			}
		}
		catch (JAXBException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Could not load default testcase definition from file '" + xmlFile.getName() + "': " + System.getProperty("line.separator") + trace.toString());
		}
	}
}