package com.secunet.eidserver.testbed.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.testbedutils.helperclasses.Pair;

@Entity
@Table(name = "TEST_CASE")
public abstract class BaseTestCaseEntity extends BaseEntity implements TestCase
{
	private static final long serialVersionUID = -5481226771913538563L;

	@Column(name = "NAME", unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@CollectionTable(name = "TESTCASE_STEP_MAPPING")
	@Column(name = "STEP")
	private List<TestCaseStep> testCaseSteps = new ArrayList<>();

	@ManyToMany(mappedBy = "testCases", targetEntity = TestCandidateEntity.class)
	private List<TestCandidate> candidates = new ArrayList<TestCandidate>();

	@Column(name = "MANDATORY_PROFILES")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsMandatoryprofile> mandatoryProfiles = new HashSet<>();

	@Column(name = "OPTIONAL_PROFILES")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsOptionalprofile> optionalProfiles = new HashSet<>();

	private List<String> certificateBaseNames = new ArrayList<>();

	@ElementCollection
	@JoinTable(name = "VARIABLE_TABLE", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyColumn(name = "VALUE")
	@Column(name = "VARIABLE")
	private Map<String, String> variables = new HashMap<>();

	@Column(name = "EID_CARD")
	@Enumerated(EnumType.STRING)
	private EidCard card;

	@Column(name = "E_SERVICE")
	@Enumerated(EnumType.STRING)
	private EService eservice;

	@Column(name = "MODULE")
	private List<String> module = new ArrayList<>();

	@Column(name = "MANUAL_TEST_EXPLANATION")
	private String manualExplanation;

	@Column(name = "SINGLE_CLIENT_EXPLANATION")
	private String clientExplanation;

	@Column(name = "RELEVANT_STEPS")
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
		return this.testCaseSteps;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certificateBaseNames == null) ? 0 : certificateBaseNames.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((mandatoryProfiles == null) ? 0 : mandatoryProfiles.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((optionalProfiles == null) ? 0 : optionalProfiles.hashCode());
		result = prime * result + ((testCaseSteps == null) ? 0 : testCaseSteps.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseTestCaseEntity other = (BaseTestCaseEntity) obj;
		if (certificateBaseNames == null)
		{
			if (other.certificateBaseNames != null)
				return false;
		}
		else if (!certificateBaseNames.equals(other.certificateBaseNames))
			return false;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (mandatoryProfiles == null)
		{
			if (other.mandatoryProfiles != null)
				return false;
		}
		else if (!mandatoryProfiles.equals(other.mandatoryProfiles))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (optionalProfiles == null)
		{
			if (other.optionalProfiles != null)
				return false;
		}
		else if (!optionalProfiles.equals(other.optionalProfiles))
			return false;
		if (testCaseSteps == null)
		{
			if (other.testCaseSteps != null)
				return false;
		}
		else if (!testCaseSteps.equals(other.testCaseSteps))
			return false;
		return true;
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

	/**
	 * @return the module
	 */
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
		return eservice;
	}

	/**
	 * @param eservice
	 *            the eservice to set
	 */
	@Override
	public void setEservice(EService eservice)
	{
		this.eservice = eservice;
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
