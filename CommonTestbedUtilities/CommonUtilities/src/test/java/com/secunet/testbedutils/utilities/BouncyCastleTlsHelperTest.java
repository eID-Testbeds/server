package com.secunet.testbedutils.utilities;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;




import java.util.LinkedList;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.testng.annotations.Test;

public class BouncyCastleTlsHelperTest {
    
    @Test
    public void testConvertCipherSuiteStringToInt() {
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_RSA_WITH_AES_128_CBC_SHA256"), 0x003C);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_RSA_WITH_AES_256_CBC_SHA256"), 0x003D);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_RSA_WITH_AES_128_GCM_SHA256"), 0x009C);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_RSA_WITH_AES_256_GCM_SHA384"), 0x009D);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA"), 0xC004);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA"), 0xC005);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA"), 0xC009);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA"), 0xC00A);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA"), 0xC00E);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA"), 0xC00F);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"), 0xC013);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA"), 0xC014);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_DSS_WITH_AES_128_CBC_SHA256"), 0x003E);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_RSA_WITH_AES_128_CBC_SHA256"), 0x003F);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"), 0x0040);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"), 0x0067);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_DSS_WITH_AES_256_CBC_SHA256"), 0x0068);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_RSA_WITH_AES_256_CBC_SHA256"), 0x0069);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"), 0x006A);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256"), 0x006B);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256"), 0x009E);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384"), 0x009F);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_RSA_WITH_AES_128_GCM_SHA256"), 0x00A0);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_RSA_WITH_AES_256_GCM_SHA384"), 0x00A1);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256"), 0x00A2);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384"), 0x00A3);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_DSS_WITH_AES_128_GCM_SHA256"), 0x00A4);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_DH_DSS_WITH_AES_256_GCM_SHA384"), 0x00A5);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"), 0xC023);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384"), 0xC024);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256"), 0xC025);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384"), 0xC026);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"), 0xC027);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"), 0xC028);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256"), 0xC029);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384"), 0xC02A);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"), 0xC02B);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"), 0xC02C);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256"), 0xC02D);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384"), 0xC02E);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"), 0xC02F);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"), 0xC030);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256"), 0xC031);
        assertEquals(BouncyCastleTlsHelper.convertCipherSuiteStringToInt("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"), 0xC032);
    }
    
    @Test
    public void testConvertCipherSuiteIntToString() {
        assertEquals("TLS_RSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x003C));
        assertEquals("TLS_RSA_WITH_AES_256_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x003D));
        assertEquals("TLS_RSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x009C));
        assertEquals("TLS_RSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x009D));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC004));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC005));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC009));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC00A));
        assertEquals("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC00E));
        assertEquals("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC00F));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC013));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC014));
        assertEquals("TLS_DH_DSS_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x003E));
        assertEquals("TLS_DH_RSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x003F));
        assertEquals("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x0040));
        assertEquals("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x0067));
        assertEquals("TLS_DH_DSS_WITH_AES_256_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x0068));
        assertEquals("TLS_DH_RSA_WITH_AES_256_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x0069));
        assertEquals("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x006A));
        assertEquals("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x006B));
        assertEquals("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x009E));
        assertEquals("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x009F));
        assertEquals("TLS_DH_RSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A0));
        assertEquals("TLS_DH_RSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A1));
        assertEquals("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A2));
        assertEquals("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A3));
        assertEquals("TLS_DH_DSS_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A4));
        assertEquals("TLS_DH_DSS_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0x00A5));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC023));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC024));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC025));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC026));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC027));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC028));
        assertEquals("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC029));
        assertEquals("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02A));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02B));
        assertEquals("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02C));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02D));
        assertEquals("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02E));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC02F));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC030));
        assertEquals("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC031));
        assertEquals("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", BouncyCastleTlsHelper.convertCipherSuiteIntToString(0xC032));
    }
    
    @Test
    public void testConvertNamedCurveStringToInt() {
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect163k1"), 1);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect163r1"), 2);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect163r2"), 3);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect193r1"), 4);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect193r2"), 5);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect233k1"), 6);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect233r1"), 7);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect239k1"), 8);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect283k1"), 9);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect283r1"), 10);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect409k1"), 11);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect409r1"), 12);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect571k1"), 13);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("sect571r1"), 14);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp160k1"), 15);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp160r1"), 16);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp160r2"), 17);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp192k1"), 18);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp192r1"), 19);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp224k1"), 20);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp224r1"), 21);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp256k1"), 22);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp256r1"), 23);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp384r1"), 24);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("secp521r1"), 25);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("brainpoolP256r1"), 26);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("brainpoolP384r1"), 27);
        assertEquals(BouncyCastleTlsHelper.convertNamedCurveStringToInt("brainpoolP512r1"), 28);
    }
    
    @Test
    public void testConvertNamedCurveIntToString() {
        assertEquals("sect163k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(1));
        assertEquals("sect163r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(2));
        assertEquals("sect163r2", BouncyCastleTlsHelper.convertNamedCurveIntToString(3));
        assertEquals("sect193r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(4));
        assertEquals("sect193r2", BouncyCastleTlsHelper.convertNamedCurveIntToString(5));
        assertEquals("sect233k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(6));
        assertEquals("sect233r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(7));
        assertEquals("sect239k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(8));
        assertEquals("sect283k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(9));
        assertEquals("sect283r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(10));
        assertEquals("sect409k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(11));
        assertEquals("sect409r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(12));
        assertEquals("sect571k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(13));
        assertEquals("sect571r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(14));
        assertEquals("secp160k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(15));
        assertEquals("secp160r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(16));
        assertEquals("secp160r2", BouncyCastleTlsHelper.convertNamedCurveIntToString(17));
        assertEquals("secp192k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(18));
        assertEquals("secp192r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(19));
        assertEquals("secp224k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(20));
        assertEquals("secp224r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(21));
        assertEquals("secp256k1", BouncyCastleTlsHelper.convertNamedCurveIntToString(22));
        assertEquals("secp256r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(23));
        assertEquals("secp384r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(24));
        assertEquals("secp521r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(25));
        assertEquals("brainpoolP256r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(26));
        assertEquals("brainpoolP384r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(27));
        assertEquals("brainpoolP512r1", BouncyCastleTlsHelper.convertNamedCurveIntToString(28));
    }
    
    @Test
    public void testGetAllNamedCurveStrings() {
        LinkedList<String> allNamedCurves = BouncyCastleTlsHelper.getAllNamedCurveStrings();
        assertEquals(allNamedCurves.get(0), "sect163k1");
        assertEquals(allNamedCurves.get(1), "sect163r1");
        assertEquals(allNamedCurves.get(2), "sect163r2");
        assertEquals(allNamedCurves.get(3), "sect193r1");
        assertEquals(allNamedCurves.get(4), "sect193r2");
        assertEquals(allNamedCurves.get(5), "sect233k1");
        assertEquals(allNamedCurves.get(6), "sect233r1");
        assertEquals(allNamedCurves.get(7), "sect239k1");
        assertEquals(allNamedCurves.get(8), "sect283k1");
        assertEquals(allNamedCurves.get(9), "sect283r1");
        assertEquals(allNamedCurves.get(10), "sect409k1");
        assertEquals(allNamedCurves.get(11), "sect409r1");
        assertEquals(allNamedCurves.get(12), "sect571k1");
        assertEquals(allNamedCurves.get(13), "sect571r1");
        assertEquals(allNamedCurves.get(14), "secp160k1");
        assertEquals(allNamedCurves.get(15), "secp160r1");
        assertEquals(allNamedCurves.get(16), "secp160r2");
        assertEquals(allNamedCurves.get(17), "secp192k1");
        assertEquals(allNamedCurves.get(18), "secp192r1");
        assertEquals(allNamedCurves.get(19), "secp224k1");
        assertEquals(allNamedCurves.get(20), "secp224r1");
        assertEquals(allNamedCurves.get(21), "secp256k1");
        assertEquals(allNamedCurves.get(22), "secp256r1");
        assertEquals(allNamedCurves.get(23), "secp384r1");
        assertEquals(allNamedCurves.get(24), "secp521r1");
        assertEquals(allNamedCurves.get(25), "brainpoolP256r1");
        assertEquals(allNamedCurves.get(26), "brainpoolP384r1");
        assertEquals(allNamedCurves.get(27), "brainpoolP512r1");
    }
    
    @Test
    public void testConvertSignatureAndHashAlgorithmStringToClass() {
        SignatureAndHashAlgorithm saha = null;
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("MD5withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.md5);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("MD5withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.md5);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("MD5withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.md5);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
    
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA1withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha1);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA1withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha1);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA1withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha1);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
    
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA224withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha224);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA224withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha224);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA224withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha224);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
    
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA256withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha256);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA256withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha256);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA256withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha256);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
    
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA384withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha384);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA384withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha384);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA384withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha384);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
                
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA512withRSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha512);
        assertEquals(saha.getSignature(), SignatureAlgorithm.rsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA512withDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha512);
        assertEquals(saha.getSignature(), SignatureAlgorithm.dsa);
        saha = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass("SHA512withECDSA");
        assertNotNull(saha);
        assertEquals(saha.getHash(), HashAlgorithm.sha512);
        assertEquals(saha.getSignature(), SignatureAlgorithm.ecdsa);
    }
    
    @Test
    public void testConvertDHParametersObjectToDHStandardGroupsString() {
        assertEquals("rfc2409_768", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc2409_768));
        assertEquals("rfc2409_1024", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc2409_1024));
        assertEquals("rfc3526_1536", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_1536));
        assertEquals("rfc3526_2048", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_2048));
        assertEquals("rfc3526_3072", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_3072));
        assertEquals("rfc3526_4096", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_4096));
        assertEquals("rfc3526_6144", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_6144));
        assertEquals("rfc3526_8192", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc3526_8192));
        // rfc4306_768 is equal to rfc2409_768
        assertEquals("rfc2409_768", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc4306_768));
        // rfc4306_1024 is equal to rfc2409_1024
        assertEquals("rfc2409_1024", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc4306_1024));
        assertEquals("rfc5114_1024_160", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc5114_1024_160));
        assertEquals("rfc5114_2048_224", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc5114_2048_224));
        assertEquals("rfc5114_2048_256", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc5114_2048_256));
        // rfc4306_768 is equal to rfc2409_768
        assertEquals("rfc2409_768", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc5996_768));
        // rfc4306_1024 is equal to rfc2409_1024
        assertEquals("rfc2409_1024", BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(DHStandardGroups.rfc5996_1024));
    }
    
    @Test
    public void testConvertDHStandardGroupsStringToDHParametersObject() {
        assertEquals(DHStandardGroups.rfc2409_768, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc2409_768"));
        assertEquals(DHStandardGroups.rfc2409_1024, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc2409_1024"));
        assertEquals(DHStandardGroups.rfc3526_1536, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_1536"));
        assertEquals(DHStandardGroups.rfc3526_2048, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_2048"));
        assertEquals(DHStandardGroups.rfc3526_3072, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_3072"));
        assertEquals(DHStandardGroups.rfc3526_4096, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_4096"));
        assertEquals(DHStandardGroups.rfc3526_6144, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_6144"));
        assertEquals(DHStandardGroups.rfc3526_8192, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc3526_8192"));
        // rfc4306_768 is equal to rfc2409_768
        assertEquals(DHStandardGroups.rfc2409_768, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc4306_768"));
        // rfc4306_1024 is equal to rfc2409_1024
        assertEquals(DHStandardGroups.rfc2409_1024, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc4306_1024"));
        assertEquals(DHStandardGroups.rfc5114_1024_160, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc5114_1024_160"));
        assertEquals(DHStandardGroups.rfc5114_2048_224, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc5114_2048_224"));
        assertEquals(DHStandardGroups.rfc5114_2048_256, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc5114_2048_256"));
        // rfc4306_768 is equal to rfc2409_768
        assertEquals(DHStandardGroups.rfc2409_768, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc5996_768"));
        // rfc4306_1024 is equal to rfc2409_1024
        assertEquals(DHStandardGroups.rfc2409_1024, BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject("rfc5996_1024"));
    }
    
    @Test
    public void testConvertProtocolVersionStringToObject() {
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("SSLv3"), ProtocolVersion.SSLv3);
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("TLSv10"), ProtocolVersion.TLSv10);
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("TLSv11"), ProtocolVersion.TLSv11);
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("TLSv12"), ProtocolVersion.TLSv12);
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("DTLSv10"), ProtocolVersion.DTLSv10);
        assertEquals(BouncyCastleTlsHelper.convertProtocolVersionStringToObject("DTLSv12"), ProtocolVersion.DTLSv12);
    }
}
