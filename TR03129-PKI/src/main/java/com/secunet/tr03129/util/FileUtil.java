package com.secunet.tr03129.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.secunet.tr03129.CertType;
import com.secunet.tr03129.pa.OptionalBinaryType;

/**
 *
 * @author maenz.torsten
 */
public class FileUtil
{

	private static final Logger log = Logger.getLogger(FileUtil.class.getName());

	private FileUtil()
	{
	}


	private static InputStream getFile(String path)
	{
		log.log(Level.INFO, "Loading file " + path);
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	public static InputStream getListAsStream(CertType type) throws IOException
	{
		return getFile(type.getFileLocation());
	}

	public static byte[] getFileFromPath(String path) throws IOException
	{
		return IOUtils.toByteArray(getFile(path));
	}

	public static byte[] getRaw(CertType type)
	{
		if (type == null)
		{
			log.log(Level.INFO, "Type is not set, can not load");
			return null;
		}

		String file = type.getFileLocation();
		log.log(Level.INFO, "Load file " + file + " from resource folder");

		InputStream stream = getFile(file);

		if (stream == null)
		{
			log.info("File not found: " + file);
		}
		else
		{
			try
			{
				return IOUtils.toByteArray(stream);
			}
			catch (IOException e)
			{
				log.log(Level.SEVERE, "File can not convert to byte array, reason: ", e);
				return null;
			}
		}

		return null;
	}

	public static OptionalBinaryType getList(CertType type) throws IOException
	{
		OptionalBinaryType binary = new OptionalBinaryType();
		byte[] plain = IOUtils.toByteArray(getFile(type.getFileLocation()));
		binary.setBinary(plain);
		return binary;
	}

}
