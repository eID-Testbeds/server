package com.secunet.eidserver.testbed.runner;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.DHParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.testbedutils.cvc.cvcertificate.TAAlgorithm;
import com.secunet.testbedutils.eac2.EAC2ObjectIdentifiers;
import com.secunet.testbedutils.eac2.StandardizedDomainParameters;
import com.secunet.testbedutils.eac2.StandardizedDomainParameters.Table_Type;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

public class TestCardSecurity
{

	/**
	 * CscaMasterList
	 * {
	 *   iso-itu-t(2) international-organization(23) icao(136)
	 *   mrtd(1) security(1) masterlist(2)
	 * }
	 */
	public final static ASN1ObjectIdentifier id_MasterList = new ASN1ObjectIdentifier("2.23.136.1.1.2");
	
    /**
     * id-BlackList OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) eID(2) 2}
     */
    static final ASN1ObjectIdentifier id_BlackList = new ASN1ObjectIdentifier(EAC2ObjectIdentifiers.id_eID + ".2");

    /**
     * id-DefectList OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) MRTD(1) 5}
     */
    static final ASN1ObjectIdentifier id_DefectList = new ASN1ObjectIdentifier(EAC2ObjectIdentifiers.bsi_de + ".3.1.5");
    
    /**
     * id-CertRevoked OBJECT IDENTIFIER ::= {
     * id-DefectList AuthDefect(1) 1}
     */
    static final ASN1ObjectIdentifier id_CertRevoked = new ASN1ObjectIdentifier(id_DefectList + ".1.1");

	public static void main(String[] args)
	{
		Security.addProvider(new BouncyCastleProvider());
		
		//doMasterList();
		//doDefectList();
		doBlackList();
		//doCardSecurity();
		//doNewCardSecurity();
		//doMultipleCardSecurity();
	}
	
	public static void doMasterList()
	{
		try
		{
			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_CSCA_1_MASTERLIST_SIGNER_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_CSCA_1_MASTERLIST_SIGNER.DER");

			X509Certificate cscaCert = loadCertificateFile("CERT_ECARD_CSCA_1.DER"); 
			
			byte[] contentBytes = getMasterListBytes(cscaCert);

			byte[] cmsBytes = generateMasterList(privKey, signCert, contentBytes);
			
			System.out.println("contentBytes: " + DatatypeConverter.printHexBinary(contentBytes));
			System.out.println("signCert    : " + DatatypeConverter.printHexBinary(writeFile("signCertML.cer", signCert.getEncoded())));
			System.out.println("privKey     : " + DatatypeConverter.printHexBinary(writeFile("privKeyML.der", privKey.getEncoded())));
			System.out.println("masterList  : " + DatatypeConverter.printHexBinary(writeFile("MASTERLIST_ECARD_CSCA_1.DER", cmsBytes)));
		}
		catch (CertificateEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public static void doDefectList()
	{
		try
		{
			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_CSCA_1_DEFECTLIST_SIGNER_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_CSCA_1_DEFECTLIST_SIGNER.DER");
//			PrivateKey privKey = loadKey("RSA", GeneralConstants.SIGNATURE);
//			X509Certificate signCert = loadCertificate("RSA", GeneralConstants.SIGNATURE);

			X509Certificate revokedCert = loadCertificateFile("CERT_ECARD_DS_1_B.DER"); 
			
			SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(Certificate.getInstance(revokedCert.getEncoded())));
			byte[] contentBytes = getDefectListBytes(signerIdentifier);

			byte[] cmsBytes = generateDefectList(privKey, signCert, contentBytes);
			
			System.out.println("contentBytes: " + DatatypeConverter.printHexBinary(contentBytes));
			System.out.println("signCert    : " + DatatypeConverter.printHexBinary(writeFile("signCertDL.cer", signCert.getEncoded())));
			System.out.println("privKey     : " + DatatypeConverter.printHexBinary(writeFile("privKeyDL.der", privKey.getEncoded())));
			System.out.println("defectList  : " + DatatypeConverter.printHexBinary(writeFile("BLACKLIST_ECARD_DS_1.DER", cmsBytes)));
		}
		catch (CertificateEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public static void doBlackList()
	{
		try
		{
			byte[] listID = new byte[] { 0x22 }; //TODO
//			byte[] sectorID = new byte[] { (byte) 0xF1, (byte) 0xFB, (byte) 0xB3, (byte) 0x0A, (byte) 0xE3, (byte) 0xA9, (byte) 0xBF, (byte) 0x2B, (byte) 0x34, (byte) 0x05, (byte) 0x4B, (byte) 0x5E, (byte) 0x1C, (byte) 0x9A, (byte) 0x3F, (byte) 0x9D, (byte) 0x07, (byte) 0xAB, (byte) 0x32, (byte) 0xFB, (byte) 0x2F, (byte) 0x8F, (byte) 0x95, (byte) 0x9A, (byte) 0x77, (byte) 0x95, (byte) 0x3C, (byte) 0x33, (byte) 0x2F, (byte) 0xB5, (byte) 0xD5, (byte) 0xB3}; //TODO !!!Hash of sector public key!!!!! Should be read and calculated directly from key file
//			byte[] sectorID = new byte[] { (byte) 0xFC, (byte) 0xB9, (byte) 0xA9, (byte) 0x0C, (byte) 0xC1, (byte) 0xE6, (byte) 0x75, (byte) 0xD8, (byte) 0x3E, (byte) 0x87, (byte) 0x44, (byte) 0x30, (byte) 0x97, (byte) 0x2A, (byte) 0x8B, (byte) 0xDE, (byte) 0xA0, (byte) 0xC5, (byte) 0xB0, (byte) 0xB9, (byte) 0xFB, (byte) 0x3A, (byte) 0x4F, (byte) 0x67, (byte) 0x29, (byte) 0x01, (byte) 0xEF, (byte) 0xDC, (byte) 0xD6, (byte) 0xC6, (byte) 0x99, (byte) 0x54};
//			byte[] sectorID = new byte[] { (byte) 0xD7, (byte) 0x5D, (byte) 0xD8, (byte) 0x0D, (byte) 0x69, (byte) 0xB1, (byte) 0x8C, (byte) 0x7B, (byte) 0xAC, (byte) 0xBD, (byte) 0x40, (byte) 0x2A, (byte) 0x62, (byte) 0x00, (byte) 0xBA, (byte) 0x29, (byte) 0x8D, (byte) 0xD5, (byte) 0x74, (byte) 0x2F, (byte) 0x59, (byte) 0x69, (byte) 0x79, (byte) 0xD1, (byte) 0xB1, (byte) 0x2D, (byte) 0x71, (byte) 0x0C, (byte) 0x37, (byte) 0xE1, (byte) 0xC2, (byte) 0xEB};
			
			byte[] keyFileBytes = loadFile("cv/keys/TERM_SECTOR1.x509.cv");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] sectorID = md.digest(keyFileBytes);
			
			
			
			byte[] revokedRestrictedID = new byte[] { 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44 };		
			List<byte[]> revokedRestrictedIDs = new LinkedList<byte[]>();
			revokedRestrictedIDs.add(revokedRestrictedID);
			byte[] contentBytes = getBlackListBytes(listID, sectorID, revokedRestrictedIDs);

			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_CSCA_1_BLACKLIST_SIGNER_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_CSCA_1_BLACKLIST_SIGNER.DER");
//			PrivateKey privKey = loadKey("RSA", GeneralConstants.SIGNATURE);
//			X509Certificate signCert = loadCertificate("RSA", GeneralConstants.SIGNATURE);

			byte[] cmsBytes = generateBlackList(privKey, signCert, contentBytes);
			
			System.out.println("sectorID    : " + DatatypeConverter.printHexBinary(sectorID));
			System.out.println("contentBytes: " + DatatypeConverter.printHexBinary(contentBytes));
			System.out.println("signCert    : " + DatatypeConverter.printHexBinary(writeFile("signCertBL.cer", signCert.getEncoded())));
			System.out.println("privKey     : " + DatatypeConverter.printHexBinary(writeFile("privKeyBL.der", privKey.getEncoded())));
			System.out.println("blackList   : " + DatatypeConverter.printHexBinary(writeFile("BLACKLIST_ECARD_EIDCARDS_TERM_1_X.DER", cmsBytes)));
		}
		catch (CertificateEncodingException | NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	public static void doCardSecurity()
	{
		try
		{
			byte[] cardSecurityBytes = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDSECURITY.getContent());
			byte[] siBytes = getSecurityInfoBytes(cardSecurityBytes);
//			LinkedList<X509Certificate> certsFromOrig = getCertificates(cardSecurityBytes);
//			byte[] siOriginalBytes = DatatypeConverter.parseHexBinary("318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");

//			PrivateKey privKey = loadKey("RSA", GeneralConstants.SIGNATURE);
//			X509Certificate signCert = loadCertificate("RSA", GeneralConstants.SIGNATURE);
//			PrivateKey privKey = createKey();
//			X509Certificate signCert = certsFromOrig.getFirst();
			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_DS_1_A_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_DS_1_A.DER");

			byte[] efCardSecurityBytes = generateEfCardSecurity(privKey, signCert, siBytes);
			
			System.out.println("EFCARDSECURITY orig: " + EidCardFiles.EFCARDSECURITY.getContent());
			System.out.println("securityInfoBytes  : " + DatatypeConverter.printHexBinary(siBytes));
//			System.out.println("signCert           : " + DatatypeConverter.printHexBinary(writeFile("signCert.cer", signCert.getEncoded())));
//			System.out.println("privKey            : " + DatatypeConverter.printHexBinary(writeFile("privKey.der", privKey.getEncoded())));
			System.out.println("EFCARDSECURITY DS A: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_DS_A.DER", efCardSecurityBytes)));

			PrivateKey privKey2 = loadKeyFile("EC", "CERT_ECARD_DS_1_B_privKey.DER");
			X509Certificate signCert2 = loadCertificateFile("CERT_ECARD_DS_1_B.DER");
//			byte[] siBytes2 = DatatypeConverter.parseHexBinary("3182011E300D060804007F00070202020201023012060A04007F000702020302020201020201413012060A04007F0007020204020202010202010D3017060A04007F0007020205020330090201010201430101FF3017060A04007F0007020205020330090201010201440101003019060904007F000702020502300C060704007F0007010202010D301C060904007F000702020302300C060704007F0007010202010D0201413016060804007F0007020206160A42445230315F434152443062060904007F0007020201023052300C060704007F0007010202010D034200046A0A37C5085C9B2AC4C303C9B4FAF3358904402C4F3F396DA921CB81BFB6A903619435960B6730608CE822CAD2C59E760F2C4C8BADA2DE0A21D4450FD3E14B52020141");
			efCardSecurityBytes = generateEfCardSecurity(privKey2, signCert2, siBytes);
			
//			System.out.println("securityInfoBytes2 : " + DatatypeConverter.printHexBinary(siBytes2));
			System.out.println("EFCARDSECURITY DS B: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_DS_B.DER", efCardSecurityBytes)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void doNewCardSecurity()
	{
		try
		{
			byte[] cardSecurityBytes = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDSECURITY_DS_A.getContent());
			byte[] accBytes = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDACCESS.getContent());
			byte[] siBytes = getSecurityInfoBytes(cardSecurityBytes);
//			LinkedList<X509Certificate> certsFromOrig = getCertificates(cardSecurityBytes);
			byte[] siDifferentRIKeyID_CARD5_Bytes = DatatypeConverter.parseHexBinary("318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201030101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201040101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
			byte[] siDifferentRIKeyID_CARD6_Bytes = DatatypeConverter.parseHexBinary("318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201050101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201060101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
			byte[] siNotOnCurveBytes = DatatypeConverter.parseHexBinary("318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F9020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");

//			PrivateKey privKey = loadKey("RSA", GeneralConstants.SIGNATURE);
//			X509Certificate signCert = loadCertificate("RSA", GeneralConstants.SIGNATURE);
//			PrivateKey privKey = createKey();
//			X509Certificate signCert = certsFromOrig.getFirst();
			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_DS_1_A_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_DS_1_A.DER");

			byte[] efCardSecurity_CARD5_Bytes = generateEfCardSecurity(privKey, signCert, siDifferentRIKeyID_CARD5_Bytes);
			byte[] efCardSecurity_CARD6_Bytes = generateEfCardSecurity(privKey, signCert, siDifferentRIKeyID_CARD6_Bytes);
			byte[] efCardSecurityNOCBytes = generateEfCardSecurity(privKey, signCert, siNotOnCurveBytes);
			
			System.out.println("SI EF.CARD_ACC     : " + DatatypeConverter.printHexBinary(writeFile("EF_CARDACCESS.DER", accBytes)));
			System.out.println("SI EF.CARD_SEC A   : " + DatatypeConverter.printHexBinary(siBytes));
			System.out.println("SI EF.CARD_SEC 5   : " + DatatypeConverter.printHexBinary(siDifferentRIKeyID_CARD5_Bytes));
			System.out.println("SI EF.CARD_SEC 6   : " + DatatypeConverter.printHexBinary(siDifferentRIKeyID_CARD6_Bytes));
			System.out.println("SI EF.CARD_SEC 13  : " + DatatypeConverter.printHexBinary(siNotOnCurveBytes));
//			System.out.println("signCert           : " + DatatypeConverter.printHexBinary(writeFile("signCert.cer", signCert.getEncoded())));
//			System.out.println("privKey            : " + DatatypeConverter.printHexBinary(writeFile("privKey.der", privKey.getEncoded())));
			System.out.println("EFCARDSECURITY RiC5: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_CARD5.DER", efCardSecurity_CARD5_Bytes)));
			System.out.println("EFCARDSECURITY RiC6: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_CARD6.DER", efCardSecurity_CARD6_Bytes)));
			System.out.println("EFCARDSECURITY NoCu: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_NOTONCURVE.DER", efCardSecurityNOCBytes)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void doMultipleCardSecurity()
	{
		try
		{
			byte[] cardSecurityBytes = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDSECURITY_DS_A.getContent());
			byte[] siCardAccessBytes = DatatypeConverter.parseHexBinary(EidCardFiles.EFCARDACCESS.getContent());
			byte[] siCardSecurityBytes = getSecurityInfoBytes(cardSecurityBytes);
//			LinkedList<X509Certificate> certsFromOrig = getCertificates(cardSecurityBytes);
			String SI_ACC_REPLACEABLE = "3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F000701020201xx020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C";
			String SI_SEC_REPLACEABLE = "318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F000701020201xx0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C";
			int standardizedDomainParameterInt = 13;
			String standardizedDomainParameterHexString = String.format("%02X", standardizedDomainParameterInt);
			String siCardAccessBytesHexString = SI_ACC_REPLACEABLE.replace("xx", standardizedDomainParameterHexString);
			String siCardSecurityBytesHexString = SI_SEC_REPLACEABLE.replace("xx", standardizedDomainParameterHexString);

			for(StandardizedDomainParameters.Table sdp : StandardizedDomainParameters.Table.values())
			{
				KeyPair kp = createChipAuthenticationKeyPair(sdp);
				if(kp != null)
				{
					PublicKey pubKey = kp.getPublic();
					System.out.println("Created Key for SDP #" +sdp.getId()+ " CLASS: " + pubKey.getClass().getName());
					if(pubKey instanceof BCECPublicKey)
					{
						BCECPublicKey bcPubKey = (BCECPublicKey) pubKey;
						System.out.print(bcPubKey.toString());
						SubjectPublicKeyInfo spki = StandardizedDomainParameters.generateSubjectPublicKeyInfo(sdp, pubKey);
						System.out.println("SubjectPublicKeyInfo: " + DatatypeConverter.printHexBinary(spki.getEncoded()));
						System.out.println();
					}
					else if(pubKey instanceof BCDHPublicKey)
					{
						BCDHPublicKey bcPubKey = (BCDHPublicKey) pubKey;
						System.out.println("\tPublic Y (hex): " + DatatypeConverter.printHexBinary(bcPubKey.getY().toByteArray()));
						System.out.println("\tPublic Y (dec): " + bcPubKey.getY());
						System.out.println("\tParam  P (dec): " + bcPubKey.getParams().getP());
						System.out.println("\tParam  G (dec): " + bcPubKey.getParams().getG());
						System.out.println("\tParam  L (dec): " + bcPubKey.getParams().getL());
						SubjectPublicKeyInfo spki = StandardizedDomainParameters.generateSubjectPublicKeyInfo(sdp, pubKey);
						System.out.println("SubjectPublicKeyInfo: " + DatatypeConverter.printHexBinary(spki.getEncoded()));
						System.out.println();
					}
				}
			}
			
			System.out.println("SI ACC match: " + EidCardFiles.EFCARDACCESS.getContent().equals(siCardAccessBytesHexString));
			System.out.println("SI ACC REPLACE : " + SI_ACC_REPLACEABLE);
			System.out.println("SI ACC ORIGINAL: " + EidCardFiles.EFCARDACCESS.getContent());
			System.out.println("SI ACC MOD     : " + siCardAccessBytesHexString);
			System.out.println("SI SEC match: " + DatatypeConverter.printHexBinary(siCardSecurityBytes).equals(siCardSecurityBytesHexString));
			System.out.println("SI SEC REPLACE : " + SI_SEC_REPLACEABLE);
			System.out.println("SI SEC ORIGINAL: " + DatatypeConverter.printHexBinary(siCardSecurityBytes));
			System.out.println("SI SEC MOD     : " + siCardSecurityBytesHexString);
			
			PrivateKey privKey = loadKeyFile("EC", "CERT_ECARD_DS_1_A_privKey.DER");
			X509Certificate signCert = loadCertificateFile("CERT_ECARD_DS_1_A.DER");

			byte[] efCardSecurity = generateEfCardSecurity(privKey, signCert, siCardSecurityBytes);
			
//			System.out.println("SI EF.CARD_SEC 5   : " + DatatypeConverter.printHexBinary(siDifferentRIKeyID_CARD5_Bytes));
//			System.out.println("SI EF.CARD_SEC 6   : " + DatatypeConverter.printHexBinary(siDifferentRIKeyID_CARD6_Bytes));
//			System.out.println("SI EF.CARD_SEC 13  : " + DatatypeConverter.printHexBinary(siNotOnCurveBytes));
//			System.out.println("EFCARDSECURITY RiC5: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_CARD5.DER", efCardSecurity_CARD5_Bytes)));
//			System.out.println("EFCARDSECURITY RiC6: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_CARD6.DER", efCardSecurity_CARD6_Bytes)));
//			System.out.println("EFCARDSECURITY NoCu: " + DatatypeConverter.printHexBinary(writeFile("EF_CARDSECURITY_NOTONCURVE.DER", efCardSecurityNOCBytes)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static byte[] getMasterListBytes(X509Certificate cscaCert)
	{
		/*
		 * MasterList ::= SEQUENCE {
		 *   version   INTEGER {v0(0)}
		 *   certList  SET OF Certificate
		 * }
		 */
		
		try
		{
			ASN1EncodableVector asn1MasterList = new ASN1EncodableVector();

			// version
			asn1MasterList.add(new ASN1Integer(0));
			
			// certList
			ASN1EncodableVector asn1Certs = new ASN1EncodableVector();

			asn1Certs.add(Certificate.getInstance(ASN1Primitive.fromByteArray(cscaCert.getEncoded())));

			asn1MasterList.add(new DERSet(asn1Certs));

			ASN1Sequence defectList = new DERSequence(asn1MasterList);
			
			return defectList.getEncoded(ASN1Encoding.DER);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static byte[] getDefectListBytes(SignerIdentifier revokedDS)
	{
		/*
		 * DefectList ::= SEQUENCE {
		 *   version   INTEGER {v1(0)}
		 *   hashAlg   OBJECT IDENTIFIER,
		 *   defects   SET OF Defect
		 * }
		 * 
		 * Defect ::= SEQUENCE{
		 *   signerIdentifier SignerIdentifier,
		 *   certificateHash  OCTET STRING OPTIONAL,
		 *   knownDefects     SET OF KnownDefect
		 * }
		 * 
		 * KnownDefect ::= SEQUENCE{
		 *   defectType       OBJECT IDENTIFIER,
		 *   parameters       ANY defined by defectType OPTIONAL
		 * }
		 */
		
		try
		{
			ASN1EncodableVector asn1DefectList = new ASN1EncodableVector();

			// version
			asn1DefectList.add(new ASN1Integer(0));
			
			// hashAlg
			asn1DefectList.add(NISTObjectIdentifiers.id_sha256);

			// defects
			ASN1EncodableVector asn1Defects = new ASN1EncodableVector();

				// Defect
				ASN1EncodableVector asn1Defect = new ASN1EncodableVector();
				asn1Defect.add(revokedDS);
				
				// skip certificateHash

				ASN1EncodableVector asn1KnownDefects = new ASN1EncodableVector();
				
					// KnownDefect
					ASN1EncodableVector asn1KnownDefect = new ASN1EncodableVector();

					asn1KnownDefect.add(id_CertRevoked); // defectType

					asn1KnownDefect.add(new ASN1Enumerated(0)); //parameters:  StatusCode ::= ENUMERATED { noIndication(0) ...} 
				
				asn1KnownDefects.add(new DERSequence(asn1KnownDefect));
				
				asn1Defect.add(new DERSet(asn1KnownDefects));
				
			asn1Defects.add(new DERSequence(asn1Defect));

			asn1DefectList.add(new DERSet(asn1Defects));

			ASN1Sequence defectList = new DERSequence(asn1DefectList);
			
			return defectList.getEncoded(ASN1Encoding.DER);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static byte[] getBlackListBytes(byte[] listID, byte[] sectorID, List<byte[]> revokedRIs)
	{
		/*
		 * BlackList ::= SEQUENCE{
		 * 	 version    INTEGER{v1(0)},
		 *   type       INTEGER{complete(0),added(1),removed(2)},
		 *   listID     ListID,
		 *   deltaBase  ListID OPTIONAL, -- required for delta lists
		 *   content    SEQUENCE OF BlackListDetails,
		 * }
		 * 
		 * BlackListDetails ::= SEQUENCE{
		 *   sectorID           OCTET STRING,
		 *   sectorSpecificIDs  SEQUENCE OF OCTET STRING
		 * }
		 * 
		 * ListID ::= OCTET STRING
		 */
		
		try
		{
			ASN1EncodableVector asn1BlackList = new ASN1EncodableVector();

			// version
			asn1BlackList.add(new ASN1Integer(0));
			
			// type
			asn1BlackList.add(new ASN1Integer(0));

			// listID
			asn1BlackList.add(new DEROctetString(listID));

			// content
			ASN1EncodableVector asn1Content = new ASN1EncodableVector();

				// blackListDetails
				ASN1EncodableVector asn1BlackListDetails = new ASN1EncodableVector();
				asn1BlackListDetails.add(new DEROctetString(sectorID)); // sectorID
	
				ASN1EncodableVector asn1SectorSpecificIDs = new ASN1EncodableVector();
				for(byte[] revokedRI : revokedRIs)
				{
					asn1SectorSpecificIDs.add(new DEROctetString(revokedRI)); // sectorSpecificID
				}
	
				asn1BlackListDetails.add(new DERSequence(asn1SectorSpecificIDs));
			
			asn1Content.add(new DERSequence(asn1BlackListDetails));

			asn1BlackList.add(new DERSequence(asn1Content));

			ASN1Sequence blackList = new DERSequence(asn1BlackList);
			
			return blackList.getEncoded(ASN1Encoding.DER);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static byte[] generateMasterList(PrivateKey privKey, X509Certificate signCert, byte[] contentBytes)
	{
		return generateCMSSignedData(id_MasterList, "SHA224withECDSA", privKey, signCert, contentBytes);
	}

	private static byte[] generateDefectList(PrivateKey privKey, X509Certificate signCert, byte[] contentBytes)
	{
		return generateCMSSignedData(id_DefectList, "SHA224withECDSA", privKey, signCert, contentBytes);
	}

	private static byte[] generateBlackList(PrivateKey privKey, X509Certificate signCert, byte[] contentBytes)
	{
		return generateCMSSignedData(id_BlackList, "SHA224withECDSA", privKey, signCert, contentBytes);
	}

	private static byte[] generateEfCardSecurity(PrivateKey privKey, X509Certificate signCert, byte[] contentBytes)
	{
		return generateCMSSignedData(EAC2ObjectIdentifiers.id_SecurityObject, "SHA224withECDSA", privKey, signCert, contentBytes);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static byte[] generateCMSSignedData(ASN1ObjectIdentifier contentType, String signatureAlgorithm, PrivateKey privKey, X509Certificate signCert, byte[] contentBytes)
	{
		try
		{
			ArrayList certList = new ArrayList();
			certList.add(signCert);
			Store certs = new JcaCertStore(certList);

			ContentSigner signer = new JcaContentSignerBuilder(signatureAlgorithm).setProvider("BC").build(privKey);
			DefaultSignedAttributeTableGenerator signedAttrGen = new DefaultSignedAttributeTableGenerator() {

				@Override
				protected Hashtable createStandardAttributeTable(Map parameters)
				{
					Hashtable std = super.createStandardAttributeTable(parameters);
					if (std.containsKey(CMSAttributes.signingTime))
					{
						std.remove(CMSAttributes.signingTime);
					}
					if (std.containsKey(CMSAttributes.cmsAlgorithmProtect))
					{
						std.remove(CMSAttributes.cmsAlgorithmProtect);
					}
					return std;
				}

			};

			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			gen.addSignerInfoGenerator(
					new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).setSignedAttributeGenerator(signedAttrGen).build(signer, signCert));
			gen.addCertificates(certs);

			CMSSignedData sigData = gen.generate(new CMSProcessableByteArray(contentType, contentBytes), true);

			return sigData.toASN1Structure().getEncoded(ASN1Encoding.DER);
		}
		catch (CertificateEncodingException | OperatorCreationException | CMSException | IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static KeyPair createChipAuthenticationKeyPair(StandardizedDomainParameters.Table sdp)
	{
		try
		{
			if(sdp.getBcName() != null)
			{
				if(sdp.getType() == Table_Type.ECP)
				{
					ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(sdp.getBcName());
					KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");
					g.initialize(ecSpec, new SecureRandom());
					return g.generateKeyPair();
				}
				if(sdp.getType() == Table_Type.GFP)
				{
					DHParameters dhParameters = BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject(sdp.getBcName());
					DHParameterSpec dhSpec = new DHParameterSpec(dhParameters.getP(), dhParameters.getG(), dhParameters.getL());
					KeyPairGenerator g = KeyPairGenerator.getInstance("DH", "BC");
					g.initialize(dhSpec, new SecureRandom());
					return g.generateKeyPair();
				}
			}
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static PrivateKey createKey()
	{
		try
		{
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("brainpoolP224r1");
			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
			g.initialize(ecSpec, new SecureRandom());
			KeyPair pair = g.generateKeyPair();
			return pair.getPrivate();
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static X509Certificate createCertificate(byte[] data)
	{
		CertificateFactory certFactory;
		try
		{
			InputStream input = new ByteArrayInputStream(data);
			certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(input);

			
			try (FileOutputStream fos = new FileOutputStream(cert.getSubjectX500Principal().getName()+".cer")) {
			    fos.write(data);
			} catch (IOException ioe) {
			    ioe.printStackTrace();
			}
			
			
			
			
			return cert;
		}
		catch (CertificateException | NoSuchProviderException e)
		{
			System.out.println("Could not create x509 certificate: " + e.getMessage());
		}
		return null;
	}

	private static X509Certificate loadCertificate(String alg, String name)
	{
		CertificateFactory certFactory;
		try
		{
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("certificates/" + alg + "/" + name + ".crt");
			certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(input);
			return cert;
		}
		catch (CertificateException e)
		{
			System.out.println("Could not read x509 certificate: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Load the key with the given name from the file system.
	 * 
	 * @see com.secunet.eidserver.testbed.common.constants.GeneralConstants for valid parameter values
	 *
	 * @param protocol
	 *            Protocol identifier
	 * @param alg
	 *            Algorithm identifier
	 * @param name
	 *            {@link String} The name of the key to load.
	 * @return {@link X509Certificate}
	 */
	private static PrivateKey loadKey(String alg, String name)
	{
		try
		{
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("keys/" + alg + "/" + name + ".der");
			if (input != null)
			{
				KeyFactory factory = KeyFactory.getInstance("RSA");
				byte[] keyBytes = IOUtils.toByteArray(input);
				KeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
				return factory.generatePrivate(spec);
			}
		}
		catch (InvalidKeySpecException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			System.out.println("Could not load key " + name + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		catch (NoSuchAlgorithmException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			System.out.println("Could not load key " + name + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		catch (IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			System.out.println("Could not load key " + name + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	private static X509Certificate loadCertificateFile(String fileName)
	{
		try(InputStream input = new FileInputStream(fileName))
		{
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(input);
			return cert;
		}
		catch (CertificateException | IOException | NoSuchProviderException e)
		{
			System.out.println("Could not read x509 certificate: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Load the key with the given name from the file system.
	 * 
	 * @param alg
	 *            Something like "RSA" etc.
	 * @param fileName
	 *            {@link String} The name of the key to load.
	 * @return {@link X509Certificate}
	 */
	private static PrivateKey loadKeyFile(String alg, String fileName)
	{
		try(InputStream input = new FileInputStream(fileName))
		{
			KeyFactory factory = KeyFactory.getInstance(alg, BouncyCastleProvider.PROVIDER_NAME);
			byte[] keyBytes = IOUtils.toByteArray(input);
			KeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			return factory.generatePrivate(spec);
		}
		catch (InvalidKeySpecException|NoSuchAlgorithmException|IOException | NoSuchProviderException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			System.out.println("Could not load key " + fileName + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
	private static byte[] getSecurityInfoBytes(byte[] data)
	{
		try(ASN1InputStream ais = new ASN1InputStream(data))
		{
				ASN1Sequence seq = (ASN1Sequence) ais.readObject();
				ContentInfo ci = ContentInfo.getInstance(seq);

				if (!ci.getContentType().equals(CMSObjectIdentifiers.signedData))
					throw new Exception("wrong content type in CardSecurity");

				SignedData sd = SignedData.getInstance((ASN1Sequence) ci.getContent());
				ContentInfo eci = sd.getEncapContentInfo();

				if (!eci.getContentType().equals(EAC2ObjectIdentifiers.id_SecurityObject))
					throw new Exception("CardSecurity does not encapsulate SecurityInfos");

				return ((ASN1OctetString) eci.getContent()).getOctets();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static LinkedList<X509Certificate> getCertificates(byte[] data)
	{
		LinkedList<X509Certificate> certList = new LinkedList<X509Certificate>();
		try(ASN1InputStream ais = new ASN1InputStream(data))
		{
				ASN1Sequence seq = (ASN1Sequence) ais.readObject();
				ContentInfo ci = ContentInfo.getInstance(seq);

				if (!ci.getContentType().equals(CMSObjectIdentifiers.signedData))
					throw new Exception("wrong content type in CardSecurity");

				SignedData sd = SignedData.getInstance((ASN1Sequence) ci.getContent());
				for(ASN1Encodable cert : sd.getCertificates())
				{
					ASN1Sequence certStructure = (ASN1Sequence)cert;
					certList.add(createCertificate(certStructure.getEncoded(ASN1Encoding.DER)));
				}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return certList;
	}
	
	private static byte[] writeFile(String name, byte[] data)
	{
	    try(FileOutputStream fos = new FileOutputStream(name))
	    {
		    fos.write(data);
		    fos.close();
	    }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	    return data;
	}

	private static byte[] loadFile(String fileName)
	{
		try(InputStream input = new FileInputStream(fileName))
		{
			return IOUtils.toByteArray(input);
		}
		catch (IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			System.out.println("Could not load file " + fileName + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}
	
}
