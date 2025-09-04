package com.secunet.eidserver.testbed.model.database.initialization;

import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Startup
@Singleton
public class StartupBean
{

	private final static Logger logger = LogManager.getLogger(StartupBean.class);

	@EJB
	private DefaultTestCaseDAO defaultTestCaseDAO;

	@EJB
	private TestCaseController testCaseController;

	private DataSource dsc;

	@PostConstruct
	private void init()
	{
		logger.info("Start Initialization...");
		try
		{
			Context ctx = new InitialContext();
			dsc = (DataSource) ctx.lookup("jdbc/H2");
		}
		catch (NamingException e)
		{
			logger.error(e);
		}

		if (!hasDefaults())
		{
			loadDefaults();
		}
		logger.info("Initialization done.");
	}

	private boolean hasDefaults()
	{
		boolean foundDefaults = false;
		try
		{
			foundDefaults = !testCaseController.getAllTestCases().isEmpty();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		return foundDefaults;
	}

	private void loadDefaults()
	{
		try
		{
			testCaseController.generateDefaultTestcases();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}
}
