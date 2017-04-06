package com.secunet.eidserver.testbed.web.ui.util;


import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.web.core.App;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;

/**
 * Utility to handle complex data and reduce it to short single text blocks.
 */
public class Humanizer
{

	/**
	 * @return Hex encoded SHA1 hash, on failure "[binary.length] Bytes"
	 */
	public static String getHumanizedSHA1Hash(final byte[] binary)
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance(App.getBinaryMessageDigest());
			byte[] digest = messageDigest.digest(binary);
			return new String(Hex.encodeHex(digest));
		}
		catch (Exception e)
		{
			Log.log(Humanizer.class).error("Failed to create humanized Binary: {}", e);
			return ((binary == null) ? "0" : binary.length) + " Bytes";
		}
	}


	public static String getCaption(TestCandidate cand)
	{
		return cand.getProfileName() + " (" + cand.getCandidateName() + ", " + I18NHandler.getText(I18NText.Version) + " " + cand.getVersionMajor() + "." + cand.getVersionMinor() + "."
				+ cand.getVersionSubminor() + ")";
	}

	public static String getCaption(TestCaseStep t)
	{
		return "" + t.getName() + " (" + t.isDefault() + ", " + t.isOptional() + ")";
	}
}
