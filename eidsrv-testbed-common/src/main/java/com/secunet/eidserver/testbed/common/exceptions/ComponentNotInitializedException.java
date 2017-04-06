package com.secunet.eidserver.testbed.common.exceptions;

import javax.ejb.EJBException;

public class ComponentNotInitializedException extends EJBException
{
	private static final long serialVersionUID = 221731287543279686L;

	public ComponentNotInitializedException(final String componentName)
	{
		super(String.format("Component %s not initialized", componentName));
	}


}
