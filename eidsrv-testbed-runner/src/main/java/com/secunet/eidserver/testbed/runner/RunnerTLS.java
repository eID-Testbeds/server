package com.secunet.eidserver.testbed.runner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.DefaultTlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.beans.TlsTester;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class RunnerTLS
{
	private static final Logger logger = LogManager.getLogger(RunnerTLS.class);
	private List<LogMessage> result = new ArrayList<LogMessage>();

	private final TlsTester tlsTester;

	public RunnerTLS(TlsTester tlsTester)
	{
		this.tlsTester = tlsTester;
	}

	public List<LogMessage> runTest(TestCandidate testCandidate, TestCase testCase, KnownValues values, Certificate clientCert, AsymmetricKeyParameter clientKey)
	{
		return runTest(testCandidate, testCase, values, clientCert, clientKey, 0);
	}

	public List<LogMessage> runTest(TestCandidate testCandidate, TestCase testCase, KnownValues values, Certificate clientCert, AsymmetricKeyParameter clientKey, int firstTlsStepIndex)
	{
		URL url = determineTarget(testCandidate, testCase);
		String host = url.getHost();
		int port = url.getPort();

		// This functionality is currently unused.
		// byte[] outData = generateOutData(testCandidate, testCase, values);
		// byte[] inData = generateInData(testCandidate, testCase, values);
		byte[] outData = null;
		byte[] inData = null;

		try
		{
			TlsTestParameters parameters = generateTlsTestParameters(testCase, values, clientCert, clientKey, firstTlsStepIndex);
			result = tlsTester.runTlsTest(host, port, parameters, outData, inData);
		}
		catch (IOException | DecoderException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(new StringWriter()));
			logger.error(trace.toString());
		}
		return result;
	}

	private boolean isCrypto(TestCase testCase)
	{
		// Profil CRYPTO
		return testCase.getMandatoryProfiles().contains(IcsMandatoryprofile.CRYPTO);
	}

	private boolean isSoapTls(TestCase testCase)
	{
		// Profil SOAP_TLS
		return testCase.getOptionalProfiles().contains(IcsOptionalprofile.SOAP_TLS);
	}

	private boolean isSaml(TestCase testCase)
	{
		// Profil SAML
		return testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML);
	}

	private boolean isTlsPsk(TestCase testCase)
	{
		// Profil TLS_PSK
		return testCase.getOptionalProfiles().contains(IcsOptionalprofile.TLS_PSK);
	}

	private boolean isAttached(TestCase testCase)
	{
		// Profil ESER_ATTACHED
		return testCase.getOptionalProfiles().contains(IcsOptionalprofile.ESER_ATTACHED);
	}

	private boolean isEidasMW(TestCase testCase)
	{
		// Profil EIDAS_MW
		return testCase.getOptionalProfiles().contains(IcsOptionalprofile.EIDAS_MW);
	}

	private URL determineTarget(TestCandidate testCandidate, TestCase testCase)
	{
		URL url = null;
		if (isCrypto(testCase))
		{
			if (isSoapTls(testCase))
			{
				// Module A3 eID interface
				url = testCandidate.getEidinterfaceUrl();
			}
			else if (isSaml(testCase))
			{
				// Module B3 SAML processor
				url = testCandidate.getSamlUrl();
			}
			else if (isTlsPsk(testCase))
			{
				// Module C2_1 eCardAPI - PSK
				url = testCandidate.getEcardapiUrl();
			}
			else if (isAttached(testCase))
			{
				// Module C2_2 eCardAPI - Attached
				url = testCandidate.getAttachedTcTokenUrl();
			}
			else if (isEidasMW(testCase))
			{
				// Module D3
				url = testCandidate.getAttachedTcTokenUrl();
			}
		}
		return url;

	}

	private TlsTestParameters generateTlsTestParameters(TestCase testCase, KnownValues values, Certificate clientCert, AsymmetricKeyParameter clientKey, int firstTlsStepIndex) throws DecoderException
	{
		DefaultTlsTestParameters tlsTestParameters = new DefaultTlsTestParameters();
		List<TestCaseStep> testCaseSteps = testCase.getTestCaseSteps();
		TestCaseStep firstTestCaseStep = testCaseSteps.get(firstTlsStepIndex); // out-step with tlsStepTokens
		String tokenString = firstTestCaseStep.getMessage();

		tokenString = resolvePlaceholders(values, tokenString);


		Step step = JaxBUtil.unmarshal(tokenString, Step.class);

		// process PSK parameters from KnownValues
		byte[] pskId = null, pskKey = null;
		if (values.containsElement(Replaceable.PSK.toString()) && values.containsElement(Replaceable.SESSIONIDENTIFIER.toString()))
		{
			pskKey = Hex.decodeHex(values.get(Replaceable.PSK.toString()).getValue().toCharArray());
			pskId = values.get(Replaceable.SESSIONIDENTIFIER.toString()).getValue().getBytes();
		}

		// process tls tokens
		for (StepToken stepToken : step.getTlsStepToken())
		{
			if (!stepToken.getValue().startsWith("[") && !stepToken.getValue().endsWith("]"))
			{
				switch (stepToken.getName())
				{
					case "Version":
						tlsTestParameters.setVersion(BouncyCastleTlsHelper.convertProtocolVersionStringToObject(stepToken.getValue()));
						break;
					case "SelectedVersion":
						tlsTestParameters.setSelectedVersion(BouncyCastleTlsHelper.convertProtocolVersionStringToObject(stepToken.getValue()));
						break;
					case "CipherSuites":
						tlsTestParameters.setCipherSuites(getConvertedListAsArray(stepToken.getValue(), BouncyCastleTlsHelper::convertCipherSuiteStringToInt));
						break;
					case "SelectedCipherSuite":
						tlsTestParameters.setSelectedCipherSuite(BouncyCastleTlsHelper.convertCipherSuiteStringToInt(stepToken.getValue()));
						break;
					case "EllipticCurves":
						tlsTestParameters.setEllipticCurves(getConvertedListAsArray(stepToken.getValue(), BouncyCastleTlsHelper::convertNamedCurveStringToInt));
						break;
					case "SupportedSignatureAlgorithms":
						tlsTestParameters.setSupportedSignatureAlgorithms(getConvertedListAsVector(stepToken.getValue(), BouncyCastleTlsHelper::convertSignatureAndHashAlgorithmStringToClass));
						break;
					case "AlertLevelReceived":
						tlsTestParameters.setAlertLevelReceived(Short.parseShort(stepToken.getValue()));
						break;
					case "EncryptThenMACExtension":
						tlsTestParameters.setEncryptThenMACExtension(Boolean.parseBoolean(stepToken.getValue()));
						break;
					case "EncryptThenMACExtensionEnabled":
						tlsTestParameters.setEncryptThenMACExtensionEnabled(Boolean.parseBoolean(stepToken.getValue()));
						break;
					case "PskIdentity":
						pskId = Hex.decodeHex(stepToken.getValue().toCharArray());
						break;
					case "PskKey":
						pskKey = Hex.decodeHex(stepToken.getValue().toCharArray());
						break;
					case "ClientAuthentication":
						tlsTestParameters.setClientAuthentication(Boolean.parseBoolean(stepToken.getValue()));
						break;
					default:
						// unknown TlsToken
						throw new IllegalArgumentException("Invalid TlsToken: " + stepToken.getName());
				}
			}
		}

		if (pskId != null && pskKey != null)
		{
			tlsTestParameters.setPSK(pskId, pskKey);
		}
		if (tlsTestParameters.isClientAuthentication())
		{
			// if client authentication is activated, the configured credentials are used
			tlsTestParameters.setClientCertificate(clientCert);
			tlsTestParameters.setClientPrivateKey(clientKey);
		}
		return tlsTestParameters;
	}

	// private byte[] generateDataFromMessage(TestCandidate testCandidate, TestCase testCase, KnownValues values, int stepIndex)
	// {
	// byte[] data = null;
	//
	// List<TestCaseStep> testCaseSteps = testCase.getTestCaseSteps();
	// if (testCaseSteps.size() > stepIndex)
	// {
	// TestCaseStep testCaseStep = testCaseSteps.get(stepIndex);
	// String tokenString = testCaseStep.getMessage();
	//
	// tokenString = resolvePlaceholders(values, tokenString);
	//
	// Step step = JaxBUtil.unmarshal(tokenString, Step.class);
	//
	// if (step != null && step.getHttpStepToken() != null && step.getHttpStepToken().size() > 0 && step.getHttpStepToken().get(0) != null && step.getHttpStepToken().get(0).getValue() != null)
	// {
	// data = step.getHttpStepToken().get(0).getValue().getBytes(StandardCharsets.UTF_8);
	// }
	// }
	// return data;
	// }
	//
	//
	// private byte[] generateOutData(TestCandidate testCandidate, TestCase testCase, KnownValues values)
	// {
	// return generateDataFromMessage(testCandidate, testCase, values, 0); // step 0 => out-step
	// }
	//
	// private byte[] generateInData(TestCandidate testCandidate, TestCase testCase, KnownValues values)
	// {
	// return generateDataFromMessage(testCandidate, testCase, values, 1); // step 1 => in-step
	// }

	private String resolvePlaceholders(KnownValues values, String tokenString)
	{
		return StepHandler.insertValues(values, tokenString);
	}

	private int[] getConvertedListAsArray(String prop, Function<String, Integer> converter)
	{
		int[] list = new int[0];
		if (prop != null && !prop.trim().isEmpty())
		{
			String[] parts = prop.split(",");
			list = new int[parts.length];
			for (int i = 0; i < parts.length; i++)
			{
				list[i] = converter.apply(parts[i].trim());
			}
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector getConvertedListAsVector(String prop, Function<String, SignatureAndHashAlgorithm> converter)
	{
		Vector vec = new Vector();
		if (prop != null && !prop.trim().isEmpty())
		{
			String[] parts = prop.split(",");
			for (int i = 0; i < parts.length; i++)
			{
				vec.add(converter.apply(parts[i].trim()));
			}
		}
		return vec;
	}

}
