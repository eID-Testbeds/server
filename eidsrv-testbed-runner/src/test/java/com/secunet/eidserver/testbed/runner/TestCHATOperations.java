package com.secunet.eidserver.testbed.runner;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;

public class TestCHATOperations
{
	private final String expectedCHAT = "000F13FF03";
	private final String dgChat = "0000001200";
	private final String ageVerificationChat = "0000000001";
	private final String placeVerificationChat = "0000000002";

	@Test
	public void testCHATGeneration() throws Exception
	{
		KnownValues knownValues = new KnownValues();
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + RequestAttribute.FamilyNames.toString().toUpperCase(), GeneralConstants.PERMISSION_ALLOWED));
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + RequestAttribute.IssuingState.toString().toUpperCase(), GeneralConstants.PERMISSION_ALLOWED));
		String chat = CHATOperations.computeCHAT(knownValues);
		Assert.assertEquals(chat, dgChat);
	}

	@Test
	public void testMandatoryCHATGeneration() throws Exception
	{
		KnownValues knownValues = new KnownValues();
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + RequestAttribute.FamilyNames.toString(), GeneralConstants.PERMISSION_ALLOWED));
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + RequestAttribute.IssuingState.toString(), GeneralConstants.PERMISSION_ALLOWED));
		String chat = CHATOperations.computeCHAT(knownValues);
		Assert.assertEquals(chat, dgChat);
	}

	@Test
	public void testAgeVerificationChat() throws Exception
	{
		KnownValues knownValues = new KnownValues();
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		String chat = CHATOperations.computeCHAT(knownValues);
		Assert.assertEquals(chat, ageVerificationChat);
	}

	@Test
	public void testPlaceVerificationChat() throws Exception
	{
		KnownValues knownValues = new KnownValues();
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.PlaceVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		String chat = CHATOperations.computeCHAT(knownValues);
		Assert.assertEquals(chat, placeVerificationChat);
	}

	@Test
	public void testEverything() throws Exception
	{
		KnownValues knownValues = new KnownValues();
		// data groups
		for (RequestAttribute r : RequestAttribute.values())
		{
			knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + r.toString().toUpperCase(), GeneralConstants.PERMISSION_ALLOWED));
		}
		// special functions
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		knownValues.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.PlaceVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		String chat = CHATOperations.computeCHAT(knownValues);
		Assert.assertEquals(chat, expectedCHAT);
	}

}
