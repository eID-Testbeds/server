package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.testbedutils.helperclasses.Pair;

public interface TestCase
{
	public String getId();

	public String getName();

	public void setName(String name);

	public List<TestCaseStep> getTestCaseSteps();

	public void setTestCaseSteps(List<TestCaseStep> testCaseSteps);

	public List<String> getCertificateBaseNames();

	public void setCertificateBaseNames(List<String> certificateBaseNames);

	public Set<IcsMandatoryprofile> getMandatoryProfiles();

	public void setMandatoryProfiles(Set<IcsMandatoryprofile> profiles);

	public void addMandatoryProfile(IcsMandatoryprofile profile);

	public Set<IcsOptionalprofile> getOptionalProfiles();

	public void setOptionalProfiles(Set<IcsOptionalprofile> profiles);

	public void addOptionalProfile(IcsOptionalprofile profile);

	public String getDescription();

	public void setDescription(String description);

	public List<TestCandidate> getCandidates();

	public void setCandidates(List<TestCandidate> candidates);

	public Map<String, String> getVariables();

	public void setVariables(Map<String, String> variables);

	public EidCard getCard();

	public void setCard(EidCard eidCard);

	public EService getEservice();

	public void setEservice(EService eidCard);

	public String getManualExplanation();

	public void setManualExplanation(String manualExplanation);

	public String getClientExplanation();

	public void setClientExplanation(String clientExplanation);

	public List<String> getModule();

	public void setRelevantSteps(List<Pair<String, String>> relevantSteps);

	public List<Pair<String, String>> getRelevantSteps();

	/**
	 * Set the module path. This is an ordered list out of which the module name is created.
	 * <p>
	 * Example:
	 * <li>[0] "A"</li>
	 * <li>[1] "1"</li>
	 * <li>[2] "1"</li>
	 * <p>
	 * becomes
	 * <p>
	 * <i>EIDSERVER_A1_1_&lt;testName&gt;</i>
	 * 
	 * @param modulePath
	 */
	public void setModule(List<String> modulePath);

}
