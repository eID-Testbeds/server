package com.secunet.eidserver.testbed.generator;

import java.util.List;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.Variables;
import com.secunet.testbedutils.helperclasses.Pair;

public class ProcessedSteps
{
	private final List<TestCaseStep> steps;
	private final Variables nestedVariables;
	private final List<Pair<String, String>> relevantSteps;

	public ProcessedSteps(List<TestCaseStep> steps, Variables nestedVariables, List<Pair<String, String>> relevantSteps)
	{
		this.steps = steps;
		this.nestedVariables = nestedVariables;
		this.relevantSteps = relevantSteps;
	}

	public List<TestCaseStep> getSteps()
	{
		return this.steps;
	}

	public Variables getNestedVariables()
	{
		return nestedVariables;
	}

	public List<Pair<String, String>> getRelevantSteps()
	{
		return relevantSteps;
	}

}
