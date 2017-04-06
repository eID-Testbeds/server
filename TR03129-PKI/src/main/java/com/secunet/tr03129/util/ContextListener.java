package com.secunet.tr03129.util;

import java.security.Security;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.tr03129.cvcert.CVCAIndicator;

@WebListener
public class ContextListener implements ServletContextListener
{

	private static Logger log = LogManager.getLogger(ContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		log.debug("Context initialized...");
		log.debug("Add BouncyCastle Provider...");

		Security.addProvider(new BouncyCastleProvider());

		CVCAIndicator indicator = CVCAIndicator.getCVCAIndicator(sce.getServletContext());

		log.debug("CA Indicator: " + indicator);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		log.debug("Context destroyed...");
		log.debug("Remove BouncyCastle Provider...");
		Security.removeProvider("BC");
	}
}
