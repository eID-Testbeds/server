package com.secunet.eidserver.testbed.runner;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestStepHandlerSessionCookie
{
	private static final String STATUS = "HTTP/1.1 200 OK\r\n";
	private static final String BODY = "\r\n\r\n<html></html>";

	@Test
	public void testHexJSessionId()
	{
		String message = STATUS + "Set-Cookie: JSESSIONID=0AF3B2; Path=/; Secure; HttpOnly" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "JSESSIONID=0AF3B2");
	}

	@Test
	public void testOpaqueSpringSessionValueKeepsPadding()
	{
		String message = STATUS + "Set-Cookie: SESSION=ZjM2Yy1hYmM=; Path=/; HttpOnly; SameSite=Lax" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "SESSION=ZjM2Yy1hYmM=");
	}

	@Test
	public void testSessionCookieIsPreferredOverEarlierCookie()
	{
		String message = STATUS + "Set-Cookie: lang=de; Path=/\r\nSet-Cookie: SESSION=abc123; Path=/" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "SESSION=abc123");
	}

	@Test
	public void testJSessionIdIsPreferredOverEarlierCookie()
	{
		String message = STATUS + "Set-Cookie: lang=de\r\nSet-Cookie: JSESSIONID=0AF3B2" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "JSESSIONID=0AF3B2");
	}

	@Test
	public void testFirstCookieIsUsedWithoutKnownSessionName()
	{
		String message = STATUS + "Set-Cookie: custom=one\r\nSet-Cookie: other=two" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "custom=one");
	}

	@Test
	public void testHeaderNameIsCaseInsensitive()
	{
		String message = STATUS + "set-cookie: SESSION=abc123" + BODY;
		Assert.assertEquals(StepHandler.parseSessionCookie(message), "SESSION=abc123");
	}

	@Test
	public void testNoCookie()
	{
		Assert.assertNull(StepHandler.parseSessionCookie(STATUS + "Content-Type: text/html" + BODY));
	}

	@Test
	public void testTextInsideBodyLineIsIgnored()
	{
		String message = STATUS + "Content-Type: text/html\r\n\r\n<p>Set-Cookie: SESSION=abc123</p>";
		Assert.assertNull(StepHandler.parseSessionCookie(message));
	}
}
