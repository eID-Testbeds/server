package com.secunet.eidserver.testbed.common.enumerations;

public enum RequestAttribute
{
	DocumentType(1, 8), IssuingState(2, 9), DateOfExpiry(3, 10), GivenNames(4, 11), FamilyNames(5, 12), ArtisticName(6, 13), AcademicTitle(7, 14), DateOfBirth(8, 15), PlaceOfBirth(9,
			16), Nationality(10, 17), BirthName(13, 20), PlaceOfResidence(17, 24), PlaceID(18, 25), ResidencePermitI(19, 26), ResidencePermitII(20, 27);

	private final int datagroupId, chatId;

	private RequestAttribute(int datagroupID, int chatId)
	{
		this.datagroupId = datagroupID;
		this.chatId = chatId;
	}

	/**
	 * Returns the datagroup id as int
	 * 
	 * @return
	 */
	public int getDatagroupId()
	{
		return datagroupId;
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
	 * Returns the enum value that represents the datagroup with the given id
	 * 
	 * @param url
	 */
	public static RequestAttribute getFromId(int id)
	{
		for (RequestAttribute x : RequestAttribute.values())
		{
			if (id == x.getDatagroupId())
			{
				return x;
			}
		}
		return null;
	}

	/**
	 * Returns the enum value that represents the datagroup with the given name. This method is not case sensitive.
	 * 
	 * @param name
	 *            {@link String} The name of the data group
	 * @param ignorePrefix
	 *            {@link boolean} Whether prefixes are to be ignored. This will trigger a comparison of the end of the name to the name of the attribute: name.endsWith(RequestAttribute).
	 * @return
	 */
	public static RequestAttribute getFromName(String name, boolean ignorePrefix)
	{
		for (RequestAttribute x : RequestAttribute.values())
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