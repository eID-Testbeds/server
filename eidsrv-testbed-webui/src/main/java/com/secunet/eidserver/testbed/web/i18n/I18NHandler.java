package com.secunet.eidserver.testbed.web.i18n;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsCaDomainparams;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementWrappingUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.web.core.Log;
import com.vaadin.server.VaadinSession;

public class I18NHandler
{

	static final Set<Locale> supportedLocales;
	public static final Locale GERMAN = Locale.GERMAN;
	public static final Locale ENGLISH = Locale.ENGLISH;

	static final String RESBUNDLE_NAME_GLOBAL = "com.secunet.eidserver.testbed.i18n.web";
	static final Map<String, Map<Locale, ResourceBundle>> localResourceBundleMapping = new HashMap<>();

	static
	{
		supportedLocales = new HashSet<Locale>();
		supportedLocales.add(GERMAN);
		supportedLocales.add(ENGLISH);
	}


	/** set the locale of the application */
	public static void setLocale(Locale locale)
	{
		if (locale == null)
			locale = Locale.ENGLISH;

		VaadinSession.getCurrent().setLocale(locale);
	}

	public static Locale getLocale()
	{
		return VaadinSession.getCurrent().getLocale();
	}

	public static String getImagePath(I18NImage imageResource)
	{
		String path = imageResource.getFilePath();

		if (imageResource.isLocalized())
			path = getLocale().getCountry().toLowerCase() + "/" + path;

		return path;
	}

	public static String getText(I18NImage imageRes)
	{
		return getLocalizedText("Image.Description." + imageRes.name(), getLocale(), false);
	}

	public static String getText(IcsXmlsecEncryptionContentUri algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}

	public static String getText(IcsMandatoryprofile p)
	{
		if (p == null)
			return getLocalizedText("NULL", getLocale(), false);

		return p.value();
	}
	
	public static String getText(IcsCa p)
	{
		if (p == null)
			return getLocalizedText("NULL", getLocale(), false);

		return p.value();
	}
	
	public static String getText(IcsCaDomainparams p)
	{
		if (p == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		return p.value();
	}
	
	public static String getText(IcsXmlsecSignatureDigest p)
	{
		if (p == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		return p.value();
	}

	public static String getText(IcsOptionalprofile p)
	{
		if (p == null)
			return getLocalizedText("NULL", getLocale(), false);

		return p.value();
	}

	public static String getText(IcsTlsVersion algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}

	public static String getText(IcsTlsSignaturealgorithms algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}
	
	public static String getText(IcsXmlsecSignatureUri algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}
	
	public static String getText(IcsXmlsecSignatureCanonicalization algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}

	public static String getText(IcsTlsCiphersuite algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}

	public static String getText(IcsEllipticcurve algo)
	{
		if (algo == null)
			return getLocalizedText("NULL", getLocale(), false);

		return algo.value();
	}
	
	public static String getText(IcsXmlsecEncryptionKeyAgreementUri b)
	{
		if (b == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		return b.value();
	}
	
	public static String getText(IcsXmlsecEncryptionKeyAgreementWrappingUri b)
	{
		if (b == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		return b.value();
	}
	
	public static String getText(IcsXmlsecEncryptionKeyTransportUri b)
	{
		if (b == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		return b.value();
	}

	public static String getText(BigInteger length)
	{
		if (length == null)
			return getLocalizedText("NULL", getLocale(), false);

		return length.toString();
	}
	
	public static String getText(XmlEncryptionKeyAgreement b)
	{
		if (b == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		StringBuilder strBldr = new StringBuilder();
		
		if (b.getKeyAgreementAlgorithm() != null)
			strBldr.append(getText(b.getKeyAgreementAlgorithm()));
		
		if (b.getKeyAgreementAlgorithm() != null && b.getKeyWrappingAlgorithm() != null)
			strBldr.append(" ,");
		
		if (b.getKeyWrappingAlgorithm() != null)
			strBldr.append(getText(b.getKeyWrappingAlgorithm()));
		
		if (b.getBitLengths() != null && strBldr.length() > 0)
			strBldr.append(" ,");
		
		if (b.getBitLengths() != null && strBldr.length() > 0)
			strBldr.append(getText(I18NText.BitLength_Count0, "" + b.getBitLengths().size()));
		
		if (b.getEllipticCurves() != null && strBldr.length() > 0)
			strBldr.append(" ,");
		
		if (b.getEllipticCurves() != null && strBldr.length() > 0)
			strBldr.append(getText(I18NText.EllipticCurve_Count0, "" + b.getEllipticCurves().size()));
			
		
		if (strBldr.length() > 0)
			return strBldr.toString();
		else
			return "Empty XML Encryption Key Agreement";
	}
	
	public static String getText(XmlEncryptionKeyTransport b)
	{
		if (b == null)
			return getLocalizedText("NULL", getLocale(), false);
		
		StringBuilder strBldr = new StringBuilder();
		
		if (b.getTransportAlgorithm() != null)
			strBldr.append(getText(b.getTransportAlgorithm()));
		
		if (b.getBitLengths() != null && strBldr.length() > 0)
			strBldr.append(" ,");
		
		if (b.getBitLengths() != null && strBldr.length() > 0)
			strBldr.append(getText(I18NText.BitLength_Count0, "" + b.getBitLengths().size()));
		
		if (b.getEllipticCurves() != null && strBldr.length() > 0)
			strBldr.append(" ,");
		
		if (b.getEllipticCurves() != null && strBldr.length() > 0)
			strBldr.append(getText(I18NText.EllipticCurve_Count0, "" + b.getEllipticCurves().size()));
			
		
		if (strBldr.length() > 0)
			return strBldr.toString();
		else
			return "Empty Encryption Key Transport";
	}

	public static String getText(I18NText txtResource, Locale locale, Object... paras)
	{
		return getText(txtResource.getKey(), locale, paras);
	}

	public static String getText(I18NText txtResource, Object... paras)
	{
		return getText(txtResource.getKey(), getLocale(), paras);
	}

	private static String getText(String string, Locale locale, Object... paras)
	{
		String text = getLocalizedText(string, locale, true);
		if (paras != null)
		{
			for (int i = 0; i < paras.length; i++)
			{
				if (paras[i] == null)
					text = text.replace("{" + i + "}", "");
				else
					text = text.replace("{" + i + "}", paras[i].toString());
			}
		}

		return text;
	}


	/**
	 * @param resourcetag
	 *            the resource tag
	 * @param locale
	 *            if null, we will get it
	 * @param debugMissingResources
	 *            debug flag
	 * @return the localized text or the resource tag
	 */
	private static String getLocalizedText(final String resourcetag, Locale locale, boolean debugMissingResources)
	{
		if (locale == null)
			locale = getLocale();

		try
		{
			return getBundle(RESBUNDLE_NAME_GLOBAL, locale).getString(resourcetag);
		}
		catch (MissingResourceException mre)
		{
			if (debugMissingResources)
				Log.log(I18NHandler.class).error("Failed to load ResourceBundle {}: {}", RESBUNDLE_NAME_GLOBAL, mre);

			try
			{
				if (debugMissingResources)
					Log.log(I18NHandler.class).warn("Trying Backup ResourceBunde for ENGLISH");
				return getBundle(RESBUNDLE_NAME_GLOBAL, Locale.ENGLISH).getString(resourcetag);
			}
			catch (MissingResourceException mreBackUp)
			{
				if (debugMissingResources)
					Log.log(I18NHandler.class).error("Failed to load ResourceBundle {}: {}", RESBUNDLE_NAME_GLOBAL, mre);
			}
		}

		return resourcetag;
	}

	/**
	 * first searches in cache, else will use {@link ResourceBundle#getBundle(String, Locale)} to get it
	 *
	 * @param bundlePath
	 *            must not be null
	 * @param locale
	 *            must not be null
	 * @return the resource bundle
	 */
	private static ResourceBundle getBundle(String bundlePath, Locale locale)
	{
		if (!localResourceBundleMapping.containsKey(bundlePath))
			localResourceBundleMapping.put(bundlePath, new HashMap<>());

		if (localResourceBundleMapping.get(bundlePath).containsKey(locale))
		{
			return localResourceBundleMapping.get(bundlePath).get(locale);
		}
		else
		{
			final ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, locale);
			if (bundle != null)
				localResourceBundleMapping.get(bundlePath).put(locale, bundle);

			return bundle;
		}
	}

}
