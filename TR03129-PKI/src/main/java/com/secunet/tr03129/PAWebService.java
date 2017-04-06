package com.secunet.tr03129;

import java.io.IOException;

import javax.jws.WebService;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.tr03129.constant.Constants;
import com.secunet.tr03129.pa.CallbackIndicatorType;
import com.secunet.tr03129.pa.EACPKIDVPAProtocolType;
import com.secunet.tr03129.pa.GetDefectListResult;
import com.secunet.tr03129.pa.GetDefectListReturnCodeType;
import com.secunet.tr03129.pa.GetMasterListResult;
import com.secunet.tr03129.pa.GetMasterListReturnCodeType;
import com.secunet.tr03129.pa.OptionalBinaryType;
import com.secunet.tr03129.pa.OptionalMessageIDType;
import com.secunet.tr03129.pa.OptionalStringType;
import com.secunet.tr03129.pa.SendDefectListReturnCodeType;
import com.secunet.tr03129.pa.SendDefectListStatusInfoType;
import com.secunet.tr03129.pa.SendMasterListReturnCodeType;
import com.secunet.tr03129.pa.SendMasterListStatusInfoType;
import com.secunet.tr03129.util.FileUtil;

@WebService(endpointInterface = "com.secunet.tr03129.pa.EACPKIDVPAProtocolType", wsdlLocation = "WEB-INF/wsdl/WS_DV_PassiveAuth.wsdl", targetNamespace = "uri:EAC-PKI-DV-Protocol/1.1", serviceName = "PA-Service", portName = "EAC-DV-ProtocolServicePort")
public class PAWebService implements EACPKIDVPAProtocolType
{
	private static final Logger log = LogManager.getLogger(PAWebService.class);

	@Override
	public GetMasterListResult getMasterList(CallbackIndicatorType callbackIndicator, OptionalMessageIDType messageID, OptionalStringType responseURL)
	{
		log.log(Level.INFO, "Incoming Request: getMasterList(" + callbackIndicator + ", " + messageID + ", " + responseURL + ")");

		GetMasterListResult result = new GetMasterListResult();
		if (callbackIndicator == null)
		{
			log.log(Level.ERROR, "callbackIndicator was not provided");
			result.setReturnCode(GetMasterListReturnCodeType.FAILURE_SYNTAX);
			return result;
		}

		OptionalBinaryType optionalType;
		try
		{
			optionalType = FileUtil.getList(CertType.MASTERLIST);
		}
		catch (IOException e)
		{
			log.log(Level.ERROR, "Unable to load the master list");
			result.setReturnCode(GetMasterListReturnCodeType.FAILURE_LIST_NOT_AVAILABLE);
			return result;
		}
		log.info("Return MasterList (%s)", optionalType);
		result.setReturnCode(GetMasterListReturnCodeType.OK_LIST_AVAILABLE);
		result.setMasterList(optionalType);

		return result;
	}

	@Override
	public GetDefectListResult getDefectList(CallbackIndicatorType callbackIndicator, OptionalMessageIDType messageID, OptionalStringType responseURL)
	{
		log.log(Level.INFO, "Incoming Request: getDefectList(" + callbackIndicator + ", " + messageID + ", " + responseURL + ")");

		GetDefectListResult result = new GetDefectListResult();
		if (callbackIndicator == null)
		{
			log.log(Level.ERROR, "callbackIndicator was not provided");
			result.setReturnCode(GetDefectListReturnCodeType.FAILURE_SYNTAX);
			return result;
		}

		OptionalBinaryType optionalType;
		try
		{
			optionalType = FileUtil.getList(CertType.DEFECTLIST);
		}
		catch (IOException e)
		{
			log.log(Level.ERROR, "Unable to load the defect list");
			result.setReturnCode(GetDefectListReturnCodeType.FAILURE_LIST_NOT_AVAILABLE);
			return result;
		}
		result.setReturnCode(GetDefectListReturnCodeType.OK_LIST_AVAILABLE);
		log.info("Return DefectList (%s)", optionalType);
		result.setDefectList(optionalType);
		return result;
	}

	@Override
	public SendMasterListReturnCodeType sendMasterList(OptionalMessageIDType messageID, SendMasterListStatusInfoType statusInfo, OptionalBinaryType masterList)
	{
		throw new UnsupportedOperationException(Constants.ASYNC_NOT_SUPPORTED);
	}

	@Override
	public SendDefectListReturnCodeType sendDefectList(OptionalMessageIDType messageID, SendDefectListStatusInfoType statusInfo, OptionalBinaryType defectList)
	{
		throw new UnsupportedOperationException(Constants.ASYNC_NOT_SUPPORTED);
	}

}
