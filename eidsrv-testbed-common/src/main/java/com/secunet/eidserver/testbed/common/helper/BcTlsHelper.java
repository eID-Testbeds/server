package com.secunet.eidserver.testbed.common.helper;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.tls.CipherSuite;

import com.secunet.eidserver.testbed.common.constants.Bitlengths;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

public class BcTlsHelper
{

	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Convert a set of given {@link String}s with TLS ciphersuite names to the RFC int.
	 * 
	 * @param cipherSuites
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static int[] convertCipherSuiteStringsToIntArray(List<String> cipherSuites) throws IllegalAccessException
	{
		if (cipherSuites != null && cipherSuites.size() > 0)
		{
			ArrayList<Integer> suiteList = new ArrayList<Integer>();

			for (String cs : cipherSuites)
			{
				if (cs != null)
				{
					try
					{
						int suiteVal = CipherSuite.class.getField(cs).getInt(null);
						suiteList.add(suiteVal);
					}
					catch (NoSuchFieldException e)
					{
						logger.log(Level.WARN, e.getLocalizedMessage());
					}

				}
			}

			int[] suites = new int[suiteList.size()];
			for (int i = 0; i < suites.length; i++)
			{
				suites[i] = suiteList.get(i);
			}

			return suites;
		}
		// input was empty, return empty array as well
		return new int[] {};
	}

	/**
	 * Convert a list of given {@link String}s with TLS ciphersuite names to the RFC int.
	 * 
	 * @param cipherSuites
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static int[] convertCipherSuiteStringsToIntArray(Set<String> cipherSuites) throws IllegalAccessException
	{
		List<String> suiteStrings = new ArrayList<>();
		suiteStrings.addAll(cipherSuites);
		return convertCipherSuiteStringsToIntArray(suiteStrings);
	}

	/**
	 * Convert a set of given {@link IcsTlsCiphersuite}s to the RFC int.
	 * 
	 * @param cipherSuites
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static int[] convertCipherSuiteSetToIntArray(Set<IcsTlsCiphersuite> cipherSuites) throws IllegalAccessException
	{
		Set<String> stringSuites = cipherSuites.stream().map(s -> s.value()).collect(Collectors.toSet());
		return convertCipherSuiteStringsToIntArray(stringSuites);
	}

	public static int[] convertCipherSuiteSetToIntArray(List<IcsTlsCiphersuite> cipherSuites) throws IllegalAccessException
	{
		List<String> stringSuites = cipherSuites.stream().map(s -> s.value()).collect(Collectors.toList());
		return convertCipherSuiteStringsToIntArray(stringSuites);
	}

	/**
	 * Convert a given {@link String} containing a ciphersuite name to the RFC int.
	 * 
	 * @param cipherSuite
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static int convertCipherSuiteToInt(IcsTlsCiphersuite cipherSuite) throws IllegalAccessException
	{
		return convertCipherSuiteStringToInt(cipherSuite.value());
	}

	/**
	 * Convert a given {@link IcsTlsCiphersuite} to the RFC int.
	 * 
	 * @param cipherSuite
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static int convertCipherSuiteStringToInt(String cipherSuite) throws IllegalAccessException
	{
		HashSet<String> cipherSuites = new HashSet<String>();
		cipherSuites.add(cipherSuite);
		return convertCipherSuiteStringsToIntArray(cipherSuites)[0];
	}

	/**
	 * Convert a given RFC ciphersuite int to an {@link IcsTlsCiphersuite}.
	 * 
	 * @param ciphersuite
	 * @return
	 * @throws InvalidParameterException
	 */
	public static IcsTlsCiphersuite convertIntToCiphersuite(int ciphersuite) throws InvalidParameterException
	{
		String cipherString = convertIntToCiphersuiteString(ciphersuite);
		return IcsTlsCiphersuite.fromValue(cipherString);
	}

	/**
	 * Convert a given RFC ciphersuite int to a {@link String} containing the ciphersuite name.
	 * 
	 * @param ciphersuite
	 * @return
	 * @throws InvalidParameterException
	 */
	public static String convertIntToCiphersuiteString(int ciphersuite) throws InvalidParameterException
	{
		int[] arr = { ciphersuite };
		return convertIntArrayToCiphersuiteString(arr).iterator().next();
	}

	/**
	 * Convert a given set of RFC ciphersuite ints to a set of {@link IcsTlsCiphersuite}.
	 * 
	 * @param ciphersuites
	 * @return
	 * @throws InvalidParameterException
	 */
	public static Set<IcsTlsCiphersuite> convertIntArrayToCiphersuiteSet(int[] ciphersuites) throws InvalidParameterException
	{
		Set<String> strings = convertIntArrayToCiphersuiteStringSet(ciphersuites);
		return strings.stream().map(s -> IcsTlsCiphersuite.fromValue(s)).collect(Collectors.toSet());
	}

	/**
	 * Convert a given set of RFC ciphersuite ints to a list of {@link IcsTlsCiphersuite}.
	 * 
	 * @param ciphersuites
	 * @return
	 * @throws InvalidParameterException
	 */
	public static List<IcsTlsCiphersuite> convertIntArrayToCiphersuite(int[] ciphersuites) throws InvalidParameterException
	{
		List<String> strings = convertIntArrayToCiphersuiteString(ciphersuites);
		return strings.stream().map(s -> IcsTlsCiphersuite.fromValue(s)).collect(Collectors.toList());
	}

	/**
	 * Convert a given set of RFC ciphersuite ints to a set of {@link String}s with ciphersuite names.
	 * 
	 * @param ciphersuites
	 * @return
	 * @throws InvalidParameterException
	 */
	public static Set<String> convertIntArrayToCiphersuiteStringSet(int[] ciphersuites) throws InvalidParameterException
	{
		Set<String> cipherStrings = new HashSet<>();
		cipherStrings.addAll(convertIntArrayToCiphersuiteString(ciphersuites));
		return cipherStrings;
	}

	/**
	 * Convert a given set of RFC ciphersuite ints to a list of {@link String}s with ciphersuite names.
	 * 
	 * @param ciphersuites
	 * @return
	 * @throws InvalidParameterException
	 */
	public static List<String> convertIntArrayToCiphersuiteString(int[] ciphersuites) throws InvalidParameterException
	{
		List<String> ciphersuiteStrings = new ArrayList<>();
		for (int cipherSuite : ciphersuites)
		{
			if (CipherSuite.isSCSV(cipherSuite))
				continue; // SCSV is not listed in ICS
			String cipherSuiteString = BouncyCastleTlsHelper.convertCipherSuiteIntToString(cipherSuite);
			if (null == cipherSuiteString)
			{
				throw new InvalidParameterException("No ciphersuite found for input " + cipherSuite);
			}
			ciphersuiteStrings.add(cipherSuiteString);
		}
		return ciphersuiteStrings;
	}

	/**
	 * This helper tests whether a given set of {@link BigInteger} only contains allowed bitlengths (eg. 2048, 3072, 4096, ...)
	 * 
	 * @param toTest
	 *            The set of values to test
	 * @return <i>true</i> iff all items contain allowed values, else <i>false</>
	 */
	public static boolean checkOnlyAllowedBitlengths(Set<BigInteger> toTest)
	{
		if (null == toTest)
		{
			return false;
		}
		boolean overall = true;
		for (BigInteger c : toTest)
		{
			overall &= Bitlengths.ALLOWED_BIT_LENGTHS.contains(c);
		}
		return overall;
	}
}
