package com.secunet.eidserver.testbed.web.ui.view.report;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.classes.TestReportModel;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

public class ReportModel implements Serializable
{

	/** generated */
	private static final long serialVersionUID = -5133613227302613637L;

	/** property change support */
	private final PropertyChangeSupport propertyChangeSupport;


	public static final String PROPERTY_CANDIDATE_SELECTION = "CAND_SELC";
	public static final String PROPERTY_TESTRUN_SELECTION = "RUN_SELC";
	public static final String PROPERTY_CANIDATES = "CANDIDATES";


	protected Map<String, TestCandidate> candidatesMap;
	protected TestCandidate selectedCandidate;
	protected TestReportModel selectedTestReport;
	protected TestModule rootTestSetup;

	public ReportModel(Collection<TestCandidate> candidates)
	{
		propertyChangeSupport = new PropertyChangeSupport(this);

		this.candidatesMap = candidates.stream().collect(Collectors.toMap(TestCandidate::getId, c -> c));
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

	public TestCandidate getSelectedCandidate()
	{
		return selectedCandidate;
	}

	public void setSelectedCandidate(TestCandidate selectedCandidate)
	{
		TestCandidate old = this.selectedCandidate;
		this.selectedCandidate = selectedCandidate;

		propertyChangeSupport.firePropertyChange(PROPERTY_CANDIDATE_SELECTION, old, this.selectedCandidate);
	}

	public TestReportModel getSelectedTestReport()
	{
		return selectedTestReport;
	}

	public void setSelectedTestReport(TestReportModel selectedTestReport)
	{
		Object old = this.selectedTestReport;
		this.selectedTestReport = selectedTestReport;

		propertyChangeSupport.firePropertyChange(PROPERTY_TESTRUN_SELECTION, old, this.selectedTestReport);
	}

	public Map<String, TestCandidate> getCandidatesMap()
	{
		return Collections.unmodifiableMap(candidatesMap);
	}

	public void setCandidatesMap(Map<String, TestCandidate> candidates)
	{
		Collection<TestCandidate> old = this.candidatesMap.values();
		this.candidatesMap = candidates;

		propertyChangeSupport.firePropertyChange(PROPERTY_CANIDATES, old, candidates.values());
	}

	public void setCandidates(Collection<TestCandidate> candidates)
	{
		Collection<TestCandidate> old = this.candidatesMap.values();
		this.candidatesMap = candidates.stream().collect(Collectors.toMap(TestCandidate::getId, c -> c));

		propertyChangeSupport.firePropertyChange(PROPERTY_CANIDATES, old, candidates);
	}

	public Collection<TestCandidate> getCandidates()
	{
		return Collections.unmodifiableCollection(candidatesMap.values());
	}

	public void setRootTestSetup(TestModule rootModel)
	{
		rootTestSetup = rootModel;
	}

	public TestModule getRootTestSetup()
	{
		return rootTestSetup;
	}
}
