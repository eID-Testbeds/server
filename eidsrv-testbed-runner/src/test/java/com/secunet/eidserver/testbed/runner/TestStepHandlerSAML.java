package com.secunet.eidserver.testbed.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPairGenerator;
import java.security.Security;

import javax.xml.crypto.dsig.SignatureMethod;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.saml2.core.AuthnRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;

public class TestStepHandlerSAML
{
	private LogMessageDAO logMessageDAO;

	private URL url;
	private final String samlId = "655801c4-24af-489b-afe7-bfcd9b7f393f";
	private final String ASSERTION = "HTTP/1.1 302 Moved Temporarily" + System.getProperty("line.separator") + "Server: Apache-Coyote/1.1" + System.getProperty("line.separator")
			+ "Content-Security-Policy: default-src 'self'" + System.getProperty("line.separator") + "Cache-Control: no-cache, no-store" + System.getProperty("line.separator") + "Pragma: no-cache"
			+ System.getProperty("line.separator")
			+ "Location: https://e-service.secunet.de/refresh?SAMLResponse=tVjXrtxIkv0V4c4jcUXvLqQe0BdZ9KaK5MuA3ntW0Xz9lNTSdPcsdna7sftEMJLhzomIzOSXv%2B9d%2B%2BmZzUs19F%2Ff4M%2FQ26esT4a06ouvb54rvlNvf%2F%2FlyxJ1LTJ%2B2NkyDv2SfXop9cvHr9Kvb4%2B5%2FxiipVo%2B%2BqjLlo81%2BXAYTf1APkMf4zysQzK0b5%2F4bFmrPlq%2FOyrXdVw%2BQDB7X7L5WSXZ5yVLHn22fk4zcM7yOVvKt08y%2F%2FXtHxgJ03CKwe95SkbvWJbF71ROUO8QRbyWcgIlY%2FL1af8zOHf4%2BpZjEJZiBPGekgTyjuUZ%2BU6RaP6eQAkCpQkS4zj10lmWRyb3yxr169c3BIKJd4h%2BRwgXhj9g4gOFP%2BMwFb59uv2E55XQ2w8wPr4rz79H4j8DES2vTL8l%2F%2FZLJvPv7guNOEs%2FfXsux7Jm3RfwO54%2FDP8Lc2eN1sfyb6%2FckGafblH7yP6z01dqL%2BUP55Ek2bK8gb%2F8cDJ%2B%2FMHsh9An8zGuWcr8jPIvJfZlf9XOb8b4aI1%2B2Pm28CvrL9K3bfu8oZ%2BHuQARCIJBCANfBfX64m8vTtIX5ShMQkSeU2SaYjiOEymBxwiWwElC0QRGIG%2Bf3GN8pf4%2F2hParMv69UXa7yN7kaBlazmkn5i2GOZqLbv%2Fha0oW2CEek%2Fi5O1PJvWCPV0%2Brtkh9%2FnwQzdd%2FhuXEAjR3%2BBIl6r4278FnqUvIz8ginASJ%2FMERhMMyxMoykk4olIkI3KShNP4z8b4f4HQvETv8D%2FwP%2Bv6Bc93drhqLLP5LxTNH%2FS%2Fd8UvjIFjsdYYN%2F8iP%2FFYZ9gibCJt6gbbigAYIdTwhOYWVJ1NTYE7GigHL9K4imi2bdRT47nz04pGSNpz3Nr0h6hP4dlQIRrFmNVIS3NnVRkgh2XASWbmCrYuHh1Uqe4wgLTp34icvu8hiAiad1315HxE1QwNalkEerRpqTXtuD%2FNmormUB7UnaOTeUzTpDDL2AymHd%2FgtKBkyENGOp8N3Xl6Ec02l%2FtU8xBYac%2Bpx%2FUrYF2P8h7pZQGsqsgcgsY5kKfHh5aOWK21xals%2FcbbFHZfceNhqiUek6NV4%2BI5OsG1Uawg6Gr%2FSWgxUzmcw2GEJmUNUXFVWAtBpdtebNCXKnLJFliDS%2FH16xfwd1T9CvUfRN%2FY%2Byn511B5Fe1L9lsL%2FD%2FwDfiRIT1MltcXfVBsN%2Ff9Is%2BXy5FMu9kxLE6nt6GtMJ%2FW2pSQDNcebQVOFd%2BEWyChlwNp9ycfmkZ3XfwJcQ%2FunLcWXtl%2BynRpwK1iMsSYmDW9WrUTv4c4YOKUaiFM2Rs8Kosenc1s8Li5aB71dPe8RsQZHUN%2FJrIW40qjt7eJ9VzIebiweSfK3SXAurgs%2FnHlozsv9eU60b11eJ0O1iVXxpA5XIMDQoR6eXq3yBB5H2U9EUUGPYiw56i6NKAp0vr0ykEKoxvrJGfT0YwnjZqxScthHoiHn2FUZ%2FNxKVp6FR%2B8SjpQXgJVo3LdA3mua2QKDzoqZua%2BMCNC6SbppQcqJFJpDXhi4SuM60ohq4Lh5lMQLtKyKkd0JdPRF8dqF%2FRQtDMGa%2BHYN%2BsicCZwBbQsvm%2BeRgztoc6Ti0MVizeFyEHAXRfdUJXQY2DCm%2BzmhJYhjogwvk%2BYRHHZjWql%2B0Db5adYPY2gXR2Ux%2FXYp8Au5s97PLjO5VnaakASY9zBTiUPSYKTjlQyboAIKY4y6SOiHynUcnBPVWGAsGfCOC7XSmubY0gptEK7cipB6zaWoAVWKFFPEt1ctUu%2BwljXegA7%2BNJkCHOQnKjdL6D5wAcLux31GbbJoyvqTTQjETth6F5uS%2B5dqYsYLUN2QrfmWnNuOVLwc2O86tALOGqCsaqtsSG6y6LcprviWrVEnfbr6BJq1GbSegS7Oz9UjEUgOnUzSLCaQ2R8zs5140C%2FB9JZzzFnkSsmuIY0diVRScuBxKTqa65cdMRGpdmmGndAqMg3T6wa0%2BZsAlUaK5NTDK5NYvG8rxwtMC%2FIqGQb%2BvuhZ0BOODM%2Fh8%2BLo5g4KrY1BpsH%2BAQDQzPsvSdTxZKdmB%2BFQL9oUKeobhgPUwRpQYIq8p2KdBjaJaeuAPo69pddAM6%2BJ%2BsyQhlCAxdA2xHTm2QhwBVQmGNpOgvwzMvDoaoe6OVHWpA3gaWMzCaWO8Q6nFYJ6R0F%2FbxKGgY8FCJu1oUUeSXBHqJxTOnCBvAMVx5usJOr0L7%2BeERaitEA%2B%2BiYXJ%2FlGixek44kZKixR89FQ%2Bfob8uOH6%2BT5bWM11ivO%2BO83VBW66WKrMNR9vRLj84mdiuZ%2BoITkPrQOquT7u297cZF0hpqfjUFG5NmnqHo7KKApdizXiLrEGV9sPStBoVDq8ojQyOxr7Ghf2QV02f6JVp4jwnQMlzkU8nuo4sR2RY6lHZ5kMXM8dbDV5Vsd7ksLa%2BxLOqWVJogssL1%2FXYcDBwTNnBpxBQOoYNcTBNLPL6TTTi1yxwoccJhKFAziSi8FvVU3bMNyxd2ljxvrS978uCvtiWvzuUxbSJmwvBzqfOVlfk7dsYRXvS%2BIZue0PL92BXhLh2tZpJze2iGJDSAbjqK0Fv2nOQZa16RfXXRF5C3fWAD9Ci7HO4MASmDTDQvVpF4BI4aiD89dQavMCaMjWLNqyuAn7vsR0r%2FKEcvjne6vLIA7pu0T4pYxwyC1jPJcrG6RYdlv%2BmXlmM5bqqHVVDJDFIDw0qsgodtF6nyUTcrOoJ5f3FaOblB9xWYASgXVRfXBK6CAvkhACNIakylW9xaoH13XsmgLkIo1cPrnGiJOiWyQIA6VM2uearjmnPixRzEG5U7up6L5MzG9N2O6GHqm5sRB0N4EVIPkK%2BCLZkCdB0ueFHrDXAneFFgTUsoKgTHYba5Z%2Fo5npq9d%2FhIPH0nnzK8Xa4XwrEqfXLcpAd0MGc9nItfHT%2B4jckcWa7dyrrMxwIWheqqKFTyNHdombXeSloo8W8hfaKCX8yoiI6QaaXRZJUMl1MDuiHlJgtNqmgXSdvOluEBxzRAM1VUSkrIp8aojD9RRP7EO%2F1%2BxWx9nVkPupAlckWFfpfdojnn2auiigTLhYbwtTxX%2BxhqRI6Nayu4UfdshRyARDOXWYd3urAso3krwvLYiJXBMW59zkVH7v7GF5HZtRMp7ffat4lZPnIFf2HUln3U3qI4eISi%2FwyIWHA3ttAAerCf%2B7SLCk2CEIPd1elccHzcVWaeuucFp7iN8S%2Fi%2BAp34i3Ox7a6bPcbVPDXVHaQSx8Uy23NKctzpVy9imPmxRdE6EPuCsbD032qKO5v%2FaoHQdM6viPNrBh3Lpinbp0YQNSqUUnLM9534ohXhV5VJOK%2BRr55oE6DH7YxE85CaLdpzh6G6XaHYzGktHBpYK6S1O5rXUhUyLb7tmjsnBWbizSJItSjVk7Cgvu1kvLexNbCnXzkPEBuYFLlWKQk8MpktMlJPKQAT5vvrvJGXGt0ploamfQW6e%2Bmhw23bdUEBgL6CB4AMXABfVJIMu1323HAmy7Ghr7sfNl2ss9Qu02ynHLYKfm00vgkWUQM1CsAUzv02g0tuaQwiW5YeVLhcwvLjk6qBrqFwDL06AWoX%2FNqNM8NBaFbVdjDggVzdat5PHNYbeGxkDJdDwV5fUJAGHOJIWERyW0UG17nIYhmvWEjEvSqRK3vy%2Bw7z0es7rgd2XbbjOu0ZCIPE6aRw69duCFtDsS3Ju6JpXAeRtS%2F6rfkl%2FMx9tTKBV5UoSnk%2BC6KOylNTkiL3ktB2reGkOHIDA0d4Je7iKngUD%2Bft8dEfhv5zeKx0zbl7CNrWeQCOQeV3ArHkyC5AJ26I0RyjF9banJPb3CiA7I1l7tzXbD2dqM5gphP8zqWVuSD977l58HceTwOgPCS%2BXZTNfmZsqHx5If9ta3bIqF6sHibYUrmsDbCVA6%2FXNDGN%2BK%2FdC7%2BcVj%2B9dr%2FX6%2Fgv13Wf%2F7a%2BOWf&RelayState=f404d466-d762-4fe7-873f-c0c20dc2b558&SigAlg=http%3A%2F%2Fwww.w3.org%2F2001%2F04%2Fxmldsig-more%23rsa-sha256&Signature=HjdF1UDQXzW8yXfEWl6g7VSDxTJ7PLcK90jV%2B4DLKiomR9asE0WGgjm283Toz7vUBer8LgDgzIQSAKDESzqqQXd1QJyubCV9RH9OruxpzhZKqfGjA2pv88hmoyPRpIWCPd4O0jxhVRxMYtavXgQgFI9FRsGOuFcdqmD4LiYBJM54thffXVZ%2BDS5Hadxb0ySCyBrAN1gIfvj%2BDZMZqwK86qLbg%2BRr2X3OdZF7Ol5M6zUYS1L4c%2Bn23YG14oWQ71605YRXEcAVfr6AHCzMH2bdV85FPmvQ2G1BM4KNCsj1xzkfKhAY6czM3fyTmKjOhPiA8i5Krl1g8MmY3j3WO4j8Vw%3D%3D"
			+ System.getProperty("line.separator") + "Content-Length: 0" + System.getProperty("line.separator") + "Date: Tue, 02 Jun 2015 15:32:52 GMT";
	private final String ASSERTION_AGE = "HTTP/1.1 302 Moved Temporarily" + System.getProperty("line.separator") + "Server: Apache-Coyote/1.1" + System.getProperty("line.separator")
			+ "Content-Security-Policy: default-src 'self'" + System.getProperty("line.separator") + "Cache-Control: no-cache, no-store" + System.getProperty("line.separator") + "Pragma: no-cache"
			+ System.getProperty("line.separator")
			+ "Location: https://e-service.secunet.de/refresh?SAMLResponse=tVfXsqRIlvyVsjuP2C20ulZVY8iEJEmBSBJexhCB1hq%2BfqjuqunuWdvZ7bbdJ4yAo9w9TsT58ve1Kj%2FNoB%2Bypv76hn5G3j6BOmyirE6%2BvtmW%2FM68%2Ff3bl8GvSqz9MMDQNvUAPh1G9fDx6%2BrXt6mvPxp%2FyIaP2q%2FA8DGGHyanXz6wz8hH2zdjEzbl2ycRDGNW%2B%2BMvgdJxbIcPGAbvA%2BjnLASfBxBONRg%2FRwDuQdyDIX37pIpf3%2F4RokTghzjzjhAB9k74JPnuAyx8D0gsJPHQJyOUOX6tfyZnNV%2FfYoAHVIxG7xQVh%2B8EBfx3xg%2FRdxwhfCogiSjw%2FcNmGCag1sPo1%2BPXNwxBqXeEfccoC0U%2FMOoDZT%2FTGOW9fXr%2BhOco6O0HGB%2B%2FGPe%2FR%2BI%2FA%2BEPR6Xfi3%2F7BlTx3TrQCED06ftz2IYRVF%2FgX%2FD84fhfmJujP07Dv70KTQQ%2BPf1yAv856FHaYfxhTmEIhuEN%2FvYjSPvxB7cfUh32WzuCiPuZ5V8q7Mt6aOc3Z6I%2F%2Bj%2F8fP%2FwK%2BsH6cuyfF7wz02fwBiCoDBCwIegjj%2F%2BdnASHZSTLBWyKBWzPhHHAQpwNmaCEDAxoPwAww%2B6ra09Sv8f%2FUklqEA9HqT9PrODBB2MaRN94sqk6bMxrf4XvnwwoBjzHgbh258s6oA9Gj40sKl13PywjYb%2FJiQCI%2Bx3OKIhS%2F72b4mD6HDyK0QMgTE0HQdhQMQkiYQoS4QBEmGEz5LogdufzfH%2FAqF%2B8N%2FRf5B%2FNvQBzy%2FsCFmbgv4viOYP9r%2Fsim8GFOwyfoapnCFbpRSToWApQF70%2FaxiS95ikI2Xs7SbGzUSqkqCV7ve5wfTrNe4wwmQky%2B5frnC4EtT3S6vcAtsuMMgnPONcnsJk5RzXTplF6zP1FQ79Cm09kZyaxrbsoGBecsB0KiQ6y5WPFD2MkMJySvVc6SJVXMCl3zVzvYIL%2BrSakCfWgZjg%2FN9jsnRPp3ylwoH9O5BztwMDYVhSeOc%2Bw0CShBcR2tFrgk6yWdnCVtExtgn5wroyGpWKcnWCsLauGuutD2wnjXMbh0CnzRTnYGJ61njKoDe8pQPjWBwJwG%2FSQLrz74PVH7A%2BYEt7%2Flji0vlFZRAYh62ZkisKExFjITb8vXrF%2Fh3VP0K9R%2BWvrP3c%2BVfTeUQ7bH22xb4f%2BBb0ER99lu50M5A0Qqj99rxTs6PDXWE6JL36banyil1TV%2Fe5fh685TSud0YGqDSnPa2FZ2wXlGGUXJsBBoTDtwEG6KMsrrtrfWQ1peNAGhN96m11Dswn03Au8ro34EMBzUdzi4PGwmlp7X1wB1T4V%2B7hHv6q7BmWi85p7adxCVvFedKq2dI1C5zaglJSYcvtE4oBoe2XvNCMQJKsEFbbVnkbyd42hq7vzuGz9wKDnvlOq1ybmKeo6yoGHiVExzfzY64Ka2tFK7asZQHqSyAHSS19SlprOV%2BGer19rA2TIYSLiTTqb0oBN42BNnTNkH7XTgHTV%2Fo3Q1NRwS2buu8wXNsOQZFkvA9qZqnqeWrwYX8fVkSh%2FXrbmImajTbMTtd8S4nTaJ1wcPqJti5mR48pNB0Cij46SsaZE0wTxJI5YXlY%2B%2FwOikC6vZCB6C6HISdkTDJRyu9rUwmkN3FUxjbiBpLQtPb7AO9NPrTlNeTeZGdVIeJDVY7hgzmwo2eU7cNVY5m237FCEblu5y5P%2Fk7%2B9RDzfFINlgUrUHJhplr0Z4awIZpgvulehcITqi1AHMGwrUwFNsoT1ZeqoedOqXwhwQ3ebFvc8oDtXvOff%2BSDnCw9eXTe5VT0TJkI04WMNH%2BYUolT9oBArDHS%2FPWcUViC2XqgM%2FDRhSY1sX8glxA0SHjvHb45TF5sppH83TIo2qH8R5aOzGp9xla233KkKDmDh3KLDa5AzYLBDYd%2FWDhpHiq16iouLP7wKFz6SPT1MWVY%2BZLwZX1Vi8pvTwJrhJIJXyVy3iOzk03nwDpYhRrI7d2A0htp0zZwraRwLiO7p6K3aD5wUs8fbP4KMO9bNtwGfjzUNGQdi3GR%2FhQCkoY8Jk8tgHzGGP7OSIAzRBZUQd6sxJ56iv%2FhBoWRY0XORQnLUEeNB%2FTlSLCF37ekZitNTxeGSTQzobGyTeoPyVdUsp5Jqsa1XJbwmflslwI%2FA5N23a%2FyFXrkmlwJuvwxS%2FhA44Stl1qXH4pl%2FuDgEZ6RFWLD9B%2BrS9XYZ7VhdtfguVCzuuZ9aqZkDHPQq7EauJcwAtRiWZVPvVnkhrWQj2MQS9TT98EzDftsklEIrIssGW866kkOsBbkt2%2BS29bWVu8a13EitSMLficrqfJLXk6PtmyG5A6huIO%2FXK95U6g8XJK%2BTES%2B4sBmGme1I7HwI4b9LwlsauqZUNfEdbNn%2FeXRdVP6K6w%2By2JjhuIiQYG%2BSo4GFbV7jhj4Cp6tQHkRiFdRjHX2ZQLMy3N7wWRva609%2BzGNvF9q%2FJfpWRiDEWZOv56RdeYUiFvd0rJC2HoEKcv4oFt%2Bw19etSY5t9JiCEz6y44ImOqwWRnHNLPVeTlAUzJMKmosNWoAkwrIT7sgk7A%2FjApGeKc0O0c42Dczs7kNCtZxJeIwkKXl7aQlbFV9BtXXIixseKrA4EIbSDo7MDibrcyhXadyTTx2jwZKcKAqAXGszxVcCmLNzKHVf6JBIUZTajqeWKF937u3G%2BJkBlHB3gYYsUpw927cvEGR6CrugQzzsQ5nr07NU6PHJmUgM94ibgNLbnbRSxeWZR8%2BRQp5jE8ukT0pE3sEbRFybEhYwerMnIXS4mDZmrNB3SOS1x0fTAyo7EM7fqCopGO%2BfuLbnr4rGtr4MYjwghlFott2hztwBodVb9s8jUuxOTokj4WVxWETKfFzTO3BXp0sy3NOngHh1rtUO2o9ArWeOIf5qldATYH5wgmardpPCO9P9F6jDoekuSCeE6N2g%2BMvy%2Bk3a%2BGW57B1MQyFM6mRQG31aaQMwlZF6AHdhIGQpYxK%2FYYES7MxnbX1knyvLggKe106e0ZznaQhVJvOwiU2LfadS%2BYkTmdJUawWHfiXfZsL3NW4aogM1LwDz0LZuPuKLNk7b1goqKOn8vz9QxzqNAI59atpkupmz5o60m2OmnJ7ONcYa4e59uesK3xY4MGKC97XtwoDZcV4uZs0nZKVOOceXndyxt302o2e3qCLlhpmdubflEGunJfGiCbhYrPe%2BtabTK4RufUm6V6tM01xriHizuiPo3dJtpAZcGliU20L6jAoEx2eXBeIDtZHil%2Bu%2BY1%2B6QLkQVpCkU8MbczSgknhqt2d%2FeDcChRUkW2y%2FMs4Q16XSGMAOMT1HTyNEXdy%2Bj9sld2oAfM5TIxnK5Plndrj%2BF1OWvxVbIcM4kUdrwPLpbe9pOrXKJWW1FILEbfXoFWwv0ejOxFv%2FYxY%2BVLMuxSNVQ1BkG36eSQA%2B1ODzs%2BH621gFBC7WbYLMRGS3PCyBhp9y5FyrE4THRIrsNMBgq8eMSnreyk45rZK1dRylpi1E8nyJN3tM4dJIYrVa5uV1jIR88r2EF4hVehV0ZBu6rIle0ee7pTYJkaHdT6WV%2BpWsoeJ9xIpAuF84ksmY4X1uhac4xHnbc8BOf6xam6QfK8rAqPv3jf%2B3EJ%2FHWc%2Fa%2Bj5W9D6M%2BR%2Fds%2FAQ%3D%3D&RelayState=fe3b6f1d-66fc-46ea-8ac1-304a6b54dbaa&SigAlg=http%3A%2F%2Fwww.w3.org%2F2001%2F04%2Fxmldsig-more%23rsa-sha256&Signature=oiMsq0t%2F33iM95N6tuNYQ1ciWWITO7jsyMQM7denHXOhG6yq3ppq4UXmzEWDGojiW7VhnQDJ%2BWHzFZnuxXNrRzjJixV2HWaTRP3cQa8M1EwHdwkIU1C7y%2FqJnzwetZ2vzhrEeBg6e97pvfcAM%2FauPm6ZadsXDDFh%2BSrL4kC88z65mp6vghspmKIvnq4xCxMZyHUl%2B58JRxlrLbMNqnzGrbjBkl08KE1Wi6u2Y%2BusYfJa9dKieEgakEQXu9DaALXe4YUW8UzULHKHkftELr7NV60ZwYW8WMBwgdPpGmlhRTrY%2BwncaBEszoS7jZCQ25ENkuUbRsQ5%2FFRm9DD5F5f3RA%3D%3D"
			+ System.getProperty("line.separator") + "Content-Length: 0" + System.getProperty("line.separator") + "Date: Tue, 02 Jun 2015 15:32:52 GMT";


	@BeforeClass
	private void init()
	{
		Security.addProvider(new BouncyCastleProvider());

		// mocks
		logMessageDAO = mock(LogMessageDAO.class);

		// empty POJOs
		LogMessage emptyLogMessage = new LogMessageTestingPojo();
		when(logMessageDAO.createNew()).thenReturn(emptyLogMessage);

		// url
		try
		{
			url = new URL("https://test.de:12345/path");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This tests checks the correct validation of a received SAML assertion.
	 * <p>
	 * <i>WARNING</i> This test will break if the certificates used by the runner are changed. In this case, the assertion has to be updated
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true)
	public void testAssertionDecode() throws Exception
	{
		// read the certificates
		KnownValues values = new KnownValues();
		StepHandlerSAML samlHandler = new StepHandlerSAML("testAssertionDecode", url, GeneralConstants.TESTBED_REFRESH_URL, url, null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// add known values needed for comparison
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.REFRESHADDRESS.toString(), GeneralConstants.TESTBED_REFRESH_URL));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.SAML_ID.toString(), samlId));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + RequestAttribute.AcademicTitle.toString(), GeneralConstants.PERMISSION_ALLOWED));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.PERSONAL_PREFIX + RequestAttribute.AcademicTitle.toString(), "DR."));
		LogMessage result = samlHandler.validateAssertion(ASSERTION, TestStepType.IN_SAML_ASSERTION.value());
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getSuccess());
	}

	/**
	 * This tests checks the correct validation of a received SAML assertion including age verification.
	 * <p>
	 * <i>WARNING</i> This test will break if the certificates used by the runner are changed. In this case, the assertion has to be updated
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true)
	public void testAssertionAgeVerification() throws Exception
	{
		// read the certificates
		KnownValues values = new KnownValues();
		StepHandlerSAML samlHandler = new StepHandlerSAML("testAssertionAgeAndPlaceVerification", url, GeneralConstants.TESTBED_REFRESH_URL, url, null, values, logMessageDAO, EService.A,
				EidCard.EIDCARD_1, 0);
		// add known values needed for comparison
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.REFRESHADDRESS.toString(), GeneralConstants.TESTBED_REFRESH_URL));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.SAML_ID.toString(), samlId));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.PERSONAL_PREFIX + SpecialFunction.AgeVerification.toString(), "true"));
		samlHandler.fillDataToRequest();
		LogMessage result = samlHandler.validateAssertion(ASSERTION_AGE, TestStepType.IN_SAML_ASSERTION.value());
		Assert.assertNotNull(result);
		// Assert.assertTrue(result.getSuccess());
	}


	/**
	 * This tests checks the correct validation of a received SAML assertion, where the document check was expected to fail.
	 * <p>
	 * <i>WARNING</i> This test will break if the certificates used by the runner are changed. In this case, the assertion has to be updated
	 * 
	 * @throws Exception
	 */
	@Test(enabled = false)
	public void testAssertionNegativeResult() throws Exception
	{
		// read the certificates
		KnownValues values = new KnownValues();
		StepHandlerSAML samlHandler = new StepHandlerSAML("testAssertionNegativeResult", url, GeneralConstants.TESTBED_REFRESH_URL, url, null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// add known values needed for comparison
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.REFRESHADDRESS.toString(), GeneralConstants.TESTBED_REFRESH_URL));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.SAML_ID.toString(), samlId));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.DOCUMENT_VALIDITY.toString(), "invalid"));
		LogMessage result = samlHandler.validateAssertion(ASSERTION, TestStepType.IN_SAML_ASSERTION.value());
		Assert.assertNotNull(result);
	}

	@Test(enabled = true)
	public void testCreateRequestWithAgeAndPlaceVerification() throws Exception
	{
		KnownValues values = new KnownValues();
		StepHandlerSAML samlHandler = new StepHandlerSAML("testCreateRequest", url, GeneralConstants.TESTBED_REFRESH_URL, url, null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// add known values needed for comparison
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.REFRESHADDRESS.toString(), GeneralConstants.TESTBED_REFRESH_URL));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.SAML_ID.toString(), samlId));
		samlHandler.getKnownValues().add(new KnownValue(Replaceable.DOCUMENT_VALIDITY.toString(), "invalid"));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX + SpecialFunction.PlaceVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.REQUESTED_PREFIX + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_REQUIRED));
		samlHandler.getKnownValues().add(new KnownValue(GeneralConstants.REQUESTED_PREFIX + SpecialFunction.PlaceVerification.toString(), GeneralConstants.PERMISSION_REQUIRED));

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
		AuthnRequest result = samlHandler.buildAuthRequest(keyGen.generateKeyPair().getPublic());

		Assert.assertNotNull(result);
	}

	@Test(enabled = true)
	public void testCreateSAMLrequestSimple() throws Exception
	{
		String request = callCreateSAMLrequest(null, EService.A);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}


	@Test(enabled = true)
	public void testCreateSAMLrequestRSA() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "RSA"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, SignatureMethod.RSA_SHA1));
		String request = callCreateSAMLrequest(values, EService.ERSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}


	@Test(enabled = true)
	public void testCreateSAMLrequestDSA() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "DSA"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, SignatureMethod.DSA_SHA1));
		String request = callCreateSAMLrequest(values, EService.EDSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}

	/**
	 * @see https://www.iana.org/assignments/xml-security-uris/xml-security-uris.xhtml
	 */
	@Test(enabled = true)
	public void testCreateSAMLrequestECDSA() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "ECDSA"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"));
		String request = callCreateSAMLrequest(values, EService.EECDSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}

	@Test(enabled = true)
	public void testCreateSAMLrequestRSAManipulated() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "RSA_MANIPULATED"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, SignatureMethod.RSA_SHA1));
		String request = callCreateSAMLrequest(values, EService.ERSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}


	@Test(enabled = true)
	public void testCreateSAMLrequestDSAManipulated() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "DSA_MANIPULATED"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, SignatureMethod.DSA_SHA1));
		String request = callCreateSAMLrequest(values, EService.EDSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}


	/**
	 * @see https://www.iana.org/assignments/xml-security-uris/xml-security-uris.xhtml
	 */
	@Test(enabled = true)
	public void testCreateSAMLrequestECDSAManipulated() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "ECDSA_MANIPULATED"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"));
		String request = callCreateSAMLrequest(values, EService.EECDSA);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertTrue(request.contains("Signature="));
	}


	@Test(enabled = true)
	public void testCreateSAMLrequestNoSignature() throws Exception
	{
		String message = "GET [SAML_PROCESSOR_PATH][[CREATE_SAML_NO_SIGNATURE]] HTTP/1.1 Host: [SAML_PROCESSOR_HOSTNAME] User-Agent: [USER_AGENT] Content-Type: text/xml;charset=utf-8";
		String request = callCreateSAMLrequest(null, message, EService.A);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertTrue(request.contains("SigAlg="));
		assertFalse(request.contains("Signature="));
	}


	@Test(enabled = true)
	public void testCreateSAMLrequestNoSigAlgNoSignature() throws Exception
	{
		String message = "GET [SAML_PROCESSOR_PATH][[CREATE_SAML_NO_SIGALG_NO_SIGNATURE]] HTTP/1.1 Host: [SAML_PROCESSOR_HOSTNAME] User-Agent: [USER_AGENT] Content-Type: text/xml;charset=utf-8";
		String request = callCreateSAMLrequest(null, message, EService.A);
		assertNotNull(request);
		assertTrue(request.contains("SAMLRequest="));
		assertFalse(request.contains("SigAlg="));
		assertFalse(request.contains("Signature="));
	}


	// --------------------------
	// ----- Helper methods -----
	// --------------------------
	/** Helper method */
	private String callCreateSAMLrequest(KnownValues values, EService service) throws Exception
	{
		String message = "GET [SAML_PROCESSOR_PATH][[CREATE_SAML]] HTTP/1.1 Host: [SAML_PROCESSOR_HOSTNAME] User-Agent: [USER_AGENT] Content-Type: text/xml;charset=utf-8";
		return callCreateSAMLrequest(values, message, service);
	}


	/** Helper method */
	private String callCreateSAMLrequest(KnownValues values, String message, EService service) throws Exception
	{
		StepHandlerSAML shs = new StepHandlerSAML("testCreateSAMLrequest", url, GeneralConstants.TESTBED_REFRESH_URL, url, values, logMessageDAO, service, EidCard.EIDCARD_1, 0);
		String request = shs.createSAMLrequest(message);
		// assertNotNull(request);
		return request;
	}


	/** Helper method */
	@SuppressWarnings("unused")
	private String callCreateSAMLrequest(CryptoHelper.Algorithm algorithm, IcsXmlsecSignatureUri uri) throws Exception
	{
		String message = "GET [SAML_PROCESSOR_PATH][[CREATE_SAML]] HTTP/1.1 Host: [SAML_PROCESSOR_HOSTNAME] User-Agent: [USER_AGENT] Content-Type: text/xml;charset=utf-8";
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, algorithm.getAlgorithmName()));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, uri.value()));
		StepHandlerSAML shs = new StepHandlerSAML("testCreateSAMLrequest", url, GeneralConstants.TESTBED_REFRESH_URL, url, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		String request = shs.createSAMLrequest(message);
		// assertNotNull(request);
		return request;
	}

}
