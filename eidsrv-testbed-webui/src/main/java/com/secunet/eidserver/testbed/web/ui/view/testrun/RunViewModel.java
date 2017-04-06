package com.secunet.eidserver.testbed.web.ui.view.testrun;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.classes.TestRunModel;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

public class RunViewModel implements Serializable
{

	public static final String SELECTED_CANDIDATE = "SELECTED_CANDIDATE";
	public static final String RUNNING_CANDIDATES = "RUNNING_CANDIDATES";
	public static final String CANDIDATES = "CANDIDATES";
	public static final String ROOT_NODE = "ROOT_NODE";

	private static final long serialVersionUID = 1L;

	/** property change support */
	private final PropertyChangeSupport propertyChangeSupport;

	protected Map<String, TestCandidate> candidates;
	protected Map<String, TestRunModel> runningCandidates;
	protected TestCandidate selectedCandidate;
	protected TestModule rootModuleTestCaseNode;
	protected Set<String> selectedTestModulesAndCases = new HashSet<String>();


	public RunViewModel(Collection<TestCandidate> candidates)
	{
		this.candidates = candidates.stream().collect(Collectors.toMap(TestCandidate::getId, c -> c));

		this.runningCandidates = new HashMap<String, TestRunModel>();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * see static final members that start with PROPERTY
	 *
	 * @param listener
	 *            for e.g.{@link ConfigurationModel#PROPERTY_COMPONENT}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/** remove a listener from the list */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public Map<String, TestCandidate> getCandidates()
	{
		return candidates;
	}

	public void setCandidates(Map<String, TestCandidate> candidates)
	{
		Map<String, TestCandidate> old = this.candidates;
		this.candidates = candidates;

		propertyChangeSupport.firePropertyChange(CANDIDATES, old, this.candidates);
	}

	public Map<String, TestRunModel> getRunningCandidates()
	{
		return runningCandidates;
	}

	public void setRunningCandidates(Map<String, TestRunModel> runningCandidates)
	{
		Map<String, TestRunModel> old = this.runningCandidates;
		this.runningCandidates = runningCandidates;

		propertyChangeSupport.firePropertyChange(RUNNING_CANDIDATES, old, this.runningCandidates);
	}

	public TestCandidate getSelectedCandidate()
	{
		return selectedCandidate;
	}

	public void setSelectedCandidate(TestCandidate selectedCandidate)
	{
		TestCandidate old = this.selectedCandidate;
		this.selectedCandidate = selectedCandidate;

		propertyChangeSupport.firePropertyChange(SELECTED_CANDIDATE, old, this.selectedCandidate);
	}

	public TestModule getRootModuleTestCaseNode()
	{
		return rootModuleTestCaseNode;
	}

	public void setRootModuleTestCaseNode(TestModule rootModuleTestCaseNode)
	{
		TestModule old = this.rootModuleTestCaseNode;
		this.rootModuleTestCaseNode = rootModuleTestCaseNode;

		propertyChangeSupport.firePropertyChange(ROOT_NODE, old, this.rootModuleTestCaseNode);
	}

	public boolean isSelected(TestCandidate candidate)
	{
		return selectedCandidate != null && selectedCandidate.getId().equals(candidate.getId());
	}

	public boolean isRunning(TestCandidate candidate)
	{
		// TODO we have to check if the candidate is running,
		// or finished ->
		// or available for
		return runningCandidates.containsKey(candidate.getId());
	}

	public void addRunning(TestCandidate candidate, TestRunModel runModel)
	{
		runningCandidates.put(candidate.getId(), runModel);
	}

	public void removeRunning(TestCandidate candidate)
	{
		runningCandidates.remove(candidate.getId());
	}

}
