package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.Set;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

@Local
public interface ReportGenerator
{
	byte[] generateReport(TestCandidate canidateId, Set<Log> logs, ReportType outType);
}
