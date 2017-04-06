package com.secunet.eidserver.testbed.runner;

import java.util.Optional;

public class Result<T>
{

	private final String message;
	private final boolean success;
	private Optional<T> computedValue = Optional.empty();

	public Result(boolean success, String message)
	{
		this.success = success;
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public boolean wasSuccessful()
	{
		return success;
	}

	/**
	 * @return the computedValue
	 */
	public Optional<T> getComputed()
	{
		return computedValue;
	}

	/**
	 * @param computedValue
	 *            the computedValue to set
	 */
	public void setComputed(T computed)
	{
		this.computedValue = Optional.of(computed);
	}

}