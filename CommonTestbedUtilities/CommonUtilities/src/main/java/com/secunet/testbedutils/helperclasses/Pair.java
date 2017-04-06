package com.secunet.testbedutils.helperclasses;

import java.io.Serializable;

public class Pair<A, B> implements Serializable
{
	private static final long serialVersionUID = -1494861284538664588L;

	private final A a;
	private final B b;

	public Pair(A first, B second)
	{
		super();
		this.a = first;
		this.b = second;
	}

	@Override
	public int hashCode()
	{
		int hashFirst = a != null ? a.hashCode() : 0;
		int hashSecond = b != null ? b.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Pair)
		{
			@SuppressWarnings("rawtypes")
			Pair other = (Pair) o;
			return (this.getA().equals(other.getA()) && this.getB().equals(other.getB()));
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "(" + a + ", " + b + ")";
	}

	public A getA()
	{
		return a;
	}

	public B getB()
	{
		return b;
	}
}
