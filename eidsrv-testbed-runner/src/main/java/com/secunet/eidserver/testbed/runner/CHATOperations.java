package com.secunet.eidserver.testbed.runner;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;

public class CHATOperations
{
	/**
	 * Creates a complete CHAT given the map of known values.
	 * 
	 * @param knownValues
	 * @return
	 */
	public static String computeCHAT(KnownValues knownValues)
	{
		Set<RequestAttribute> attributeSet = new HashSet<>();
		KnownValues allowedValues = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX);
		Set<SpecialFunction> functionSet = new HashSet<>();
		for (KnownValue value : allowedValues)
		{
			if (GeneralConstants.PERMISSION_ALLOWED.equals(value.getValue()) || GeneralConstants.PERMISSION_REQUIRED.equals(value.getValue()))
			{
				RequestAttribute ra = RequestAttribute.getFromName(value.getName(), true);
				if (null != ra)
				{
					attributeSet.add(ra);
				}
				else
				{
					SpecialFunction sf = SpecialFunction.getFromName(value.getName(), true);
					if (null != sf)
					{
						functionSet.add(sf);
					}
				}
			}
		}
		byte[] chat = computeCHAT(attributeSet, functionSet);
		if (null == chat)
		{
			return "000000000000000000000000";
		}
		else
		{
			return DatatypeConverter.printHexBinary(chat);
		}
	}

	/**
	 * Computes the CHAT given the input data
	 * 
	 * @param attributes
	 * @param ageVerification
	 * @param placeVerification
	 * @param restrictedId
	 * @return
	 */
	public static byte[] computeCHAT(Set<RequestAttribute> attributes, Set<SpecialFunction> functions)
	{
		if (attributes.size() == 0 && functions.size() == 0)
			return null;
		BitSet chat = new BitSet(40);
		// add attribute flags
		attributes.forEach(a -> chat.set(a.getChatId(), true));
		// add special function flags
		functions.forEach(f -> chat.set(f.getChatId(), true));
		return toByteArray(chat);
	}

	// returns the byte array representation of the bitset
	private static byte[] toByteArray(BitSet bits)
	{
		byte[] bytes = new byte[5];
		for (int i = 0; i < bits.length(); i++)
		{
			if (bits.get(i))
			{
				bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
			}
		}
		return bytes;
	}

}
