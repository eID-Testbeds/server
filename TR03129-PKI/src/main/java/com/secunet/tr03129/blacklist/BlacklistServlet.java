package com.secunet.tr03129.blacklist;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.tr03129.CertType;
import com.secunet.tr03129.util.FileUtil;

@WebServlet(name = "FileResponse", urlPatterns = { "/FileResponse" })
public class BlacklistServlet extends HttpServlet
{

	/** generated */
	private static final long serialVersionUID = -3043033691465984651L;

	private final static Logger log = LogManager.getLogger(BlacklistServlet.class);


	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("Incoming request: send every request a blacklist response");


		response.setContentType("application/octet-stream;charset=UTF-8");

		log.info("Return Blacklist {0} to client", CertType.BLACKLIST.getFileLocation());
		try (ServletOutputStream output = response.getOutputStream(); InputStream input = FileUtil.getListAsStream(CertType.BLACKLIST);)
		{
			byte[] buffer = new byte[2048];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, bytesRead);
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		processRequest(request, response);
	}

	@Override
	public String getServletInfo()
	{
		return "TR-03129-MOCK";
	}

}
