package com.secunet.eidserver.testbed.common.classes;

import static org.testng.Assert.assertEquals;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.eidserver.testbed.common.enumerations.EidRequestApdu;
import com.secunet.eidserver.testbed.common.enumerations.EidResponseApdu;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;

public class EidCardsTest
{

	@Test
	public void testBirthday() throws Exception
	{
		LocalDate date = LocalDate.of(1964, 8, 12);
		assertEquals(EidCards.birthday(EidCard.EIDCARD_1), date);
	}

	@Test
	public void testPlace() throws Exception
	{
		assertEquals(EidCards.communityId(EidCard.EIDCARD_1), GeneralConstants.PLACE_COMPARISON_VALUE);
	}

	@Test
	public void testDataGroups() throws Exception
	{
		// DG6 contains data on card 1, but not on card 2
		assertEquals(EidCards.apdus(EidCard.EIDCARD_1).get(EidRequestApdu.READ_DG6).getResponseApdu(), EidResponseApdu.DG6.getResponseApdu());
		assertEquals(EidCards.apdus(EidCard.EIDCARD_2).get(EidRequestApdu.READ_DG6).getResponseApdu(), EidResponseApdu.NOT_ON_CHIP.getResponseApdu());
	}

	@Test
	public void testEfCardAccesss() throws Exception
	{
		assertEquals(EidCards.cardAccess(EidCard.EIDCARD_1), EidCardFiles.EFCARDACCESS.getContent());
	}

	@Test
	public void testEfCardSecurity() throws Exception
	{
		assertEquals(EidCards.cardSecurity(EidCard.EIDCARD_1), EidCardFiles.EFCARDSECURITY_DS_A.getContent());
	}
}
