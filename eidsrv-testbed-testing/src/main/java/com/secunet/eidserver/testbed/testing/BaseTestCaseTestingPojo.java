package com.secunet.eidserver.testbed.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.testbedutils.helperclasses.Pair;

public abstract class BaseTestCaseTestingPojo extends BaseTestingPojo implements TestCase
{
	private static final long serialVersionUID = -5481226771913538563L;

	private String name;

	private String description;

	private List<TestCaseStep> testCaseSteps = new ArrayList<>();

	private List<TestCandidate> candidates = new ArrayList<TestCandidate>();

	private Set<IcsMandatoryprofile> mandatoryProfiles = new HashSet<>();

	private Set<IcsOptionalprofile> optionalProfiles = new HashSet<>();

	private List<String> certificateBaseNames = new ArrayList<>();

	private Map<String, String> variables = new HashMap<>();

	private List<String> module = new ArrayList<>();

	private EService eService;

	private EidCard card;

	private String manualExplanation;

	private String clientExplanation;

	private List<Pair<String, String>> relevantSteps;

	@Override
	public void setRelevantSteps(List<Pair<String, String>> relevantSteps)
	{
		this.relevantSteps = relevantSteps;
	}

	@Override
	public List<Pair<String, String>> getRelevantSteps()
	{
		return relevantSteps;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public List<TestCaseStep> getTestCaseSteps()
	{
		return testCaseSteps;
	}

	@Override
	public void setTestCaseSteps(List<TestCaseStep> testCaseSteps)
	{
		this.testCaseSteps = testCaseSteps;
	}

	@Override
	public List<String> getCertificateBaseNames()
	{
		return this.certificateBaseNames;
	}

	@Override
	public void setCertificateBaseNames(List<String> certificateBaseNames)
	{
		this.certificateBaseNames = certificateBaseNames;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public List<TestCandidate> getCandidates()
	{
		return candidates;
	}

	@Override
	public void setCandidates(List<TestCandidate> candidates)
	{
		this.candidates = candidates;
	}

	@Override
	public Set<IcsMandatoryprofile> getMandatoryProfiles()
	{
		return mandatoryProfiles;
	}

	@Override
	public void setMandatoryProfiles(Set<IcsMandatoryprofile> profiles)
	{
		this.mandatoryProfiles = profiles;
	}

	@Override
	public void addMandatoryProfile(IcsMandatoryprofile profile)
	{
		this.mandatoryProfiles.add(profile);
	}

	@Override
	public Set<IcsOptionalprofile> getOptionalProfiles()
	{
		return optionalProfiles;
	}

	@Override
	public void setOptionalProfiles(Set<IcsOptionalprofile> profiles)
	{
		this.optionalProfiles = profiles;
	}

	@Override
	public void addOptionalProfile(IcsOptionalprofile profile)
	{
		this.optionalProfiles.add(profile);
	}

	/**
	 * @return the variables
	 */
	@Override
	public Map<String, String> getVariables()
	{
		return variables;
	}

	/**
	 * @param variables
	 *            the variables to set
	 */
	@Override
	public void setVariables(Map<String, String> variables)
	{
		this.variables = variables;
	}

	@Override
	public List<String> getModule()
	{
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	@Override
	public void setModule(List<String> module)
	{
		this.module = module;
	}

	@Override
	public EidCard getCard()
	{
		return this.card;
	}

	@Override
	public void setCard(EidCard eidCard)
	{
		this.card = eidCard;
	}

	@Override
	public String getManualExplanation()
	{
		return this.manualExplanation;
	}

	@Override
	public void setManualExplanation(String manualExplanation)
	{
		this.manualExplanation = manualExplanation;
	}

	/**
	 * @return the eservice
	 */
	@Override
	public EService getEservice()
	{
		return eService;
	}

	/**
	 * @param eservice
	 *            the eservice to set
	 */
	@Override
	public void setEservice(EService eservice)
	{
		this.eService = eservice;
	}

	/**
	 * @return the clientExplanation
	 */
	@Override
	public String getClientExplanation()
	{
		return clientExplanation;
	}

	/**
	 * @param clientExplanation
	 *            the clientExplanation to set
	 */
	@Override
	public void setClientExplanation(String clientExplanation)
	{
		this.clientExplanation = clientExplanation;
	}

}
