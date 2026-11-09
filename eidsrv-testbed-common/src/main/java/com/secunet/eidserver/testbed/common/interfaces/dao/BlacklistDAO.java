package com.secunet.eidserver.testbed.common.interfaces.dao;

import com.secunet.eidserver.testbed.common.interfaces.entities.Blacklist;

import javax.ejb.Local;

@Local
public interface BlacklistDAO extends GenericDAO<Blacklist>
{

	public Blacklist findByEservice(final char eserviceID);
}
