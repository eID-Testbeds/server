package com.secunet.eidserver.testbed.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.ClientCertificateType;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.NewSessionTicket;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.crypto.tls.TlsECCUtils;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.crypto.tls.TlsUtils;
import org.bouncycastle.util.Arrays;

import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

class TesterTlsClient extends DefaultTlsClient implements TlsAuthentication
{

	private final LogMessageDAO logMessageDAO;

	private final List<LogMessage> result;

	private final TlsTestParameters parameters;

	public TesterTlsClient(final LogMessageDAO logMessageDAO, final List<LogMessage> result, final TlsTestParameters parameters)
	{
		this.logMessageDAO = logMessageDAO;
		this.result = result;
		this.parameters = parameters;
	}

	@Override
	public TlsAuthentication getAuthentication() throws IOException
	{
		return this;
	}

	// BEGIN interface TlsAuthentication
	@Override
	public void notifyServerCertificate(Certificate serverCertificate) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException
	{
		TlsCredentials credentials = null;

		if (parameters.isClientAuthentication())
		{
            Certificate certificate = parameters.getClientCertificate();
            AsymmetricKeyParameter privateKey = parameters.getClientPrivateKey();
            short signatureAlgorithm = SignatureAlgorithm.rsa;

            short[] certificateTypes = certificateRequest.getCertificateTypes();
            if (certificateTypes == null || !Arrays.contains(certificateTypes, ClientCertificateType.rsa_sign))
            {
                return null;
            }
            
            SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
            if (certificateRequest.getSupportedSignatureAlgorithms() != null)
            {
                for (int i = 0; i < certificateRequest.getSupportedSignatureAlgorithms().size(); ++i)
                {
                    SignatureAndHashAlgorithm alg = (SignatureAndHashAlgorithm)
                    		certificateRequest.getSupportedSignatureAlgorithms().elementAt(i);
                    if (alg.getSignature() == signatureAlgorithm)
                    {
                        signatureAndHashAlgorithm = alg;
                        break;
                    }
                }

                if (signatureAndHashAlgorithm == null)
                {
                    return null;
                }
            }

            credentials = new DefaultTlsSignerCredentials(context, certificate, privateKey, signatureAndHashAlgorithm);
		}

		return credentials;
	}
	// END interface TlsAuthentication

	@Override
	public int[] getCipherSuites()
	{
		return parameters.getCipherSuites();
	}

	@Override
	public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException
	{
		// Check, if selected TLS version matches expectation
		if (parameters.getSelectedVersion() != null)
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.notifyServerVersion.toString());
			if (parameters.getSelectedVersion().equals(serverVersion))
			{
				msg.setSuccess(true);
				msg.setMessage("Server selected expected TLS version " + parameters.getSelectedVersion() + ".");
			}
			else
			{
				msg.setSuccess(false);
				msg.setMessage("Expected TLS version " + parameters.getSelectedVersion() + ", but server selected " + serverVersion + ".");
			}
			result.add(msg);
		}

		super.notifyServerVersion(serverVersion);
	}

	@Override
	public void notifySessionID(byte[] sessionID)
	{
		// TODO Auto-generated method stub
		super.notifySessionID(sessionID);
	}

	@Override
	public void notifySelectedCipherSuite(int selectedCipherSuite)
	{
		// Check, if selected cipher suite matches expectation
		if (parameters.getSelectedCipherSuite() != -1)
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.notifySelectedCipherSuite.toString());
			if (parameters.getSelectedCipherSuite() == selectedCipherSuite)
			{
				msg.setSuccess(true);
				msg.setMessage("Server selected expected cipher suite " + parameters.getSelectedCipherSuite() + ".");
			}
			else
			{
				msg.setSuccess(false);
				msg.setMessage("Expected cipher suite " + parameters.getSelectedCipherSuite() + ", but server selected " + selectedCipherSuite + ".");
			}
			result.add(msg);
		}

		super.notifySelectedCipherSuite(selectedCipherSuite);
	}

	@Override
	public void notifySelectedCompressionMethod(short selectedCompressionMethod)
	{
		// TODO Auto-generated method stub
		super.notifySelectedCompressionMethod(selectedCompressionMethod);
	}

	@Override
	public void notifyNewSessionTicket(NewSessionTicket newSessionTicket) throws IOException
	{
		// TODO Auto-generated method stub
		super.notifyNewSessionTicket(newSessionTicket);
	}

	@Override
	public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException
	{
		// TODO Auto-generated method stub

		// The implementation in super class AbstractTlsPeer may throw an exception.
		// This must be suppressed, so we skip the super call.
		// super.notifySecureRenegotiation(secureRenegotiation);
	}

	@Override
	public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
	{
		// TODO Auto-generated method stub
		super.notifyAlertRaised(alertLevel, alertDescription, message, cause);
	}

	@Override
	public void notifyAlertReceived(short alertLevel, short alertDescription)
	{
		// Check, if received alert level matches expectation
		// Please, cross check with method notifyHandshakeComplete(), see below
		if (parameters.getAlertLevelReceived() != -1)
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.notifyAlertReceived.toString());
			if (parameters.getAlertLevelReceived() == 0)
			{
				msg.setSuccess(false);
				msg.setMessage("Expected to complete handshake, but server sent alert level " + alertLevel + ".");
			}
			else if (parameters.getAlertLevelReceived() == alertLevel)
			{
				msg.setSuccess(true);
				msg.setMessage("Server sent expected alert level " + parameters.getAlertLevelReceived() + ".");
			}
			else
			{
				msg.setSuccess(false);
				msg.setMessage("Expected alert level " + parameters.getAlertLevelReceived() + ", but server sent " + alertLevel + ".");
			}
			result.add(msg);
		}

		super.notifyAlertReceived(alertLevel, alertDescription);
	}

	@Override
	public void notifyHandshakeComplete() throws IOException
	{
		// Check, if we expected to receive a TLS alert
		// Please, cross check with method notifyAlertReceived(short alertLevel, short alertDescription), see above
		if (parameters.getAlertLevelReceived() != -1)
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.notifyHandshakeComplete.toString());
			if (parameters.getAlertLevelReceived() == 0)
			{
				msg.setSuccess(true);
				msg.setMessage("Server completed handshake as expected.");
			}
			else
			{
				msg.setSuccess(false);
				msg.setMessage("Expected alert level" + parameters.getAlertLevelReceived() + ", but server completed handshake.");
			}
			result.add(msg);
		}

		super.notifyHandshakeComplete();
	}

	@Override
	public ProtocolVersion getClientVersion()
	{
		return parameters.getVersion();
	}

	@Override
	public ProtocolVersion getMinimumVersion()
	{
		// TODO Auto-generated method stub
		return super.getMinimumVersion();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Hashtable getClientExtensions() throws IOException
	{

		Hashtable clientExtensions = super.getClientExtensions();

		ProtocolVersion clientVersion = context.getClientVersion();

		/*
		 * RFC 5246 7.4.1.4.1. Note: this extension is not meaningful for TLS versions prior to 1.2.
		 * Clients MUST NOT offer it if they are offering prior versions.
		 */
		if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(clientVersion))
		{
			// Usually, this extension is already prepared in super.getClientExtensions()
			// We just set it again to change it according to our parameters.

			this.supportedSignatureAlgorithms = parameters.getSupportedSignatureAlgorithms();
			clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
			TlsUtils.addSignatureAlgorithmsExtension(clientExtensions, supportedSignatureAlgorithms);
		}

		if (TlsECCUtils.containsECCCipherSuites(getCipherSuites()))
		{
			// Usually, this extension is already prepared in super.getClientExtensions()
			// We just set it again to change it according to our parameters.

			this.namedCurves = parameters.getEllipticCurves();
			clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
			TlsECCUtils.addSupportedEllipticCurvesExtension(clientExtensions, namedCurves);
		}

		if (parameters.getEncryptThenMACExtension())
		{
			clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
			TlsExtensionsUtils.addEncryptThenMACExtension(clientExtensions);
		}

		return clientExtensions;

	}


	@SuppressWarnings("rawtypes")
	@Override
	public void processServerExtensions(Hashtable serverExtensions) throws IOException
	{

		// Check, if selected EncryptThenMACExtension matches expectation
		if (parameters.getEncryptThenMACExtension())
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.processServerExtensionEncryptThenMAC.toString());
			boolean serverHasEncryptThenMACExtension = TlsExtensionsUtils.hasEncryptThenMACExtension(serverExtensions);
			if (parameters.getEncryptThenMACExtensionEnabled() == serverHasEncryptThenMACExtension)
			{
				msg.setSuccess(true);
				msg.setMessage("Server responded as expected and" + (serverHasEncryptThenMACExtension ? " " : " did not ") + "set encrypt-then-MAC extension.");
			}
			else
			{
				msg.setSuccess(false);
				msg.setMessage("Server responded NOT as expected and" + (serverHasEncryptThenMACExtension ? " " : " did not ") + "set encrypt-then-MAC extension.");
			}
			result.add(msg);
		}

		super.processServerExtensions(serverExtensions);
	}


}
