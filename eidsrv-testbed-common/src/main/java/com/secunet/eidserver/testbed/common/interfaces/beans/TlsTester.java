package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

@Local
public interface TlsTester {
    
    public List<LogMessage> runTlsTest(String host, int port, TlsTestParameters parameters, byte[] outData, byte[] inData) throws UnknownHostException, IOException;

}
