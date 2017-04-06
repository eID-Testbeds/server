// package com.secunet.eidserver.testbed.web.domain.test;
//
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Random;
// import java.util.Set;
//
// import com.secunet.eidserver.testbed.common.constants.Bitlengths;
// import com.secunet.eidserver.testbed.common.ics.IcsCa;
// import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
// import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
// import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
// import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
// import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
// import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
// import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
// import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
// import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
// import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
// import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
// import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
//
// public class TestCandidateMother
// {
// static Random r = new Random();
//
// public static TestCandidate generateDemoCandidate()
// {
// return generateDemoCandidate(nextInt(100));
// }
//
// static TestCandidate generateDemoCandidate(int i)
// {
// TestCandidate candidate = new TestCandidateEntity();
// candidate.setApiMajor(nextInt(5));
// candidate.setApiMinor(nextInt(5));
// candidate.setApiSubminor(nextInt(5));
// candidate.setEcardapiUrl("https://test.secunet.com/" + candidate.getId());
// candidate.setEidinterfaceUrl("https://eid.test.secunet.com/" + candidate.getId());
// candidate.setSamlUrl("https://saml.test.secunet.com/" + candidate.getId());
// candidate.setCandidateName("Candidate Name " + i);
// candidate.setChipAuthenticationAlgorithms(getRandomSet(IcsCa.class));
// candidate.setMandatoryProfiles(getRandomSet(IcsMandatoryprofile.class));
// candidate.setOptionalProfiles(getRandomSet(IcsOptionalprofile.class));
// candidate.setProfileName("ProfileName " + i);
// candidate.setXmlEncryptionAlgorithms(getRandomSet(IcsXmlsecEncryptionContentUri.class));
// candidate.setVendor("secunet");
// candidate.setVersionMajor(nextInt(3));
// candidate.setVersionMinor(nextInt(6));
// candidate.setVersionSubminor(2);
// candidate.setXmlSignatureAlgorithmsEid(generateRandomXmlSignature(4));
// candidate.setXmlSignatureAlgorithmsSaml(generateRandomXmlSignature(2));
//
// Set<Tls> tlsEntitiesEidInterface = new HashSet<>();
// Tls eidEntity = new TlsEntity();
// eidEntity.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// eidEntity.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// eidEntity.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesEidInterface.add(eidEntity);
// Tls eidEntity2 = new TlsEntity();
// eidEntity2.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// eidEntity2.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// eidEntity2.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesEidInterface.add(eidEntity2);
// candidate.setTlsEidInterface(tlsEntitiesEidInterface);
//
// Set<Tls> tlsEntitiesSAML = new HashSet<>();
// Tls samlEntity = new TlsEntity();
// samlEntity.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// samlEntity.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// samlEntity.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesSAML.add(samlEntity);
// Tls samlEntity2 = new TlsEntity();
// samlEntity2.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// samlEntity2.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// samlEntity2.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesSAML.add(samlEntity2);
// candidate.setTlsSaml(tlsEntitiesSAML);
//
// Set<Tls> tlsEntitiesEcardPsk = new HashSet<>();
// Tls ecardPskEentity = new TlsEntity();
// ecardPskEentity.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// ecardPskEentity.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// ecardPskEentity.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesEcardPsk.add(ecardPskEentity);
// Tls ecardPskEentity2 = new TlsEntity();
// ecardPskEentity2.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// ecardPskEentity2.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// ecardPskEentity2.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesEcardPsk.add(ecardPskEentity2);
// candidate.setTlsEidInterface(tlsEntitiesEcardPsk);
//
// Set<Tls> tlsEntitiesAttached = new HashSet<>();
// Tls attachedEntity = new TlsEntity();
// attachedEntity.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// attachedEntity.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// attachedEntity.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesAttached.add(attachedEntity);
// Tls attachedEntity2 = new TlsEntity();
// attachedEntity2.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// attachedEntity2.setTlsCiphersuites(getRandomList(IcsTlsCiphersuite.class));
// attachedEntity2.setTlsVersion(IcsTlsVersion.values()[nextInt(IcsTlsVersion.values().length)]);
// tlsEntitiesAttached.add(attachedEntity2);
// candidate.setTlsEidInterface(tlsEntitiesAttached);
//
// return candidate;
// }
//
// private static Set<XmlSignature> generateRandomXmlSignature(int max)
// {
// Set<XmlSignature> set = new HashSet<>();
//
// for (int i = 0; i < max; i++)
// {
// XmlSignature xmlSig = new XmlSignatureEntity();
// xmlSig.setSignatureAlgorithm(IcsXmlsecSignatureUri.values()[nextInt(IcsXmlsecSignatureUri.values().length)]);
// xmlSig.setCanonicalizationMethod(IcsXmlsecSignatureCanonicalization.values()[nextInt(IcsXmlsecSignatureCanonicalization.values().length)]);
// xmlSig.setDigestMethod(IcsXmlsecSignatureDigest.values()[nextInt(IcsXmlsecSignatureDigest.values().length)]);
//
// if (0 == nextInt(2))
// {
// xmlSig.setBitLengths(Bitlengths.ALLOWED_BIT_LENGTHS);
// }
// else
// {
// xmlSig.setEllipticCurves(getRandomSet(IcsEllipticcurve.class));
// }
// set.add(xmlSig);
// }
//
// return set;
// }
//
// private static <T extends Enum<T>> Set<T> getRandomSet(Class<T> clazz)
// {
// T[] allTs = clazz.getEnumConstants();
//
// Set<T> set = new HashSet<>();
// int max = nextInt(allTs.length);
// for (int i = 0; i < max; i++)
// set.add(allTs[nextInt(allTs.length)]);
//
//
// return set;
// }
//
// private static <T extends Enum<T>> List<T> getRandomList(Class<T> clazz)
// {
// T[] allTs = clazz.getEnumConstants();
//
// List<T> set = new ArrayList<>();
// int max = nextInt(allTs.length);
// for (int i = 0; i < max; i++)
// {
// set.add(allTs[nextInt(allTs.length)]);
// }
//
// return set;
// }
//
// private static int nextInt(int length)
// {
// return r.nextInt(length);
// }
//
// }
