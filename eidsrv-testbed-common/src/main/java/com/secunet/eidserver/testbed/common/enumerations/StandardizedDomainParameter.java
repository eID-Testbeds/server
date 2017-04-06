package com.secunet.eidserver.testbed.common.enumerations;

/**
 * StandardardizedDomainParameters according to TR-3110-3, A2.1.1.1
 *
 */
public enum StandardizedDomainParameter
{
	secp192r1(8), BrainpoolP192r1(9), secp224r1(10), BrainpoolP224r1(11), secp256r1(12), BrainpoolP256r1(13), BrainpoolP320r1(14), secp384r1(15), BrainpoolP384r1(16), BrainpoolP512r1(17), secp521r1(
			18);

	private final int id;

	private StandardizedDomainParameter(int id)
	{
		this.id = id;
	}

	/**
	 * Returns the domain param id as int
	 * 
	 * @return
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns the enum value that represents the standardized domain parameter with the
	 * given name
	 * 
	 * @param id
	 */
	public static StandardizedDomainParameter getFromId(int id)
	{
		for (StandardizedDomainParameter x : StandardizedDomainParameter.values())
		{
			if (id == x.getId())
			{
				return x;
			}
		}
		return null;
	}

}
