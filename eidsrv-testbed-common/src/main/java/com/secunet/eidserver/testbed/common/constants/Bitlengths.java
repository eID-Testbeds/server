/**
 * 
 */
package com.secunet.eidserver.testbed.common.constants;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class Bitlengths
{
	public static final List<BigInteger> ALLOWED_BIT_LENGTHS = new ArrayList<>();

	static
	{
		ALLOWED_BIT_LENGTHS.add(new BigInteger("2048", 10));
		ALLOWED_BIT_LENGTHS.add(new BigInteger("3072", 10));
		ALLOWED_BIT_LENGTHS.add(new BigInteger("4096", 10));
	}
}
