package com.secunet.eidserver.testbed.common.classes;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;

public class TestModule
{
	private String name;
	private List<TestModule> modules = new ArrayList<>();
	private List<TestCase> testCases = new ArrayList<>();

	@SuppressWarnings("unused")
	private TestModule()
	{

	}

	public TestModule(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<TestModule> getSubModules()
	{
		return modules;
	}

	/**
	 * Get the child module with the given name. In case no such module exists, <i>null</i> is returned.
	 * 
	 * @param name
	 * @return
	 */
	public TestModule getSubModule(String name) throws InvalidParameterException
	{
		if (name == null)
		{
			throw new InvalidParameterException("Module name may not be null.");
		}
		TestModule t = null;
		for (TestModule child : modules)
		{
			if (name.equals(child.getName()))
			{
				t = child;
				break;
			}
		}
		return t;
	}

	public List<TestCase> getTestCases()
	{
		return testCases;
	}

	public void setSubModules(List<TestModule> testModules)
	{
		this.modules = testModules;
	}

	public void setTestCases(List<TestCase> testCases)
	{
		this.testCases = testCases;
	}

	public void addSubModule(TestModule module)
	{
		this.modules.add(module);
	}

	public void addTestCase(TestCase testcase)
	{
		this.testCases.add(testcase);
	}
}