package com.secunet.tr03129.cvcert;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;

import com.secunet.tr03129.constant.Constants;

public class CertDescRegistry
{
	private static Logger log = Logger.getLogger(CertDescRegistry.class.getName());
	private static Map<String, byte[]> desc = new HashMap<>();

	static
	{
		log.log(Level.FINEST, "Create initial database with all descriptions from folder " + Constants.CERT_DESC_FOLDER);

		try
		{
			for (CertificateDescription description : CertificateDescription.values())
			{
				String hash = Hex.encodeHexString(description.getHash());
				log.log(Level.INFO, "Loaded description with hash " + hash);
				desc.put(hash, description.getBytes());
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to initialize the database. Causes:", e);
		}
	}


	private CertDescRegistry()
	{
	}


	public static byte[] getDescriptionForHash(String descriptionHash) throws InvalidParameterException
	{
		if (descriptionHash == null)
		{
			throw new InvalidParameterException("No description hash has been provided");
		}
		if (desc.containsKey(descriptionHash))
		{
			return desc.get(descriptionHash);
		}
		throw new InvalidParameterException("No description found for hash " + descriptionHash);
	}

	public static byte[] getDescriptionForHash(byte[] descriptionHash) throws InvalidParameterException
	{
		return getDescriptionForHash(Hex.encodeHexString(descriptionHash));
	}

}
