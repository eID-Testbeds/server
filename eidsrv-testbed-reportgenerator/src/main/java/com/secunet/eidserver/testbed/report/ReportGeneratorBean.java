package com.secunet.eidserver.testbed.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.exceptions.ReportException;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportGenerator;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.report.data.KeyValueReportObject;
import com.secunet.eidserver.testbed.report.data.LogsReportObject;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

@Named
@Stateless
public class ReportGeneratorBean implements ReportGenerator
{

	private static final Logger logger = LogManager.getRootLogger();

	@Override
	public byte[] generateReport(TestCandidate candidate, Set<Log> logs, ReportType outType)
	{

		try
		{
			// get the JRXML template as a stream
			InputStream template = this.getClass().getResourceAsStream("/testreport.jrxml");
			// compile the report from the stream
			JasperReport report = JasperCompileManager.compileReport(template);

			// logs to be displayed in a table:
			List<LogsReportObject> logsTableObjects = new ArrayList<LogsReportObject>();

			// prepare the data to be used
			for (Log log : logs)
			{
				LogsReportObject ltoSummary = new LogsReportObject();
				ltoSummary.setId("0");
				String summary = (log.isSuccess()) ? "Successful" : "Unsuccessful";
				summary += " test run. Run date was " + log.getRunDate();
				ltoSummary.setLogMessage(summary);
				ltoSummary.setTestCaseName(log.getTestCase());
				logsTableObjects.add(ltoSummary);
				int id = 1;
				List<LogMessage> logMessages = log.getLogMessages();
				for (LogMessage logMessage : logMessages)
				{
					// traverse and process, e.g. fill Report POJOs
					LogsReportObject lto = new LogsReportObject();
					lto.setId(String.valueOf(id++));
					lto.setTestCaseName(log.getTestCase());
					StringBuffer sb = new StringBuffer();
					sb.append(logMessage.getMessage());
					lto.setLogMessage(sb.toString());
					logsTableObjects.add(lto);
				}
			}
			List<KeyValueReportObject> profileData = new ArrayList<KeyValueReportObject>();
			profileData.add(new KeyValueReportObject("Vendor", candidate.getVendor()));
			profileData.add(new KeyValueReportObject("Version Major", String.valueOf(candidate.getVersionMajor())));
			profileData.add(new KeyValueReportObject("Version Minor", String.valueOf(candidate.getVersionMinor())));
			profileData.add(new KeyValueReportObject("Version Subminor", String.valueOf(candidate.getVersionSubminor())));
			profileData.add(new KeyValueReportObject("API Major", String.valueOf(candidate.getApiMajor())));
			profileData.add(new KeyValueReportObject("API Minor", String.valueOf(candidate.getApiMinor())));
			profileData.add(new KeyValueReportObject("API Subminor", String.valueOf(candidate.getApiSubminor())));
			profileData.add(new KeyValueReportObject("eCard-API URL", candidate.getEcardapiUrl().toString()));
			profileData.add(new KeyValueReportObject("SAML URL", candidate.getSamlUrl().toString()));
			profileData.add(new KeyValueReportObject("Name", candidate.getCandidateName()));
			profileData.add(new KeyValueReportObject("Optional Profiles", enumIterableToString(candidate.getOptionalProfiles())));
			profileData.add(new KeyValueReportObject("Mandatory Profiles", enumIterableToString(candidate.getMandatoryProfiles())));

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("tableData", logsTableObjects);
			params.put("profileData", profileData);

			// fill out the report into a print object, ready for export.
			JasperPrint print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

			// export
			JRExporter exporter = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (outType == ReportType.PDF)
			{
				exporter = new JRPdfExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
				exporter.exportReport();

			}
			else if (outType == ReportType.DOCX)
			{
				exporter = new JRDocxExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
				exporter.exportReport();

			}
			else
			{
				String msg = "Unknown ReportType: " + outType != null ? outType.toString() : "null";
				throw new ReportException(msg);
			}

			return baos.toByteArray();

		}
		catch (Exception e)
		{
			logger.error("Report error: ", e);
			throw new ReportException(e.getMessage());
		}
	}

	private <T extends Enum<T>> String enumIterableToString(Iterable<T> iterable)
	{
		Iterable<T> it = iterable == null ? Collections.<T> emptyList() : iterable;
		StringBuffer sb = new StringBuffer();
		for (T t : it)
		{
			if (sb.length() != 0)
			{
				sb.append(", ");
			}
			sb.append(t.toString());
		}
		return sb.toString();
	}
}
