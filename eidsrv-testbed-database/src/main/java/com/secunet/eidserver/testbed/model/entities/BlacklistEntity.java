package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.Blacklist;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import de.bsi.testbedutils.cvc.cvcertificate.CVCertificate;
import de.bsi.testbedutils.cvc.cvcertificate.DataBuffer;
import de.bsi.testbedutils.cvc.cvcertificate.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "BLACKLIST")
@NamedQuery(name = "BlacklistEntity.findAll", query = "SELECT t FROM CertificateCVEntity t")
public class BlacklistEntity extends BaseEntity implements Serializable, Blacklist
{
	private static final long serialVersionUID = 1603400191631663139L;

	private static final Logger logger = LogManager.getRootLogger();

	@Column(name = "ESERVICE")
	private char eserviceID;

	@Lob
	@Column(length = 500000)
	private byte[] blacklist;

	@Override
	public byte[] getContent()
	{
		return blacklist;
	}

	@Override
	public void setContent(byte[] content)
	{
		this.blacklist = content;
	}

	@Override
	public char getEserviceID()
	{
		return eserviceID;
	}

	@Override
	public void setEserviceID(char eServiceID)
	{
		this.eserviceID = eServiceID;
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
		result = prime * result + Character.hashCode(eserviceID);
		result = prime * result + Arrays.hashCode(blacklist);
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
		BlacklistEntity other = (BlacklistEntity) obj;
		if (eserviceID != other.eserviceID)
			return false;
		if (!Arrays.equals(blacklist, other.blacklist))
			return false;
		return true;
	}
}
