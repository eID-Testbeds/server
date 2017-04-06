package com.secunet.testbedutils.eac2;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import com.secunet.testbedutils.eac2.cv.UtilPublicKey;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

public class StandardizedDomainParameters 
{
	
    /**
     * standardizedDomainParameters OBJECT IDENTIFIER ::= {
     * bsi-de algorithms(1) 2}
     */
    static final ASN1ObjectIdentifier standardizedDomainParametersOID = new ASN1ObjectIdentifier(EAC2ObjectIdentifiers.bsi_de + ".1.2");
    
    public enum Table_Type
    {
    	GFP, ECP;    	
    }

	public enum Table
	{
		
		ID_00( 0, Table_Type.GFP, "1024-bit MODP Group with 160-bit Prime Order Subgroup", "rfc5114_1024_160"),
		ID_01( 1, Table_Type.GFP, "2048-bit MODP Group with 224-bit Prime Order Subgroup", "rfc5114_2048_224"),
		ID_02( 2, Table_Type.GFP, "2048-bit MODP Group with 256-bit Prime Order Subgroup", "rfc5114_2048_256"),
		ID_08( 8, Table_Type.ECP, "NIST P-192 (secp192r1)", "secp192r1"),
		ID_09( 9, Table_Type.ECP, "BrainpoolP192r1", "brainpoolP192r1"),
		ID_10(10, Table_Type.ECP, "NIST P-224 (secp224r1)", "secp224r1"),
		ID_11(11, Table_Type.ECP, "BrainpoolP224r1", "brainpoolP224r1"),
		ID_12(12, Table_Type.ECP, "NIST P-256 (secp256r1)", "secp256r1"),
		ID_13(13, Table_Type.ECP, "BrainpoolP256r1", "brainpoolP256r1"),
		ID_14(14, Table_Type.ECP, "BrainpoolP320r1", "brainpoolP320r1"),
		ID_15(15, Table_Type.ECP, "NIST P-384 (secp384r1)", "secp384r1"),
		ID_16(16, Table_Type.ECP, "BrainpoolP384r1", "brainpoolP384r1"),
		ID_17(17, Table_Type.ECP, "BrainpoolP512r1", "brainpoolP512r1"),
		ID_18(18, Table_Type.ECP, "NIST P-521 (secp521r1)", "secp521r1");
		
		private int id;
		private Table_Type type;
		private String name;
		private String bcName;
		
		private Table(int id, Table_Type type, String name, String bcName)
		{
			this.id = id;
			this.type = type;
			this.name = name;
			this.bcName = bcName;
		}

		public static Table getById(int id)
		{
			for(Table entry : Table.values())
			{
				if(entry.getId() == id) return entry;
			}
			return null;
		}

		public int getId()
		{
			return id;
		}

		public Table_Type getType()
		{
			return type;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getBcName()
		{
			return bcName;
		}
		
		public String getIdAsHexString()
		{
			return String.format("%02X", id);
		}
		
	}
	
	public static SubjectPublicKeyInfo generateSubjectPublicKeyInfo(Table id, PublicKey pubKey) throws IOException
	{
		AlgorithmIdentifier algId = new AlgorithmIdentifier(standardizedDomainParametersOID, new ASN1Integer(id.getId()));
		byte[] publicKeyBytes = null;
		switch(id.type)
		{
			case GFP:
				publicKeyBytes = (new ASN1Integer(((DHPublicKey) pubKey).getY())).getEncoded(ASN1Encoding.DER);
				break;
			case ECP:
				publicKeyBytes = UtilPublicKey.getRawKey((ECPublicKey) pubKey, true);
				break;
			default:
				return null;	
		}
		
		return new SubjectPublicKeyInfo(algId, publicKeyBytes);
	}

	public static AlgorithmParameterSpec getParameters(Table id) 
	{
		if(id != null)
		{
			switch(id.type)
			{
				case GFP:
					return convert(BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject(id.getBcName()));
				case ECP:
					return ECNamedCurveTable.getParameterSpec(id.getBcName());
				default:
					return null;	
			}
		}
		
		return null;
	}
	
	public static AlgorithmParameterSpec getParameters(BigInteger positiveValue) 
	{
		return getParameters(Table.getById(positiveValue.intValue()));
	}
	
	public static AlgorithmParameterSpec convert(X9ECParameters params)
	{
		if(params == null) return null;
		return new ECParameterSpec( params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed() );
	}
	
	public static AlgorithmParameterSpec convert(DHParameters params)
	{
		if(params == null) return null;
		return new DHParameterSpec(params.getP(), params.getG(), params.getL());
	}
	
}
