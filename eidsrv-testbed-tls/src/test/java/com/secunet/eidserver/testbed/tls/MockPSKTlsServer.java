package com.secunet.eidserver.testbed.tls;

import java.io.IOException;
import java.io.PrintStream;

import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.PSKTlsServer;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.TlsEncryptionCredentials;
import org.bouncycastle.crypto.tls.TlsPSKIdentityManager;
import org.bouncycastle.util.Strings;

import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;

class MockPSKTlsServer
  extends PSKTlsServer
{
	private final TlsTestParameters tlsParameters;
	
  MockPSKTlsServer(TlsTestParameters tlsParameters)
  {
    super(new MyIdentityManager(tlsParameters.getPSK()));
    this.tlsParameters = tlsParameters;
  }
  
  public void notifyAlertRaised(short paramShort1, short paramShort2, String paramString, Throwable paramThrowable)
  {
    PrintStream localPrintStream = paramShort1 == 2 ? System.err : System.out;
    localPrintStream.println("TLS-PSK server raised alert: " + AlertLevel.getText(paramShort1) + ", " + AlertDescription.getText(paramShort2));
    if (paramString != null) {
      localPrintStream.println("> " + paramString);
    }
    if (paramThrowable != null) {
      paramThrowable.printStackTrace(localPrintStream);
    }
  }
  
  public void notifyAlertReceived(short paramShort1, short paramShort2)
  {
    PrintStream localPrintStream = paramShort1 == 2 ? System.err : System.out;
    localPrintStream.println("TLS-PSK server received alert: " + AlertLevel.getText(paramShort1) + ", " + AlertDescription.getText(paramShort2));
  }
  
  public void notifyHandshakeComplete()
    throws IOException
  {
    super.notifyHandshakeComplete();
    byte[] arrayOfByte = context.getSecurityParameters().getPSKIdentity();
    if (arrayOfByte != null)
    {
      String str = Strings.fromUTF8ByteArray(arrayOfByte);
      System.out.println("TLS-PSK server completed handshake for PSK identity: " + str);
    }
  }
  
  protected int[] getCipherSuites()
  {
    return tlsParameters.getCipherSuites();
  }
  
  protected ProtocolVersion getMaximumVersion()
  {
    return ProtocolVersion.TLSv12;
  }
  
  protected ProtocolVersion getMinimumVersion()
  {
    return ProtocolVersion.TLSv12;
  }
  
  public ProtocolVersion getServerVersion()
    throws IOException
  {
    ProtocolVersion localProtocolVersion = super.getServerVersion();
    System.out.println("TLS-PSK server negotiated " + localProtocolVersion);
    return localProtocolVersion;
  }
  
  protected TlsEncryptionCredentials getRSAEncryptionCredentials()
    throws IOException
  {
    return TlsTestUtils.loadEncryptionCredentials(context, new String[] { "x509-server.pem", "x509-ca.pem" }, "x509-server-key.pem");
  }
  
  static class MyIdentityManager
    implements TlsPSKIdentityManager
  {
	  private final byte[][] psk;
    public MyIdentityManager(byte[][] psk)
	{
    	this.psk = psk;
	}

	public byte[] getHint()
    {
      return Strings.toUTF8ByteArray("hint");
    }
    
    public byte[] getPSK(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte != null)
      {
    	  return psk[1];
//        String str = Strings.fromUTF8ByteArray(paramArrayOfByte);
//        if (str.equals("client")) {
//          return new byte[16];
//        }
      }
      return null;
    }
  }
}
