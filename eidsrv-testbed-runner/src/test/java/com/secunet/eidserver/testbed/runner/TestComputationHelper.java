package com.secunet.eidserver.testbed.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.smartcardio.ResponseAPDU;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;
import com.secunet.testbedutils.eac2.sm.SecureMessaging;

@SuppressWarnings("restriction")
public class TestComputationHelper
{
	public final String EF_CARD_ACCESS = "3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C";

	// auth-token
	public final String CA_PRIVATE = "308202050201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482010F3082010B0201010420A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101";
	// GOV
	public final String SERVER_KEY = "0494AA3838EA3D28031BD96FDF1BF52CCC520322FA8FAA9DA58040E6AF441C05D44DED1A6BB0BE31CF875FFCFAED915E34772B9FCD85E3068E65A9551F10C545D2";
	public final String EAC2_NONCE = "B7E75E9053DACF69";
	public final String EXPECTED_TOKEN = "CB9D53B56EAD82EE";

	// APDU
	// command
	public final String APDU_SET_AT = "0CA4040C1D8711012040D6B2827261B9CF50426851796D7C8E08E8147D6344D60FA000";
	public final String APDU_DOC_VALIDITY = "8C2080001D871101A826F7CDA5B9226F53711879DF606C138E08EED6752A7C9A2D0000";
	public final String APDU_OTHER = "0C2241A41D871101226656C2230751D942B6BA0BE30546078E08FA3FA0CF803BBE1600";
	public final String APDU_RI = "0C86000000014287820131013FDCA8B2C2E6C1148F6859D66E7DD4389AFE6D703A27B6C8B280EC58DE9C6794E34B5105AD623C9D58358CEDD376D8B7AADE959DC42CF352D59138B70D523CFC2B5314CE35F4C7343B6FC221FF49BC4BB89466E5B9EDF91439847AD89329FE4E681ED59BFF6A8F293B3E8AC13766BB27ACEAFB9D18757AF3779893CED541FF68081D41C4749E4C203A751AEEBDE154D9E9926A730B8420D9AF7E6C49FF91CF0827B8CA0A3FB5500DE189BCC7F7504DE5E5A260DD92626D93C919A8D1FE7A5CF9EAA83702495CCEC0BDEBDA4AD14F82D37F0E38521C04F92610500D3180EA206C6EBBC14F2C1DB7F7E5B6399F8480D7B23177496415AE353D30063062C448BB6CB718BAF0AD67772E7258E92D96DA0982A3BBE3C261E5208498773C865912A47991C213043C113E684BEAEA393B2C5D109701248E08D95AD69FB79C1FF00000";
	public final String APDU_DG = "0CB0930000000E970200008E0841FA58E0CE7FACA50000";
	// expected responses
	public final String RESPONSE_SET_AT = "990290008E08E11EE94336ACCEC59000";
	public final String RESPONSE_DOC_VALIDITY = "990290008E083AEB9029B3492C009000";
	public final String RESPONSE_OTHER = "990290008E08FA43FC30D02398A09000";
	public final String RESPONSE_RI = "873101E790C3261AB8B2CEF4AF669073E162F135E167CD380F10A57ECECF418CE2FB00E8EE3908C62759F85CB104A23E79A216990290008E08FF28BA052AE4E3959000";
	public final String RESPONSE_DG = "872101A7359451B1DBCB8CA7F5FF12B870C28E338EB5B29F9129686E7BE3A5C6F3D3F5990290008E08E2E6AF5B6BD9CDBD9000";

	// signature
	public final String SIGNATURE = "A6A1C16AFF69117F551BC14D2A064A723D059B0982FE14E0AE50FDCB4F088A6EA6F9C89BCC6B304AE6323DEE47D42BBE603C699E257FFEDD230337E9773952E3";
	public final String AUXILARY_DATA = "67177315060904007F00070301040253083230313530353331";
	public final String EAC1_CHALLENGE = "531D7F48EBFF2E30";
	public final String EAC1_IDPICC = "2CB7775C2D0D85BEE09A2962F39D8E34FBC5F4AA8FFDB7ABD46A4B41B90E25A8";

	// local
	private ComputationHelper sim;

	@Test
	public void testECParameterSpecExtraction() throws Exception
	{
		sim = new ComputationHelper(EF_CARD_ACCESS, EAC2_NONCE, CA_PRIVATE);
		Assert.assertNotNull(sim.getEcParameterSpec());
	}

	@Test(dependsOnMethods = { "testECParameterSpecExtraction" })
	public void testSignatureValidation() throws Exception
	{
		// read a sample certificate
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/GOV_TERMINAL_CERT.hex"), "UTF-8"));
		String data = new String();
		try
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				data = data.concat(line);
			}
		}
		finally
		{
			br.close();
		}
		Assert.assertTrue(data.length() > 0);
		CVCertificate cert = new CVCertificate(new DataBuffer(Hex.decodeHex(data.toCharArray())));
		sim.setTerminalCertificate(cert);
		boolean valid = sim.isValidSignature(SIGNATURE, EAC1_IDPICC, EAC1_CHALLENGE, SERVER_KEY, AUXILARY_DATA);
		Assert.assertEquals(valid, true);
	}

	@Test(dependsOnMethods = { "testSignatureValidation" })
	public void testAuthenticationToken() throws Exception, SharedSecretNotYetReadyException
	{
		String authenticationToken = sim.computeAuthenticationToken(SERVER_KEY);
		Assert.assertNotNull(authenticationToken);
		Assert.assertEquals(authenticationToken, EXPECTED_TOKEN);
	}

	@Test(dependsOnMethods = { "testAuthenticationToken" })
	public void testApduProcessing() throws Exception
	{
		List<String> apdus = new ArrayList<String>();
		apdus.add(APDU_SET_AT);
		apdus.add(APDU_DOC_VALIDITY);
		apdus.add(APDU_OTHER);
		apdus.add(APDU_RI);
		apdus.add(APDU_DG);
		String result = sim.handleApduList(apdus, EidCard.EIDCARD_1, null, null);
		Assert.assertNotNull(result);

		// get generated responses
		String response = sim.getPreviousApdus();
		Assert.assertEquals(response,
				("<OutputAPDU>" + RESPONSE_SET_AT + "</OutputAPDU>" + System.getProperty("line.separator") + "<OutputAPDU>" + RESPONSE_DOC_VALIDITY + "</OutputAPDU>"
						+ System.getProperty("line.separator") + "<OutputAPDU>" + RESPONSE_OTHER + "</OutputAPDU>" + System.getProperty("line.separator") + "<OutputAPDU>" + RESPONSE_RI
						+ "</OutputAPDU>") + System.getProperty("line.separator") + "<OutputAPDU>" + RESPONSE_DG + "</OutputAPDU>");
	}


	@Test(dependsOnMethods = { "testAuthenticationToken" })
	public void testEncryptAndOrMac() throws Exception
	{
		Field secureMessagingField = ComputationHelper.class.getDeclaredField("sm");
		secureMessagingField.setAccessible(true);
		SecureMessaging secMsg = (SecureMessaging) secureMessagingField.get(sim);

		String plainResponseApdu = "650C0C0A4D55535445524D414E4E9000"; // data group 5
		ResponseAPDU plainResponse = new ResponseAPDU(DatatypeConverter.parseHexBinary(plainResponseApdu));
		ResponseAPDU responseAPDU = null;
		// Default: encrypt (0x87) and MAC (0x8e)
		String expectedAPDU = "871101F9676765D81B91CF260D2FF303C15721990290008E08E07FE19AEF7441D19000";
		responseAPDU = sim.encryptAndOrMac(plainResponse, true, true);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		responseAPDU = secMsg.encrypt(plainResponse);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		boolean useDecreasedSSCForMAC = true;
		responseAPDU = sim.encryptAndOrMac(plainResponse, true, true, useDecreasedSSCForMAC);
		Assert.assertNotEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);

		// Default: MAC only
		expectedAPDU = "990290008E084DBCC35BF07363989000";
		responseAPDU = sim.encryptAndOrMac(plainResponse, false, true);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		responseAPDU = secMsg.mac(plainResponse);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		responseAPDU = sim.encryptAndOrMac(plainResponse, false, true, useDecreasedSSCForMAC);
		Assert.assertNotEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);

		// encrypt, but skip MAC generation
		expectedAPDU = "871101F9676765D81B91CF260D2FF303C15721990290009000";
		responseAPDU = sim.encryptAndOrMac(plainResponse, true, false);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		responseAPDU = sim.encryptAndOrMac(plainResponse, true, false, useDecreasedSSCForMAC);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);

		// skip both
		expectedAPDU = "990290009000";
		responseAPDU = sim.encryptAndOrMac(plainResponse, false, false);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
		responseAPDU = sim.encryptAndOrMac(plainResponse, false, false, useDecreasedSSCForMAC);
		Assert.assertEquals(DatatypeConverter.printHexBinary(responseAPDU.getBytes()), expectedAPDU);
	}

	@Test(dependsOnMethods = { "testECParameterSpecExtraction" })
	public void testCvValidationDefaultChain() throws Exception
	{
		Map<String, CVCertificate> cvCerts = new HashMap<>();
		cvCerts.put("DV", loadFromHex("CERT_ECARD_CV_DV_1_A.cvcert"));
		cvCerts.put("TERM", loadFromHex("CERT_ECARD_CV_TERM_1_A.cvcert"));
		Result validateResult = sim.validateCVcertificates(cvCerts, 2, EService.A);
		Assert.assertNotNull(validateResult);
		Assert.assertEquals(validateResult.wasSuccessful(), true);
	}

	private CVCertificate loadFromHex(String certificateName)
	{
		File f = new File(Thread.currentThread().getContextClassLoader().getResource("CVCertificates/certs/" + certificateName).getFile());
		try
		{
			DataBuffer cvBuffer = new DataBuffer(IOUtils.toByteArray(new FileInputStream(f)));
			return new CVCertificate(cvBuffer);
		}
		catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	// private void addSSC(byte[] sendSequenceCounter, int a)
	// {
	// for (int i = sendSequenceCounter.length - 1; i >= 0 && a > 0; i--)
	// {
	// a += sendSequenceCounter[i] & 0xFF;
	// sendSequenceCounter[i] = (byte) a;
	// a >>>= 8;
	// }
	// }
	//
	//
	// @Test
	// public void testSSC() throws Exception
	// {
	// int blockLength = 16;
	//
	// byte[] sendSequenceCounter = new byte[blockLength];
	// addSSC(sendSequenceCounter, 1);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// addSSC(sendSequenceCounter, 1);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// sim.decSSC(sendSequenceCounter, 1);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// addSSC(sendSequenceCounter, 9);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// sim.decSSC(sendSequenceCounter, 9);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// sim.decSSC(sendSequenceCounter, 1);
	// System.out.println(ByteHelper.toHexString(sendSequenceCounter));
	// }

}