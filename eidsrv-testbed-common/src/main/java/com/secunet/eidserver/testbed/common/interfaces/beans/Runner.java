package com.secunet.eidserver.testbed.common.interfaces.beans;

import com.secunet.eidserver.testbed.common.exceptions.TestrunException;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import org.opensaml.security.x509.X509Credential;

import javax.ejb.Local;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Local
public interface Runner
{

	/**
	 * Run the given testcase using the data provided by the profile.
	 * 
	 * @param profile
	 * @param tCase
	 * @return
	 */
	public List<LogMessage> runTest(TestCandidate profile, TestCase tCase) throws TestrunException;

    X509Credential getSignatureCreds(CryptoHelper.Protocol protocol, CryptoHelper.Algorithm algorithm, EService service, TestCandidate candidate) throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException;

	X509Credential getEncryptionCreds(CryptoHelper.Protocol protocol, CryptoHelper.Algorithm algorithm, EService service, TestCandidate candidate) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException;
}
