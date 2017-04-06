package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseTestingPojo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String id;

	public BaseTestingPojo()
	{
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof BaseTestingPojo))
		{
			return false;
		}
		BaseTestingPojo other = (BaseTestingPojo) obj;
		return getId().equals(other.getId());
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}