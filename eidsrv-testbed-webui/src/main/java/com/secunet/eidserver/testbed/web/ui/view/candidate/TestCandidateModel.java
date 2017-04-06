package com.secunet.eidserver.testbed.web.ui.view.candidate;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

public class TestCandidateModel implements Serializable
{

	/** generated */
	private static final long serialVersionUID = -3598100647621726165L;


	public static final String PROPERTY_TESTCANDIDATE_SELECTION = "CANDIDATE_SELECTION";


	/** property change support */
	private final PropertyChangeSupport propertyChangeSupport;

	protected TestCandidate TestCandidate;

	public TestCandidateModel()
	{
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public TestCandidate getTestCandidate()
	{
		return TestCandidate;
	}

	public void setTestCandidate(TestCandidate TestCandidate)
	{
		TestCandidate old = this.TestCandidate;
		this.TestCandidate = TestCandidate;

		propertyChangeSupport.firePropertyChange(PROPERTY_TESTCANDIDATE_SELECTION, old, this.TestCandidate);
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
}
