package com.secunet.eidserver.testbed.tls;

enum TlsTesterSteps {
    beginTls,
    transmitAndVerifyData,
    endTls,
    notifyServerVersion,
    notifySelectedCipherSuite,
    notifyAlertReceived,
    notifyHandshakeComplete,
    processServerExtensionEncryptThenMAC 
}
