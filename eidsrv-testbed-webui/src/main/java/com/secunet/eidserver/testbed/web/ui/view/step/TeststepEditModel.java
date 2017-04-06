package com.secunet.eidserver.testbed.web.ui.view.step;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

public class TeststepEditModel implements Serializable
{

	private static final long serialVersionUID = 1L;

	/** property change support */
	private final PropertyChangeSupport propertyChangeSupport;

	public static final String STEPS = "STEPS";
	public static final String SELECTED = "SELECTED";
	public static final String EDIT = "EDIT";


	protected Collection<TestCaseStep> allSteps = new ArrayList<>();
	protected TestCaseStep selectedStep;
	protected TestCaseStep editableStep;
	protected String oldSuffix;

	public TeststepEditModel(Set<TestCaseStep> allTestCaseSteps)
	{
		propertyChangeSupport = new PropertyChangeSupport(this);

		allSteps = allTestCaseSteps;
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


	public Collection<TestCaseStep> getAllSteps()
	{
		return allSteps;
	}

	public List<TestCaseStep> getAllStepsSorted()
	{
		ArrayList<TestCaseStep> sorted = new ArrayList<>(getAllSteps());

		Collections.sort(sorted, (s1, s2) -> s1.getName().compareTo(s2.getName()));

		return sorted;
	}


	public void setAllSteps(Collection<TestCaseStep> allSteps)
	{
		Collection<TestCaseStep> old = this.allSteps;
		this.allSteps = allSteps;

		propertyChangeSupport.firePropertyChange(STEPS, old, this.allSteps);
	}


	public TestCaseStep getSelectedStep()
	{
		return selectedStep;
	}


	public void setSelectedStep(TestCaseStep selectedStep)
	{
		TestCaseStep old = this.selectedStep;
		this.selectedStep = selectedStep;

		propertyChangeSupport.firePropertyChange(SELECTED, old, this.selectedStep);
	}


	public TestCaseStep getEditableStep()
	{
		return editableStep;
	}

	public String oldSuffix()
	{
		return oldSuffix;
	}

	public void setEditableStep(TestCaseStep editableStep, String oldSuffix)
	{
		this.oldSuffix = oldSuffix;
		TestCaseStep old = this.editableStep;
		this.editableStep = editableStep;

		propertyChangeSupport.firePropertyChange(EDIT, old, this.editableStep);
	}


}
