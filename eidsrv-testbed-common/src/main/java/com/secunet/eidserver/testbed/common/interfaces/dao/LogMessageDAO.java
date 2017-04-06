package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

@Local
public interface LogMessageDAO extends GenericDAO<LogMessage>
{


}
