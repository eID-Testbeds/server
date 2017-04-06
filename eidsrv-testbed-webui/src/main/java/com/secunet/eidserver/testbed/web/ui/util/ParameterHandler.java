package com.secunet.eidserver.testbed.web.ui.util;

import java.util.Optional;

public class ParameterHandler
{
	/** use to seperate the path "edit/<uuid>" */
	public static final String PATH_SEPERATOR = "/";
	public static final String CREATE = "create";
	/** usage: "edit/<uuid>" */
	public static final String EDIT = "edit";


	private final String parameters;


	public ParameterHandler(final String parameters)
	{
		this.parameters = parameters;
	}


	public boolean isEdit()
	{
		return parameters != null && !parameters.isEmpty() && parameters.contains(EDIT);
	}


	public boolean isCreate()
	{
		return parameters != null && !parameters.isEmpty() && parameters.contains(CREATE);
	}

	public Optional<String> getAssociatedUUID()
	{
		Optional<String> uuid = Optional.empty();

		if (isEdit() && (parameters.lastIndexOf(PATH_SEPERATOR) > 0 && parameters.lastIndexOf(PATH_SEPERATOR) < parameters.length()))
			uuid = Optional.of(parameters.substring(parameters.lastIndexOf(PATH_SEPERATOR) + 1));

		return uuid;
	}


}

