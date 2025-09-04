package com.secunet.eidserver.testbed.common.classes;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.eidserver.testbed.common.enumerations.EidRequestApdu;
import com.secunet.eidserver.testbed.common.enumerations.EidResponseApdu;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;

public class EidCardsTest
{

	@BeforeClass
	public void setupSuite() throws Exception
	{
		EidCards.initCards(null);
	}
	@Test
	public void testBirthday() throws Exception
	{
		LocalDate date = LocalDate.of(1964, 8, 12);
		assertEquals(EidCards.birthday(EidCard.EIDCARD_1), date);
		LocalDate date2 = LocalDate.of(1960, 12, 31);
		assertEquals(EidCards.birthday(EidCard.EIDCARD_3), date2);
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
		// todo: fix this test, left side of the assertion uses the newly generated and signed certificate, which wont work as there is no generated cert when running tests
		//assertEquals(EidCards.cardSecurity(EidCard.EIDCARD_1).substring(0, 50), EidCardFiles.EFCARDSECURITY_DS_A.getContent().substring(0, 50));
	}
}
