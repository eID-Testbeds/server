package com.secunet.eidserver.testbed.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

import com.secunet.eidserver.testbed.common.classes.EidCards;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.eidserver.testbed.common.enumerations.EidRequestApdu;
import com.secunet.eidserver.testbed.common.enumerations.EidResponseApdu;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SignatureAlgorithm;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.enumerations.StandardizedDomainParameter;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorization;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.CVPubKeyHolder;
import com.secunet.testbedutils.cvc.cvcertificate.CertHolderRole;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidKeySourceException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVMissingKeyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVUnknownAlgorithmException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVUnknownCryptoProviderException;
import com.secunet.testbedutils.eac2.DHDomainParameter.Type;
import com.secunet.testbedutils.eac2.EAC2ObjectIdentifiers;
import com.secunet.testbedutils.eac2.cv.TLVObject;
import com.secunet.testbedutils.eac2.sm.AuthenticationToken;
import com.secunet.testbedutils.eac2.sm.EIDCryptoException;
import com.secunet.testbedutils.eac2.sm.KeyDerivationFunction;
import com.secunet.testbedutils.eac2.sm.SecureMessaging;
import com.secunet.testbedutils.utilities.CommonUtil;

@SuppressWarnings("restriction")
public class ComputationHelper
{
	private static final Logger logger = LogManager.getLogger(ComputationHelper.class);

	// the terminal certificate
	private CVCertificate terminalCertificate = null;

	// secure messaging
	private SecureMessaging sm;
	private List<String> responseList;
	private Set<RequestAttribute> requestedAttributes;
	private Set<SpecialFunction> requestedFunctions;

	// authtoken
	private ECParameterSpec ecParameterSpec = null;
	private byte[] nonce;
	private byte[] testbedPrivateKey;
	private ASN1ObjectIdentifier selectedIdentifier;
	private PrivateKey privK;
	private PublicKey pubK;

	// CAInfo oids
	private final ASN1ObjectIdentifier[] ciOids = { EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_128, EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_192,
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_256 };

	/**
	 * Creates an EAC2Simulator and initializes all values using the given
	 * parameters
	 * 
	 * @param efCardAccessHex
	 * @param nonceHex
	 * @param serverPublicKey
	 */
	public ComputationHelper(String efCardAccessHex, String nonceHex, String testbedPrivateKeyHex)
	{
		// fill values
		byte[] efCardAcess;
		if (efCardAccessHex == null)
		{
			efCardAcess = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDACCESS.getContent());
		}
		else
		{
			efCardAcess = DatatypeConverter.parseHexBinary(efCardAccessHex);
		}
		if (nonceHex == null)
		{
			nonce = DatatypeConverter.parseHexBinary(GeneralConstants.NONCE);
		}
		else
		{
			nonce = DatatypeConverter.parseHexBinary(nonceHex);
		}
		if (testbedPrivateKeyHex == null)
		{
			testbedPrivateKey = DatatypeConverter.parseHexBinary(EidCardFiles.CA_PRIVATE.getContent());
		}
		else
		{
			testbedPrivateKey = DatatypeConverter.parseHexBinary(testbedPrivateKeyHex);
		}

		// init the attribute and function sets
		requestedAttributes = new HashSet<>();
		requestedFunctions = new HashSet<>();

		KeyFactory factory;
		try
		{
			Security.addProvider(new BouncyCastleProvider());
			factory = KeyFactory.getInstance("ECDH", "BC");
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(testbedPrivateKey);
			privK = factory.generatePrivate(ks);
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load testbed CA key: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}

		// compute the values
		loadECParameterSpec(efCardAcess);
	}

	// load ECParameterSpec from the currently used EF.CardAccess
	private void loadECParameterSpec(byte[] efCardAccess)
	{
		try
		{
			// read domain parameters from EF.CardAccess
			ASN1Set cardAccess = (ASN1Set) ASN1Set.fromByteArray(efCardAccess);
			for (int i = 0; i < cardAccess.size(); i++)
			{
				ASN1Sequence securityInfo = (ASN1Sequence) cardAccess.getObjectAt(i);
				ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) securityInfo.getObjectAt(0);

				// CA domain parameter info with ECDH and Generic Mapping
				if (EAC2ObjectIdentifiers.id_CA_ECDH.equals(oid))
				{
					ASN1Sequence algInfo = null;
					if (securityInfo.size() > 1 && securityInfo.size() < 4)
					{
						algInfo = (ASN1Sequence) securityInfo.getObjectAt(1);
					}
					else
					{
						logger.error("Invalid size for CA domain parameter info received.");
					}
					if (algInfo != null && ((ASN1ObjectIdentifier) algInfo.getObjectAt(0)).toString().equals("0.4.0.127.0.7.1.2"))
					{
						try
						{
							BigInteger val = ((ASN1Integer) algInfo.getObjectAt(1)).getValue();
							int curve = val.intValue();
							ecParameterSpec = ECNamedCurveTable.getParameterSpec(StandardizedDomainParameter.getFromId(curve).toString());
						}
						catch (Exception e)
						{
							StringWriter trace = new StringWriter();
							e.printStackTrace(new PrintWriter(trace));
							logger.error("Could not load CA domain parameters: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
						}
					}
					else
					{
						throw new IllegalArgumentException("Only standardized domain parameters are supported.");
					}
				}
				// CA info
				for (ASN1ObjectIdentifier ciOid : ciOids)
				{
					if (ciOid.equals(oid))
					{
						selectedIdentifier = ciOid;
						break;
					}
				}
			}
		}
		catch (IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could read EF.CardAccess structure: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
	}

	/**
	 * Validates the signature using the given parameters including auxilary
	 * data provided in EAC1
	 * 
	 * @param authenticationSimulator
	 * @param hexSignature
	 * @param hexIDpicc
	 * @param hexNonce
	 * @param hexServerCAkey
	 * @param hexAuxilaryData
	 * @return
	 */
	public boolean isValidSignature(String hexSignature, String hexIDpicc, String hexNonce, String hexServerCAkey, String hexAuxilaryData)
	{
		// now start signature computation
		CVPubKeyHolder keyHolder = terminalCertificate.getPublicKey();
		try
		{
			PublicKey certKey = null;
			if (!keyHolder.isDomainParamPresent())
			{
				// check if the domain parameters are present. if not, add them
				keyHolder.setDomainParam(ecParameterSpec);
				keyHolder.setIncludeDomainParam(true);
			}
			certKey = keyHolder.getPublicKey();

			// build the signature input data
			byte[] idPicc = DatatypeConverter.parseHexBinary(hexIDpicc);
			byte[] challenge = DatatypeConverter.parseHexBinary(hexNonce);

			String hexKey = sanitizeServerKey(hexServerCAkey);
			byte[] serverCAkey = DatatypeConverter.parseHexBinary(hexKey);
			byte[] auxData = null;
			if (hexAuxilaryData != null)
			{
				auxData = DatatypeConverter.parseHexBinary(hexAuxilaryData);
			}
			byte[] signed = CommonUtil.concatArrays(idPicc, challenge, serverCAkey, auxData);

			// verify the signature
			Signature signature = Signature.getInstance(SignatureAlgorithm.getFromEnumName(keyHolder.getAlgorithm().toString()).getAlgorithmName(), "BC");
			signature.initVerify(certKey);
			signature.update(signed);

			return signature.verify(DatatypeConverter.parseHexBinary(hexSignature));
		}
		catch (CVInvalidKeySourceException | CVKeyTypeNotSupportedException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException | CVMissingKeyException
				| InvalidKeySpecException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Could not validate signature: " + System.getProperty("line.separator") + trace.toString());
		}
		return false;
	}

	private String sanitizeServerKey(String hexServerCAkey)
	{
		// remove leading 0x04 and y coordinate of the server ca key
		String hexKey;
		if (hexServerCAkey.startsWith("04"))
		{
			hexKey = hexServerCAkey.substring(2);
		}
		else
		{
			hexKey = hexServerCAkey;
		}
		hexKey = hexKey.substring(0, (hexKey.length() / 2));
		return hexKey;
	}

	/**
	 * Validates the signature using the given parameters excluding auxilary
	 * data provided in EAC1
	 * 
	 * @param hexSignature
	 * @param hexIDpicc
	 * @param hexNonce
	 * @param hexServerCAkey
	 * @param terminalCertificate
	 * @return
	 */
	public boolean isValidSignature(String hexSignature, String hexIDpicc, String hexNonce, String hexServerCAkey)
	{
		return isValidSignature(hexSignature, hexIDpicc, hexNonce, hexServerCAkey, null);
	}

	/**
	 * Computes an authentication token for EAC2
	 * 
	 * @return
	 */
	public String computeAuthenticationToken(String hexServerCAkey) throws SharedSecretNotYetReadyException
	{
		if (hexServerCAkey == null)
		{
			throw new SharedSecretNotYetReadyException();
		}

		// generate the shared secret and derive keys
		byte[] keyMac = null, keyEncryption;
		if (ecParameterSpec.getH().intValue() == 1)
		{
			byte[] shared = null;
			try
			{
				// initialize key agreement
				KeyAgreement keyAgreement = KeyAgreement.getInstance(Type.ECDH.toString());
				keyAgreement.init(privK);

				// finish key agreement
				Security.addProvider(new BouncyCastleProvider());
				KeyFactory factory = KeyFactory.getInstance("ECDH", "BC");
				ECNamedCurveParameterSpec spec = (ECNamedCurveParameterSpec) ecParameterSpec;
				ECNamedCurveSpec params = new ECNamedCurveSpec("BrainpoolP256r1", spec.getCurve(), spec.getG(), spec.getN());
				java.security.spec.ECPoint point = ECPointUtil.decodePoint(params.getCurve(), DatatypeConverter.parseHexBinary(hexServerCAkey));
				ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
				pubK = factory.generatePublic(pubKeySpec);

				keyAgreement.doPhase(pubK, true);
				shared = keyAgreement.generateSecret();
			}
			catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | InvalidKeySpecException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not generate shared secret: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			}

			// derive sm key pair and initialize SM
			KeyDerivationFunction kdf = new KeyDerivationFunction();
			kdf.init(selectedIdentifier, shared);
			try
			{
				keyEncryption = kdf.perform(1, nonce);
				keyMac = kdf.perform(2, nonce);
				initSecureMessaging(keyEncryption, keyMac);
			}
			catch (EIDCryptoException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not derive keys from shared secret: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			}
		}
		else
		{
			throw new IllegalArgumentException("Wrong curve parameter H (cofactor of G): " + ecParameterSpec.getH());
		}

		try
		{
			return DatatypeConverter.printHexBinary(AuthenticationToken.calculate(keyMac, pubK, ecParameterSpec, selectedIdentifier));
		}
		catch (EIDCryptoException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not generate authentication token: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	/**
	 * @return the ECParameterSpec
	 */
	public ECParameterSpec getEcParameterSpec()
	{
		return ecParameterSpec;
	}

	/**
	 * Initializes the secure messaging after the AuthenticationToken has been
	 * created
	 */
	private void initSecureMessaging(byte[] keyEncryption, byte[] macKey)
	{
		SecretKey smEncryptionKey = new SecretKeySpec(keyEncryption, SecureMessaging.SymmetricCipher.AES.toString());
		SecretKey smMacKey = new SecretKeySpec(macKey, SecureMessaging.SymmetricCipher.AES.toString());
		try
		{
			sm = new SecureMessaging(SecureMessaging.SymmetricCipher.AES, smEncryptionKey, smMacKey);
		}
		catch (EIDCryptoException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not initialize secure messaging: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
	}

	public Result validateCVcertificates(Map<String, CVCertificate> knownCvCertificates, int expectedAmountOfCvCertificates, EService service)
	{
		String resultMessage = "";
		boolean isValid = true;
		CVCertificate terminalCertificate = null;
		CVCertificate dvCertificate = null;
		int numCertificatesReceived = 0;

		// load the expected CVCA certificate
		String expectedCvcaName = null;
		if (service == EService.A_2)
		{
			expectedCvcaName = "CERT_ECARD_CV_LINK_2_A.cvcert";
		}
		else
		{
			expectedCvcaName = "CERT_ECARD_CV_CVCA_1.cvcert";
		}
		CVCertificate expectedCvca = CryptoHelper.loadCvFromFile("CVCertificates/certs/" + expectedCvcaName);

		// load the expected DV certificate
		String expectedDvName = null;
		if (service == EService.A_2)
		{
			expectedDvName = "CERT_ECARD_CV_DV_2_A.cvcert";
		}
		else
		{
			expectedDvName = "CERT_ECARD_CV_DV_1_A.cvcert";
		}
		CVCertificate expectedDV = CryptoHelper.loadCvFromFile("CVCertificates/certs/" + expectedDvName);

		// iterate over the received certificates
		for (String key : knownCvCertificates.keySet())
		{
			CVCertificate c = knownCvCertificates.get(key);
			CVAuthorization cvAuthorization = c.getCertHolderAuth().getAuth();
			if (CertHolderRole.Terminal.equals(cvAuthorization.getRole()))
			{
				if (terminalCertificate != null)
				{
					resultMessage += "More than one terminal certificate has been received" + System.getProperty("line.separator");
					isValid = false;
				}
				// save the terminal certificate for later usage
				terminalCertificate = c;
				numCertificatesReceived++;
			}
			else
			{
				numCertificatesReceived++;
				if (CertHolderRole.DVdomestic.equals(cvAuthorization.getRole()))
				{
					if (dvCertificate != null)
					{
						resultMessage += "More than one DV certificate has been received" + System.getProperty("line.separator");
						isValid = false;
					}
					else if (!c.toString().equals(expectedDV.toString()))
					{
						resultMessage += "The received DV certificate did not match the expected one" + System.getProperty("line.separator");
						isValid = false;
					}
					dvCertificate = c;
				}
			}
		}

		// no terminal certificate has been found
		if (terminalCertificate == null)
		{
			isValid = false;
			resultMessage += "The server input did not contain a terminal certificate." + System.getProperty("line.separator");
		}
		else
		{
			this.terminalCertificate = terminalCertificate;
			// validate the terminal signature
			if (null != dvCertificate && null != expectedDV && null != expectedCvca)
			{
				try
				{
					isValid &= terminalCertificate.checkSign(dvCertificate, expectedCvca);
				}
				catch (CVInvalidKeySourceException | CVKeyTypeNotSupportedException | CVMissingKeyException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException
						| InvalidKeyException | SignatureException | CVUnknownAlgorithmException | CVUnknownCryptoProviderException e)
				{
					isValid = false;
					resultMessage += "Could not check signature due to " + e.getLocalizedMessage() + ". See technical log for details." + System.getProperty("line.separator");
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.error("Could not validate terminal signature: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
				}
			}
			else
			{
				isValid = false;
				resultMessage += "The signature was invalid." + System.getProperty("line.separator");
			}
		}


		// check if the correct number of certificates has been received
		if (numCertificatesReceived != expectedAmountOfCvCertificates && !GeneralConstants.DEBUG_MODE)
		{
			resultMessage += "Expected " + expectedAmountOfCvCertificates + " CV certificates, but received " + numCertificatesReceived + System.getProperty("line.separator");
			isValid = false;
		}

		return new Result(isValid, resultMessage);
	}


	public void decSSC(byte[] sendSequenceCounter, int a)
	{
		for (int i = sendSequenceCounter.length - 1; i >= 0 && a > 0; i--)
		{
			sendSequenceCounter[i] -= a & 0xFF;
			a >>>= 8;
		}
	}

	public ResponseAPDU encryptAndOrMac(ResponseAPDU responseAPDU, boolean doEncrypt, boolean doMac, boolean useDecreasedSSCForMAC) throws EIDCryptoException
	{
		ResponseAPDU response = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			if (doEncrypt)
			{
				byte[] plainResponseData = responseAPDU.getData();
				if (plainResponseData.length > 0)
				{
					Method cryptMethod = SecureMessaging.class.getDeclaredMethod("crypt", int.class, byte[].class);
					cryptMethod.setAccessible(true);
					// byte[] encData = crypt(Cipher.ENCRYPT_MODE, plainResponseData);
					byte[] encData = (byte[]) cryptMethod.invoke(sm, Cipher.ENCRYPT_MODE, plainResponseData);
					try (ByteArrayOutputStream encDataStream = new ByteArrayOutputStream())
					{
						encDataStream.write(0x01); // padding-content indicator byte
						encDataStream.write(encData);
						TLVObject do87 = new TLVObject(0x87, encDataStream.toByteArray());
						baos.write(do87.toBytes());
					}
				}
			}

			// write status
			byte[] innerStatus = new byte[] { (byte) responseAPDU.getSW1(), (byte) responseAPDU.getSW2() };
			TLVObject do99 = new TLVObject(0x99, innerStatus);
			baos.write(do99.toBytes());

			if (doMac)
			{
				if (useDecreasedSSCForMAC)
				{
					Field sscField = SecureMessaging.class.getDeclaredField("sendSequenceCounter");
					sscField.setAccessible(true);
					byte[] ssc = (byte[]) sscField.get(sm);
					decSSC(ssc, 1);
					sscField.set(sm, ssc);
				}
				// write checksum
				Method calculateChecksumMethod = SecureMessaging.class.getDeclaredMethod("calculateChecksum", byte[].class);
				calculateChecksumMethod.setAccessible(true);
				// byte[] checksum = calculateChecksum(baos.toByteArray());
				byte[] checksum = (byte[]) calculateChecksumMethod.invoke(sm, baos.toByteArray());
				if (useDecreasedSSCForMAC)
				{
					sm.nextAPDU();
				}
				TLVObject do8E = new TLVObject(0x8E, checksum);
				baos.write(do8E.toBytes());
			}

			// write status word
			baos.write(responseAPDU.getSW1());
			baos.write(responseAPDU.getSW2());

			response = new ResponseAPDU(baos.toByteArray());
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Encrypting of APDU failed." + System.getProperty("line.separator") + trace.toString());
			throw new EIDCryptoException(e);
		}
		return response;
	}


	public ResponseAPDU encryptAndOrMac(ResponseAPDU responseAPDU, boolean doEncrypt, boolean doMac) throws EIDCryptoException
	{
		return encryptAndOrMac(responseAPDU, doEncrypt, doMac, false);
	}

	/**
	 * Decodes and processes incoming APDU messages
	 * 
	 * @param apdus
	 * @param card
	 *            {@link EidCard} The eID-Card (mapping) to be used for the evaluation
	 * @param ageComparisonValue
	 * @param placeComparisonValue
	 * @return
	 */
	public String handleApduList(List<String> apdus, EidCard card, String ageComparisonValue, String placeComparisonValue)
	{
		String response = "";
		responseList = new ArrayList<String>();

		for (String apdu : apdus)
		{
			// try decrypting the apdu
			CommandAPDU cmdApdu = new CommandAPDU(DatatypeConverter.parseHexBinary(apdu));
			try
			{
				sm.nextAPDU();
				CommandAPDU decrypted = sm.decrypt(cmdApdu);
				String plainCommandApdu = DatatypeConverter.printHexBinary(decrypted.getBytes());
				response += "Decrypted SM CommandAPDU " + apdu + " to plain CommandAPDU " + plainCommandApdu + System.getProperty("line.separator");

				// generate response
				sm.nextAPDU();

				boolean useDecreasedSSCForMAC = false;
				if ((EidCard.EIDCARD_15.equals(card)) && (EidRequestApdu.READ_DG5.equals(EidRequestApdu.getFromRequest(plainCommandApdu))))
				{
					useDecreasedSSCForMAC = true;
				}

				String plainResponseApdu = null;
				plainResponseApdu = getPlainResponseApdu(plainCommandApdu, card, ageComparisonValue, placeComparisonValue);
				if (plainResponseApdu != null)
				{
					ResponseAPDU finalResponse = null;
					ResponseAPDU plainResponse = new ResponseAPDU(DatatypeConverter.parseHexBinary(plainResponseApdu));

					// status ok - don't encrypt, just compute checksum
					if (EidResponseApdu.STATUS_OK.getResponseApdu().equals(plainResponseApdu))
					{
						if ((EidCard.EIDCARD_14.equals(card)) && (EidRequestApdu.READ_DG5.equals(EidRequestApdu.getFromRequest(plainCommandApdu))))
						{
							finalResponse = encryptAndOrMac(plainResponse, false, false, useDecreasedSSCForMAC);
						}
						else if (useDecreasedSSCForMAC)
						{
							finalResponse = encryptAndOrMac(plainResponse, false, true, useDecreasedSSCForMAC);
						}
						else
						{
							finalResponse = sm.mac(plainResponse);
							// finalResponse = encryptAndOrMac(plainResponse, false, true, useDecreasedSSCForMAC);
						}
					}
					else
					{
						// encrypt
						if ((EidCard.EIDCARD_14.equals(card)) && (EidRequestApdu.READ_DG5.equals(EidRequestApdu.getFromRequest(plainCommandApdu))))
						{
							finalResponse = encryptAndOrMac(plainResponse, true, false, useDecreasedSSCForMAC);
						}
						else if (useDecreasedSSCForMAC)
						{
							finalResponse = encryptAndOrMac(plainResponse, true, true, useDecreasedSSCForMAC);
						}
						else
						{
							finalResponse = sm.encrypt(plainResponse);
							// finalResponse = encryptAndOrMac(plainResponse, true, true, useDecreasedSSCForMAC);
						}
					}
					responseList.add(DatatypeConverter.printHexBinary(finalResponse.getBytes()));
					response += "Created ResponseAPDU " + DatatypeConverter.printHexBinary(finalResponse.getBytes()) + System.getProperty("line.separator");
				}
				// unknown
				else
				{
					response += "Received an unknown CommandAPDU: " + plainCommandApdu;
					logger.error("Received an unknown CommandAPDU: " + plainCommandApdu);
				}
			}
			catch (EIDCryptoException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Decrypting of APDU " + apdu + "failed." + System.getProperty("line.separator") + trace.toString());
			}
		}
		return response;
	}


	// returns the plain response apdu for the given command apdu
	private String getPlainResponseApdu(String plainCommandApdu, EidCard card, String ageComparisonValue, String placeComparisonValue)
	{
		Map<EidRequestApdu, EidResponseApdu> apdus = EidCards.apdus(card);
		EidRequestApdu apdu = EidRequestApdu.getFromRequest(plainCommandApdu.toUpperCase());
		if (null != apdu)
		{
			// process the data
			if (EidRequestApdu.AGE_VERIFICATION.equals(apdu))
			{
				requestedFunctions.add(SpecialFunction.AgeVerification);
				Integer minAge = Integer.valueOf(ageComparisonValue);

				// compute the current age in years
				LocalDate bday = EidCards.birthday(card);
				LocalDate today = LocalDate.now();
				Period p = Period.between(bday, today);

				// now check if the card holder is old enough to fulfill the condition
				if (p.getYears() < minAge)
				{
					return EidResponseApdu.VERIFICATION_FAILED.getResponseApdu();
				}
				else
				{
					return EidResponseApdu.STATUS_OK.getResponseApdu();
				}
			}
			else if (EidRequestApdu.PLACE_VERIFICATION.equals(apdu))
			{
				requestedFunctions.add(SpecialFunction.PlaceVerification);
				if (placeComparisonValue.equals(EidCards.communityId(card)))
				{
					return EidResponseApdu.STATUS_OK.getResponseApdu();
				}
				else
				{
					return EidResponseApdu.VERIFICATION_FAILED.getResponseApdu();
				}
			}
			else
			{
				if (EidRequestApdu.RESTRICTED_IDENTIFICATION.equals(apdu))
				{
					requestedFunctions.add(SpecialFunction.RestrictedID);
				}
				RequestAttribute ra = RequestAttribute.getFromName(apdu.getOperationName(), false);
				if (ra != null)
				{
					requestedAttributes.add(ra);
				}
				return apdus.get(apdu).getResponseApdu();
			}
		}
		return null;
	}

	/**
	 * Returns response APDUs that have been generated in the previous step
	 * 
	 * @return
	 */
	public String getPreviousApdus()
	{
		String result = "";
		if (responseList != null)
		{
			for (int i = 0; i < responseList.size() - 1; i++)
			{
				result += "<OutputAPDU>" + responseList.get(i) + "</OutputAPDU>" + System.getProperty("line.separator");
			}
			if (responseList.size() > 0)
			{
				result += "<OutputAPDU>" + responseList.get(responseList.size() - 1) + "</OutputAPDU>";
			}
			responseList.clear();
		}
		return result;
	}

	/**
	 * Set the terminal certificate for this instance. Should only be used for
	 * testing purposes
	 * 
	 * @param cert
	 */
	protected void setTerminalCertificate(CVCertificate cert)
	{
		this.terminalCertificate = cert;
	}

	/**
	 * Returns the {@link Set} of {@link RequestAttribute}s that have been requested by the server during SM.
	 * 
	 * @return
	 */
	protected Set<RequestAttribute> getReceivedAttribues()
	{
		return requestedAttributes;
	}

	/**
	 * Returns the {@link Set} of {@link SpecialFunction}s that have been requested by the server during SM.
	 * 
	 * @return
	 */
	protected Set<SpecialFunction> getReceivedFunctions()
	{
		return requestedFunctions;
	}

}
