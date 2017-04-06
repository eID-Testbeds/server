package com.secunet.eidserver.testbed.web.ui.view.testcase;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

public class TestCaseEditModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** property change support */
	private final PropertyChangeSupport propertyChangeSupport;

	public static final String PROPERTY_TESTCASES = "testcases";
	public static final String PROPERTY_SELECTED_TESTCASE = "SELECTED_TESTCASE";
	public static final String PROPERTY_EDIT_TESTCASE = "EDIT_TESTCASE";
	public static final String PROPERTY_CANDIDATES = "CANDIDATES";


	protected List<TestCase> testCases;
	protected List<TestCandidate> candidates;
	protected Set<TestCaseStep> steps;
	protected TestCase selectedTestCase;
	protected CopyTestCase editTestCase;


	public TestCaseEditModel(Collection<TestCase> testcases, Collection<TestCandidate> candidates, Set<TestCaseStep> allSteps)
	{
		if (testcases == null)
			throw new IllegalArgumentException("testcases must not be null");
		if (candidates == null)
			throw new IllegalArgumentException("candidates must not be null");
		if (allSteps == null)
			throw new IllegalArgumentException("allSteps must not be null");


		propertyChangeSupport = new PropertyChangeSupport(this);

		this.testCases = new ArrayList<>(testcases);
		this.candidates = new ArrayList<>(candidates);
		this.steps = new HashSet<>(allSteps);
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


	/**
	 * @return never null, can be empty unmodifiable!
	 */
	public @Nonnull List<TestCase> getTestCasesSorted()
	{
		List<TestCase> sorted = new ArrayList<>(testCases);

		if (sorted.size() > 1)
		{
			Collections.sort(sorted, (t1, t2) -> t1.getName().compareTo(t2.getName()));
		}

		return Collections.unmodifiableList(sorted);
	}

	public @Nonnull Collection<TestCase> getTestCases()
	{
		return testCases;
	}


	public void setSelectedTestCase(@Nullable TestCase value)
	{
		TestCase old = this.selectedTestCase;
		this.selectedTestCase = value;

		propertyChangeSupport.firePropertyChange(PROPERTY_SELECTED_TESTCASE, old, this.selectedTestCase);
	}

	public @CheckForNull TestCase getSelectedTestCase()
	{
		return this.selectedTestCase;
	}

	public void setEditTestCase(@Nullable CopyTestCase value)
	{
		TestCase old = this.editTestCase;
		this.editTestCase = value;

		propertyChangeSupport.firePropertyChange(PROPERTY_EDIT_TESTCASE, old, this.editTestCase);
	}

	public @CheckForNull CopyTestCase getEditTestCase()
	{
		return this.editTestCase;
	}


	public @Nonnull List<TestCandidate> getCandidatesSorted()
	{
		List<TestCandidate> sorted = new ArrayList<>(candidates);

		if (sorted.size() > 1)
		{
			Collections.sort(sorted, (t1, t2) -> t1.getCandidateName().compareTo(t2.getCandidateName()));
		}

		return Collections.unmodifiableList(sorted);
	}

	public @Nonnull List<TestCandidate> getCandidates()
	{
		return candidates;
	}

	public void setCandidates(@Nonnull List<TestCandidate> candidates)
	{
		List<TestCandidate> old = candidates;
		this.candidates = candidates;

		propertyChangeSupport.firePropertyChange(PROPERTY_CANDIDATES, old, this.candidates);
	}


	public @Nonnull Set<TestCaseStep> getSteps()
	{
		return steps;
	}

	public @Nonnull List<TestCaseStep> getStepsSorted()
	{
		List<TestCaseStep> sorted = new ArrayList<>(steps);

		if (sorted.size() > 1)
		{
			Collections.sort(sorted, (t1, t2) -> t1.getName().compareTo(t2.getName()));
		}

		return sorted;
	}


	public void setSteps(@Nonnull Set<TestCaseStep> steps)
	{
		this.steps = steps;
	}

}
