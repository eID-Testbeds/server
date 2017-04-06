package com.secunet.tr03129;

import java.io.IOException;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.tr03129.constant.Constants;
import com.secunet.tr03129.ri.CallbackIndicatorType;
import com.secunet.tr03129.ri.DeltaIndicatorType;
import com.secunet.tr03129.ri.EACPKIDVRIProtocolType;
import com.secunet.tr03129.ri.GetBlackListResult;
import com.secunet.tr03129.ri.GetBlackListReturnCodeType;
import com.secunet.tr03129.ri.GetSectorPublicKeyResult;
import com.secunet.tr03129.ri.GetSectorPublicKeyReturnCodeType;
import com.secunet.tr03129.ri.OptionalBinaryType;
import com.secunet.tr03129.ri.OptionalDeltaBaseType;
import com.secunet.tr03129.ri.OptionalMessageIDType;
import com.secunet.tr03129.ri.OptionalStringType;
import com.secunet.tr03129.ri.SendBlackListReturnCodeType;
import com.secunet.tr03129.ri.SendBlackListStatusInfoType;
import com.secunet.tr03129.util.FileUtil;

@WebService(endpointInterface = "com.secunet.tr03129.ri.EACPKIDVRIProtocolType", wsdlLocation = "WEB-INF/wsdl/WS_DV_RestrictedID.wsdl", targetNamespace = "uri:EAC-PKI-DV-Protocol/1.1", serviceName = "RI-Service", portName = "EAC-DV-ProtocolServicePort")
public class RIWebService implements EACPKIDVRIProtocolType
{
	private static final Logger log = LogManager.getLogger(RIWebService.class);

	@Resource
	private WebServiceContext context;

	@Override
	public GetSectorPublicKeyResult getSectorPublicKey(byte[] sectorID)
	{
		GetSectorPublicKeyResult result = new GetSectorPublicKeyResult();
		if (sectorID == null)
		{
			log.log(Level.ERROR, "No sector ID has been provided");
			result.setReturnCode(GetSectorPublicKeyReturnCodeType.FAILURE_SYNTAX);
			return result;
		}
		else
		{
			log.log(Level.INFO, "Incoming Request: getSectorPublicKey(" + Hex.encodeHexString(sectorID) + ")");
		}

		result.setReturnCode(GetSectorPublicKeyReturnCodeType.OK_PK_AVAILABLE);
		try
		{
			result.setSectorPK(FileUtil.getFileFromPath(CertType.SECTOR_PUBLIC_KEY1.getFileLocation()));
		}
		catch (IOException e)
		{
			log.log(Level.ERROR, "No sector key was found for the given ID");
			result.setReturnCode(GetSectorPublicKeyReturnCodeType.FAILURE_SECTOR_ID_UNKNOWN);
			return result;
		}
		return result;
	}

	@Override
	public GetBlackListResult getBlackList(CallbackIndicatorType callbackIndicator, OptionalMessageIDType messageID, OptionalStringType responseURL, DeltaIndicatorType deltaIndicator,
			OptionalDeltaBaseType deltaBase)
	{
		log.log(Level.INFO, "Incoming Request: requestCertificate(" + callbackIndicator + ", " + messageID + ", " + responseURL + ", " + deltaIndicator + ", " + deltaBase + ")");
		GetBlackListResult result = new GetBlackListResult();
		if (callbackIndicator == null)
		{
			log.log(Level.ERROR, "callbackIndicator was null");
			result.setReturnCode(GetBlackListReturnCodeType.FAILURE_SYNTAX);
			return result;
		}


		// blacklist is never changed
		if (deltaIndicator == DeltaIndicatorType.DELTA_LIST)
		{
			if (deltaBase == null)
			{
				log.log(Level.ERROR, "Delta was requested, but no base has been provided");
				result.setReturnCode(GetBlackListReturnCodeType.FAILURE_SYNTAX);
			}
			else
			{
				result.setReturnCode(GetBlackListReturnCodeType.OK_NO_UPDATE_NEEDED);
			}
			return result;
		}

		result.setReturnCode(GetBlackListReturnCodeType.OK_COMPLETE_LIST);
		OptionalStringType cListURL = new OptionalStringType();
		cListURL.setString(buildURI());
		result.setCompleteListURL(cListURL);
		return result;
	}

	private String buildURI()
	{
		HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);

		String uri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/" + Constants.COMPLETE_BL_URL;
		log.info("Full BlackList can download on " + uri);

		return uri;
	}

	@Override
	public SendBlackListReturnCodeType sendBlackList(OptionalMessageIDType messageID, SendBlackListStatusInfoType statusInfo, OptionalBinaryType deltaListAddedItems,
			OptionalBinaryType deltaListRemovedItems, OptionalStringType completeListURL)
	{
		throw new UnsupportedOperationException(Constants.ASYNC_NOT_SUPPORTED);
	}

}
