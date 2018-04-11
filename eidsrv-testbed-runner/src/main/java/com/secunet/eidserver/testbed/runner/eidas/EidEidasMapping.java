package com.secunet.eidserver.testbed.runner.eidas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;

import de.governikus.eidassaml.starterkit.EidasNaturalPersonAttributes;

/**
 * A mapping of all attributes supported by eIDAS to the data groups and special funcitons of the german eID card.
 * 
 *
 */
public class EidEidasMapping
{
	private final static Map<EidasNaturalPersonAttributes, Set<String>> mapping;

	static
	{
		mapping = new HashMap<EidasNaturalPersonAttributes, Set<String>>();
		mapping.put(EidasNaturalPersonAttributes.FirstName, Stream.of(RequestAttribute.GivenNames.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.FamilyName, Stream.of(RequestAttribute.FamilyNames.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.DateOfBirth, Stream.of(RequestAttribute.DateOfBirth.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.PersonIdentifier, Stream.of(SpecialFunction.RestrictedID.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.PlaceOfBirth, Stream.of(RequestAttribute.PlaceOfBirth.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.BirthName,
				Stream.of(RequestAttribute.GivenNames.toString(), RequestAttribute.BirthName.toString(), RequestAttribute.FamilyNames.toString()).collect(Collectors.toSet()));
		mapping.put(EidasNaturalPersonAttributes.CurrentAddress, Stream.of(RequestAttribute.PlaceOfResidence.toString()).collect(Collectors.toSet()));
	}

	/**
	 * Returns a {@link Set} of the names which represent either data groups or special functions required to the read in order to answer a request for the given {@link EidasNaturalPersonAttributes}
	 * 
	 * @param requestedEidasAttribute
	 *            The requested {@link EidasNaturalPersonAttributes}
	 * @return A set of names which represent either German eID data groups or special functions (e.g. Restricted ID)
	 */
	public static final Set<String> getCorrespondingEidAttributes(EidasNaturalPersonAttributes requestedEidasAttribute)
	{
		if (requestedEidasAttribute == EidasNaturalPersonAttributes.Gender)
		{
			return new HashSet<>();
		}

		return mapping.get(requestedEidasAttribute);
	}

	/**
	 * Returns a {@link Set} of the names which represent either data groups or special functions required to the read in order to answer a request for the given eIDAS attribute name
	 * 
	 * @param requestedEidasAttributeName
	 *            The namne of the requested eIDAS attribute
	 * @return A set of names which represent either German eID data groups or special functions (e.g. Restricted ID)
	 */
	public static final Set<String> getCorrespondingEidAttributes(String requestedEidasAttributeName)
	{
		return getCorrespondingEidAttributes(EidasNaturalPersonAttributes.valueOf(requestedEidasAttributeName));
	}

}
