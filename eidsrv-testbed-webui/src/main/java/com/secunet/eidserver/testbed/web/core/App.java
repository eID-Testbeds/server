package com.secunet.eidserver.testbed.web.core;

import java.io.InputStream;
import java.util.Properties;

import com.vaadin.server.VaadinService;

/**
 * Globally information and functionallity for the WebApplication itself.
 */
public class App
{
	/** This system property can be used to activate the MOCKING behaviour for the backend, if the current state of this application is not in production mode */
	public static final String SYSPROP_MOCKING = "MOCKING";

	private static String version = null;

	/**
	 * @return the current version as string, not i18n
	 */
	public static String getWebAppVersion()
	{
		if (version == null)
		{
			try (final InputStream inputStream = App.class.getResourceAsStream("/META-INF/maven/com.secunet.eidserver.testbed.web/testbed-webui/pom.properties");)
			{
				Properties props = new Properties();
				props.load(inputStream);
				version = props.getProperty("version", "n/a");
			}
			catch (Exception e)
			{
				version = "n/a";
			}
		}

		return version;
	}


	/**
	 * @return the product name, not i18n
	 */
	public static String getProductName()
	{
		return "secunet(Server Testbed";
	}


	/**
	 * @return if {@link VaadinService} deployment configuration is in production mode
	 */
	public static boolean isProductionMode()
	{
		return VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode();
		// return false;
	}

	/**
	 * @return true if NOT {@link #isProductionMode()} AND {@link #SYSPROP_MOCKING} is set to {@link Boolean#TRUE#toString()}
	 */
	public static boolean isMockingMode()
	{
		final String mockPropertyValue = VaadinService.getCurrent().getDeploymentConfiguration().getApplicationOrSystemProperty(SYSPROP_MOCKING, "true");

		return !isProductionMode() && Boolean.TRUE.toString().equals(mockPropertyValue);
	}


	public static String getBinaryMessageDigest()
	{
		return "SHA1";
	}
}
