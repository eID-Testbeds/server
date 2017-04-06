package com.secunet.eidserver.testbed.common.enumerations;

public enum SpecialFunction
{
	AgeVerification(0), PlaceVerification(1), RestrictedID(2);

	private final int chatId;

	private SpecialFunction(int chatId)
	{
		this.chatId = chatId;
	}

	/**
	 * Returns the chat id as int
	 * 
	 * @return
	 */
	public int getChatId()
	{
		return chatId;
	}

	/**
	 * Returns the enum value that represents the special function with the given name. This method is not case sensitive.
	 * 
	 * @param name
	 *            {@link String} The name of the special function
	 * @param ignorePrefix
	 *            {@link boolean} Whether prefixes are to be ignored. This will trigger a comparison of the end of the name to the name of the special function: name.endsWith(SpecialFunction).
	 * @return
	 */
	public static SpecialFunction getFromName(String name, boolean ignorePrefix)
	{
		for (SpecialFunction x : SpecialFunction.values())
		{
			if (ignorePrefix)
			{
				if (name != null && name.toUpperCase().endsWith((x.toString().toUpperCase())))
				{
					return x;
				}
			}
			else
			{
				if (name != null && name.equalsIgnoreCase(x.toString()))
				{
					return x;
				}
			}
		}
		return null;
	}

}
