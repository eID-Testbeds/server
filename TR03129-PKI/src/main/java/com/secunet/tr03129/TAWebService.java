package com.secunet.tr03129;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificateHolderReference;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionData;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionDataList;
import com.secunet.testbedutils.cvc.cvcertificate.CVExtensionType;
import com.secunet.testbedutils.cvc.cvcertificate.CVSignKeyHolder;
import com.secunet.testbedutils.cvc.cvcertificate.CertHolderRole;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.IPrivateKeySource;
import com.secunet.testbedutils.cvc.cvcertificate.TAAlgorithm;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVAuthorityRefNotValidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceInvalidCountryCode;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceTooLong;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidKeySourceException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVMissingKeyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVSignOpKeyMismatchException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;
import com.secunet.tr03129.constant.Constants;
import com.secunet.tr03129.cvcert.CVAuthorizationType;
import com.secunet.tr03129.cvcert.CVCAIndicator;
import com.secunet.tr03129.cvcert.CVTermIndicator;
import com.secunet.tr03129.cvcert.CertificateDescription;
import com.secunet.tr03129.ta.CallbackIndicatorType;
import com.secunet.tr03129.ta.CertificateSeqType;
import com.secunet.tr03129.ta.EACPKIDVTAProtocolType;
import com.secunet.tr03129.ta.GetCACertificatesResult;
import com.secunet.tr03129.ta.GetCACertificatesReturnCodeType;
import com.secunet.tr03129.ta.OptionalMessageIDType;
import com.secunet.tr03129.ta.OptionalStringType;
import com.secunet.tr03129.ta.RequestCertificateResult;
import com.secunet.tr03129.ta.RequestCertificateReturnCodeType;
import com.secunet.tr03129.ta.SendCertificatesResult;
import com.secunet.tr03129.ta.SendCertificatesStatusInfoType;
import com.secunet.tr03129.util.FileUtil;
import com.secunet.tr03129.util.KeyUtil;

@WebService(endpointInterface = "com.secunet.tr03129.ta.EACPKIDVTAProtocolType", wsdlLocation = "WEB-INF/wsdl/WS_DV_TerminalAuth.wsdl", targetNamespace = "uri:EAC-PKI-DV-Protocol/1.1", serviceName = "TA-Service", portName = "EAC-DV-ProtocolServicePort")
public class TAWebService implements EACPKIDVTAProtocolType
{
	private static final Logger log = LogManager.getLogger(TAWebService.class);

	@Resource
	private WebServiceContext context;

	@Override
	public RequestCertificateResult requestCertificate(CallbackIndicatorType callbackIndicator, OptionalMessageIDType messageID, OptionalStringType responseURL, byte[] certReq)
	{
		log.log(Level.INFO, "Incoming Request: requestCertificate(" + callbackIndicator + ", " + messageID + ", " + responseURL + ", " + certReq + ")");
		RequestCertificateResult requestCertificateResult = new RequestCertificateResult();
		if (callbackIndicator == null || certReq == null)
		{
			log.log(Level.ERROR, "callbackIndicator or certReq was null");
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_SYNTAX);
			return requestCertificateResult;
		}

		final CVCAIndicator caIndicator = getCVCAIndicator();
		DataBuffer buffer = new DataBuffer(certReq);
		try
		{
			CVCertificate cvReq = new CVCertificate(buffer);

			if (cvReq.hasOuterSignature())
			{
				log.log(Level.INFO, "Incoming foreign Request " + cvReq.toString());
			}
			else
			{
				log.log(Level.INFO, "Incoming initial Request " + cvReq.toString());
			}

			CVCertificate cvcaCert = null;
			CVCertificate dvcaCert = null;
			IPrivateKeySource dvcaKeySource = null;
			switch (getCVCAIndicator())
			{
				case CA_1:
					cvcaCert = new CVCertificate(new DataBuffer(CertType.CVCA1.getRaw()));
					dvcaCert = new CVCertificate(new DataBuffer(CertType.DVCA1A.getRaw()));
					dvcaKeySource = KeyUtil.getPrivateKeySource(CertType.DVCA1A_KEY);
					break;
				case CA_2:
					cvcaCert = new CVCertificate(new DataBuffer(CertType.CVCA2A.getRaw()));
					dvcaCert = new CVCertificate(new DataBuffer(CertType.DVCA2A.getRaw()));
					dvcaKeySource = KeyUtil.getPrivateKeySource(CertType.DVCA2A_KEY);
					break;
				default:
					log.log(Level.ERROR, "Invalid servlet context");
					requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_SYNTAX);
					return requestCertificateResult;
			}

			final DataBuffer term = createCertificate(caIndicator, cvReq, dvcaCert, dvcaKeySource, cvcaCert);

			CertificateSeqType seqType = new CertificateSeqType();
			seqType.getCertificate().add(term.toByteArray());

			requestCertificateResult.setCertificateSeq(seqType);
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.OK_CERT_AVAILABLE);
			log.printf(Level.DEBUG, "Created certificate: " + new CVCertificate(term).toString());
		}
		catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException e)
		{
			log.log(Level.WARN, "Unable to parse CV request", e);
			// TODO differentiate between error types
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_REQUEST_NOT_ACCEPTED);
			return requestCertificateResult;
		}
		catch (IOException e)
		{
			log.log(Level.WARN, "Unable to load certificates or keys from the file system", e);
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_REQUEST_NOT_ACCEPTED);
			return requestCertificateResult;
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			log.log(Level.WARN, "Error while processing the private key for the CA", e);
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_REQUEST_NOT_ACCEPTED);
			return requestCertificateResult;
		}
		catch (CVCertificateHolderReferenceInvalidCountryCode | CVCertificateHolderReferenceTooLong | CVAuthorityRefNotValidException | CVSignOpKeyMismatchException | CVInvalidKeySourceException
				| CVMissingKeyException | CVKeyTypeNotSupportedException e)
		{
			log.log(Level.WARN, "Unable to create a CV certificate", e);
			// TODO differentiate between error types
			requestCertificateResult.setReturnCode(RequestCertificateReturnCodeType.FAILURE_INTERNAL_ERROR);
			return requestCertificateResult;
		}

		return requestCertificateResult;
	}

	/**
	 * Create a new CV certificate using the given parameters
	 * 
	 * @param caIndicator
	 * @param cvReq
	 * @param dvca
	 * @param keySource
	 * @param cvca
	 * @return
	 * @throws CVAuthorityRefNotValidException
	 * @throws CVCertificateHolderReferenceTooLong
	 * @throws CVCertificateHolderReferenceInvalidCountryCode
	 * @throws CVKeyTypeNotSupportedException
	 * @throws CVMissingKeyException
	 * @throws CVInvalidKeySourceException
	 * @throws CVSignOpKeyMismatchException
	 * @throws CVInvalidOidException
	 * @throws IOException
	 */
	private DataBuffer createCertificate(final CVCAIndicator caIndicator, final CVCertificate cvReq, final CVCertificate dvca, final IPrivateKeySource keySource, final CVCertificate cvca)
			throws CVCertificateHolderReferenceInvalidCountryCode, CVCertificateHolderReferenceTooLong, CVAuthorityRefNotValidException, CVSignOpKeyMismatchException, CVInvalidKeySourceException,
			CVMissingKeyException, CVKeyTypeNotSupportedException, CVInvalidOidException, IOException
	{
		log.debug("create terminal certificate: cvca=" + cvca.getCertHolderRef() + " , dvca=" + dvca.getCertHolderRef() + ", term=" + cvReq.getCertHolderRef());

		CVCertificate certObj = new CVCertificate();
		certObj.setCertAuthRef(dvca.getCertHolderRef());

		certObj.getEffDate().setDate(dvca.getEffDate().getDate());
		certObj.getExpDate().setDate(dvca.getExpDate().getDate());
		certObj.setCertHolderRef(cvReq.getCertHolderRef());

		CVCertificateHolderReference cvHolderReference = new CVCertificateHolderReference(cvReq.getCertHolderRef());
		CVTermIndicator termIndicator = CVTermIndicator.getCertificateAuthorization(cvHolderReference);
		log.debug("Terminal Indicator: " + termIndicator);

		CVAuthorizationType authTyp = CVAuthorizationType.getCertificateAuthorization(termIndicator);
		certObj.getCertHolderAuth().setAuth(authTyp.getAuthorization());
		certObj.getCertHolderAuth().getAuth().setRole(CertHolderRole.Terminal);

		certObj.setPublicKey(cvReq.getPublicKey());
		certObj.getPublicKey().setIncludeDomainParam(false);
		certObj.setExtension(cvReq.getExtension());

		if (cvReq.getExtension().getExtensions() == null)
		{
			log.info("No extensions found, create new certificate extensions");
			createExtensions(certObj, caIndicator, termIndicator);
		}

		CVSignKeyHolder cvSignKeyHolder = new CVSignKeyHolder();
		TAAlgorithm signatureTaAlgorithm = cvca.getPublicKey().getAlgorithm();
		cvSignKeyHolder.setAlgorithm(signatureTaAlgorithm);
		cvSignKeyHolder.setKeySource(keySource);

		certObj.setSignKey(cvSignKeyHolder);
		certObj.getPublicKey().generateCertPubKey();
		return certObj.generateCert();
	}

	/**
	 * Create the terminal extensions
	 * 
	 * @param certObj
	 * @param caIndicator
	 * @param termIndicator
	 * @throws IOException
	 */
	private void createExtensions(CVCertificate certObj, CVCAIndicator caIndicator, CVTermIndicator termIndicator) throws IOException
	{
		CertificateDescription desc = CertificateDescription.getCertificateDescription(caIndicator, termIndicator);

		CVExtensionData descData = null;
		if (desc != null)
		{
			descData = new CVExtensionData();
			descData.setType(CVExtensionType.extDescription);
			descData.setHash1(new DataBuffer(desc.getHash()));
		}
		else
		{
			throw new InvalidParameterException("No certificate description has been found for the given tags");
		}

		// create sector public key hash
		// term indicator has two sector public keys, other have only one
		byte[] sectorPK1 = DigestUtils.sha256(FileUtil.getRaw(CertType.SECTOR_PUBLIC_KEY1));

		CVExtensionData sectorData = new CVExtensionData();
		sectorData.setType(CVExtensionType.extSector);
		sectorData.setHash1(new DataBuffer(sectorPK1));

		if (termIndicator == CVTermIndicator.D)
		{
			log.info("eService D requested, adding second sector key");

			byte[] sectorPK2 = DigestUtils.sha256(FileUtil.getRaw(CertType.SECTOR_PUBLIC_KEY2));
			sectorData.setHash2(new DataBuffer(sectorPK2));
		}

		CVExtensionDataList dataList = new CVExtensionDataList();
		if (descData != null)
		{
			dataList.add(descData);
		}
		dataList.add(sectorData);
		certObj.getExtension().setExtensions(dataList);
	}

	@Override
	public GetCACertificatesResult getCACertificates(CallbackIndicatorType callbackIndicator, OptionalMessageIDType messageID, OptionalStringType responseURL)
	{
		log.log(Level.INFO, "Incoming Request: getCACertificates(" + callbackIndicator + ", " + messageID + ", " + responseURL + ")");
		GetCACertificatesResult result = new GetCACertificatesResult();
		if (callbackIndicator == null)
		{
			log.log(Level.ERROR, "callbackIndicator was null");
			result.setReturnCode(GetCACertificatesReturnCodeType.FAILURE_SYNTAX);
			return result;
		}

		final CVCAIndicator caIndicator = getCVCAIndicator();
		CertificateSeqType seqType = new CertificateSeqType();
		try
		{
			switch (caIndicator)
			{
				case CA_1:
					seqType.getCertificate().add(CertType.CVCA1.getRaw());
					seqType.getCertificate().add(CertType.DVCA1A.getRaw());
					break;
				case CA_2:
					seqType.getCertificate().add(CertType.CVCA2A.getRaw());
					seqType.getCertificate().add(CertType.CVCA2_LINK1.getRaw());
					seqType.getCertificate().add(CertType.CVCA2_LINK2.getRaw());
					seqType.getCertificate().add(CertType.DVCA2A.getRaw());
					break;
				default:
					log.log(Level.ERROR, "Invalid servlet context");
					result.setReturnCode(GetCACertificatesReturnCodeType.FAILURE_SYNTAX);
					return result;
			}
		}
		catch (IOException e)
		{
			log.log(Level.ERROR, "Could not read the necessary data", e);
		}

		log.log(Level.INFO, "Return %s certificates", seqType.getCertificate().size());

		if (log.isDebugEnabled())
		{
			int i = 1;
			for (byte[] rawCert : seqType.getCertificate())
			{
				log.log(Level.DEBUG, "%d Certificate", i);
				try
				{
					log.log(Level.DEBUG, new CVCertificate(new DataBuffer(rawCert)).toString());
				}
				catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException e)
				{
					if (callbackIndicator == null)
					{
						log.log(Level.WARN, "Unable to parse CV certificate", e);
						// note: this is an error during the creation of debug output, do not treat it as error and return a failure
					}
				}
			}
		}

		result.setReturnCode(GetCACertificatesReturnCodeType.OK_CERT_AVAILABLE);
		result.setCertificateSeq(seqType);
		return result;
	}

	@Override
	public SendCertificatesResult sendCertificates(OptionalMessageIDType messageID, SendCertificatesStatusInfoType statusInfo, CertificateSeqType certificateSeq)
	{
		throw new UnsupportedOperationException(Constants.ASYNC_NOT_SUPPORTED);
	}

	/**
	 * Return the CA indicator from this servlet's path
	 * 
	 * @return
	 */
	private CVCAIndicator getCVCAIndicator() throws IllegalStateException
	{
		String contextPath = null;

		if (context == null)
		{
			throw new IllegalStateException("The WebServiceContext was not set for this server");
		}

		HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
		contextPath = request.getContextPath();

		CVCAIndicator indicator = CVCAIndicator.getCVCAIndicator(contextPath);
		log.info("Found CVCA indicator: " + indicator);

		return indicator;
	}

}
