package com.secunet.eidserver.testbed.runner;

/**
 * A class holding all runtime values known to the testbed at the given time
 * 
 *
 */
public class KnownValue
{
	private final String name;
	private String value;

	/**
	 * Create a known value without assigning a value
	 * 
	 * @param name
	 */
	public KnownValue(String name)
	{
		this.name = name;
	}

	/**
	 * Create a known value
	 * 
	 * @param name
	 * @param value
	 */
	public KnownValue(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Get a placeholder string for replacement in messages
	 * 
	 * @return
	 */
	public String getPlaceholder()
	{
		return "[" + name + "]";
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return 53 * this.getName().hashCode();
	}

	@Override
	public String toString()
	{
		return getName() + ": " + getValue();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof KnownValue)
		{
			KnownValue k = (KnownValue) other;
			return (k.getName().equals(this.getName())) ? true : false;
		}
		else
		{
			return false;
		}
	}

}

