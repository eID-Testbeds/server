package com.secunet.eidserver.testbed.model.entities;

import com.secunet.eidserver.testbed.common.interfaces.entities.Blacklist;
import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Entity
@Table(name = "CRYPTOFILE")
@NamedQuery(name = "CryptoFileEntity.findAll", query = "SELECT t FROM CryptoFileEntity t")
public class CryptoFileEntity extends BaseEntity implements Serializable, CryptoFile
{
	private static final long serialVersionUID = 1603400191631663139L;

	private static final Logger logger = LogManager.getRootLogger();

	@Column(name = "FILENAME")
	private String filename;

	@Lob
	@Column(length = 500000)
	private byte[] content;

	@Override
	public byte[] getContent()
	{
		return content;
	}

	@Override
	public void setContent(byte[] content)
	{
		this.content = content;
	}

	@Override
	public String getFilename()
	{
		return filename;
	}

	@Override
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + Arrays.hashCode(content);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CryptoFileEntity other = (CryptoFileEntity) obj;
		if (filename == null)
		{
			if (other.filename != null)
				return false;
		}
		else if (!filename.equals(other.filename))
			return false;
		if (!Arrays.equals(content, other.content))
			return false;
		return true;
	}
}
