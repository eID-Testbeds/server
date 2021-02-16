/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.eidasstarterkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.joda.time.DateTime;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.ext.saml2alg.impl.DigestMethodBuilder;
import org.opensaml.saml.ext.saml2alg.impl.SigningMethodBuilder;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdattr.impl.EntityAttributesBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.opensaml.saml.saml2.metadata.impl.CompanyBuilder;
import org.opensaml.saml.saml2.metadata.impl.ContactPersonBuilder;
import org.opensaml.saml.saml2.metadata.impl.EmailAddressBuilder;
import org.opensaml.saml.saml2.metadata.impl.EncryptionMethodBuilder;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorMarshaller;
import org.opensaml.saml.saml2.metadata.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.metadata.impl.GivenNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationDisplayNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationURLBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.saml.saml2.metadata.impl.SurNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.TelephoneNumberBuilder;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.KeyInfoBuilder;
import org.opensaml.xmlsec.signature.impl.X509CertificateBuilder;
import org.opensaml.xmlsec.signature.impl.X509DataBuilder;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.governikus.eumw.eidascommon.Utils;
import de.governikus.eumw.eidasstarterkit.person_attributes.EidasPersonAttributes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;


/**
 * Use this class to build a service provider metadata.xml
 *
 * @author hohnholt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
class EidasMetadataService
{

  private static final String PROTOCOL_VERSION_URI = "http://eidas.europa.eu/entity-attributes/protocol-version";

  private static final String APPLICATION_IDENTIFIER_URI = "http://eidas.europa.eu/entity-attributes/application-identifier";

  private static final String SUPPORTED_PROTOCOL_VERSION = "1.2";

  private String id;

  private String entityId;

  private Date validUntil;

  private X509Certificate sigCert;

  private X509Certificate encCert;

  private EidasOrganisation organisation;

  private EidasContactPerson technicalContact;

  private EidasContactPerson supportContact;

  private String postEndpoint;

  private String redirectEndpoint;

  private List<EidasPersonAttributes> attributes = new ArrayList<>();

  private String middlewareVersion;

  private boolean doSign;

  private boolean requesterIdFlag;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private List<EidasNameIdType> supportedNameIdTypes = new ArrayList<>();

  EidasMetadataService(String id,
                       String entityId,
                       Date validUntil,
                       X509Certificate sigCert,
                       X509Certificate encCert,
                       EidasOrganisation organisation,
                       EidasContactPerson technicalContact,
                       EidasContactPerson supportContact,
                       String postEndpoint,
                       String redirectEndpoint,
                       List<EidasPersonAttributes> attributes,
                       List<EidasNameIdType> supportedNameIdTypes,
                       String middlewareVersion,
                       boolean doSign,
                       boolean requesterIdFlag)
  {
    super();
    this.id = id;
    this.entityId = entityId;
    this.validUntil = validUntil;
    this.sigCert = sigCert;
    this.encCert = encCert;
    this.organisation = organisation;
    this.technicalContact = technicalContact;
    this.supportContact = supportContact;
    this.postEndpoint = postEndpoint;
    this.redirectEndpoint = redirectEndpoint;

    this.attributes = attributes;
    if (this.attributes == null)
    {
      this.attributes = new ArrayList<>();
    }

    this.supportedNameIdTypes = supportedNameIdTypes;
    if (this.supportedNameIdTypes == null)
    {
      this.supportedNameIdTypes = new ArrayList<>();
    }
    if (this.supportedNameIdTypes.isEmpty())
    {
      this.supportedNameIdTypes.add(EidasNameIdType.UNSPECIFIED);
    }

    this.middlewareVersion = middlewareVersion;
    this.doSign = doSign;
    this.requesterIdFlag = requesterIdFlag;
  }

  byte[] generate(EidasSigner signer) throws CertificateEncodingException, IOException, MarshallingException,
    SignatureException, TransformerFactoryConfigurationError, TransformerException
  {
    EntityDescriptor entityDescriptor = new EntityDescriptorBuilder().buildObject();
    entityDescriptor.setID(id);
    entityDescriptor.setEntityID(entityId);
    entityDescriptor.setValidUntil(new DateTime(validUntil.getTime()));

    IDPSSODescriptor idpDescriptor = new IDPSSODescriptorBuilder().buildObject();
    idpDescriptor.setWantAuthnRequestsSigned(true);
    idpDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

    KeyDescriptor kd = new KeyDescriptorBuilder().buildObject();
    kd.setUse(UsageType.SIGNING);
    KeyInfo ki = new KeyInfoBuilder().buildObject();
    X509Data x509 = new X509DataBuilder().buildObject();
    org.opensaml.xmlsec.signature.X509Certificate x509Cert = new X509CertificateBuilder().buildObject();
    x509Cert.setValue(new String(Base64.getEncoder().encode(sigCert.getEncoded()), StandardCharsets.UTF_8));
    x509.getX509Certificates().add(x509Cert);
    ki.getX509Datas().add(x509);
    kd.setKeyInfo(ki);
    idpDescriptor.getKeyDescriptors().add(kd);

    kd = new KeyDescriptorBuilder().buildObject();
    kd.setUse(UsageType.ENCRYPTION);
    ki = new KeyInfoBuilder().buildObject();
    x509 = new X509DataBuilder().buildObject();
    x509Cert = new X509CertificateBuilder().buildObject();
    x509Cert.setValue(new String(Base64.getEncoder().encode(encCert.getEncoded()), StandardCharsets.UTF_8));
    x509.getX509Certificates().add(x509Cert);
    ki.getX509Datas().add(x509);
    kd.setKeyInfo(ki);
    EncryptionMethod encMethod = new EncryptionMethodBuilder().buildObject();
    encMethod.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
    kd.getEncryptionMethods().add(encMethod);
    idpDescriptor.getKeyDescriptors().add(kd);

    SingleSignOnService sso = new SingleSignOnServiceBuilder().buildObject();
    sso.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    sso.setLocation(postEndpoint);
    idpDescriptor.getSingleSignOnServices().add(sso);

    sso = new SingleSignOnServiceBuilder().buildObject();
    sso.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
    sso.setLocation(redirectEndpoint);
    idpDescriptor.getSingleSignOnServices().add(sso);

    for ( EidasNameIdType nameIDType : this.supportedNameIdTypes )
    {
      NameIDFormat nif = new NameIDFormatBuilder().buildObject();
      nif.setFormat(nameIDType.value);
      idpDescriptor.getNameIDFormats().add(nif);
    }

    for ( EidasPersonAttributes att : attributes )
    {
      Attribute a = new AttributeBuilder().buildObject();
      a.setFriendlyName(att.getFriendlyName());
      a.setName(att.getValue());
      idpDescriptor.getAttributes().add(a);
    }

    entityDescriptor.getRoleDescriptors().add(idpDescriptor);

    Organization organization = new OrganizationBuilder().buildObject();
    OrganizationDisplayName odn = new OrganizationDisplayNameBuilder().buildObject();
    odn.setValue(organisation.getDisplayName());
    odn.setXMLLang(organisation.getLangId());
    organization.getDisplayNames().add(odn);
    OrganizationName on = new OrganizationNameBuilder().buildObject();
    on.setValue(organisation.getName());
    on.setXMLLang(organisation.getLangId());
    organization.getOrganizationNames().add(on);
    OrganizationURL ourl = new OrganizationURLBuilder().buildObject();
    ourl.setValue(organisation.getUrl());
    ourl.setXMLLang(organisation.getLangId());
    organization.getURLs().add(ourl);
    entityDescriptor.setOrganization(organization);

    ContactPerson cp = new ContactPersonBuilder().buildObject();
    Company comp = new CompanyBuilder().buildObject();
    comp.setName(technicalContact.getCompany());
    cp.setCompany(comp);
    GivenName gn = new GivenNameBuilder().buildObject();
    gn.setName(technicalContact.getGivenName());
    cp.setGivenName(gn);
    SurName sn = new SurNameBuilder().buildObject();
    sn.setName(technicalContact.getSurName());
    cp.setSurName(sn);
    EmailAddress email = new EmailAddressBuilder().buildObject();
    email.setAddress(technicalContact.getEmail());
    cp.getEmailAddresses().add(email);
    TelephoneNumber tel = new TelephoneNumberBuilder().buildObject();
    tel.setNumber(technicalContact.getTel());
    cp.getTelephoneNumbers().add(tel);
    cp.setType(ContactPersonTypeEnumeration.TECHNICAL);
    entityDescriptor.getContactPersons().add(cp);

    cp = new ContactPersonBuilder().buildObject();
    comp = new CompanyBuilder().buildObject();
    comp.setName(supportContact.getCompany());
    cp.setCompany(comp);
    gn = new GivenNameBuilder().buildObject();
    gn.setName(supportContact.getGivenName());
    cp.setGivenName(gn);
    sn = new SurNameBuilder().buildObject();
    sn.setName(supportContact.getSurName());
    cp.setSurName(sn);
    email = new EmailAddressBuilder().buildObject();
    email.setAddress(supportContact.getEmail());
    cp.getEmailAddresses().add(email);
    tel = new TelephoneNumberBuilder().buildObject();
    tel.setNumber(supportContact.getTel());
    cp.getTelephoneNumbers().add(tel);
    cp.setType(ContactPersonTypeEnumeration.SUPPORT);
    entityDescriptor.getContactPersons().add(cp);

    EntityAttributes entAttr = new EntityAttributesBuilder().buildObject(EntityAttributes.DEFAULT_ELEMENT_NAME);
    Namespace assertion = new Namespace(SAMLConstants.SAML20_NS, SAMLConstants.SAML20_PREFIX);
    entAttr.getNamespaceManager().registerNamespaceDeclaration(assertion);

    Attribute attr = new AttributeBuilder().buildObject();
    attr.setName("urn:oasis:names:tc:SAML:attribute:assurance-certification");
    attr.setNameFormat(Attribute.URI_REFERENCE);
    XSAny any = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
    any.setTextContent("http://eidas.europa.eu/LoA/high");
    attr.getAttributeValues().add(any);
    entAttr.getAttributes().add(attr);

    Attribute protocolAttribute = new AttributeBuilder().buildObject();
    protocolAttribute.setName(PROTOCOL_VERSION_URI);
    protocolAttribute.setNameFormat(Attribute.URI_REFERENCE);
    XSAny protocolVersion = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                                                           new QName("xs:string"));
    protocolVersion.setTextContent(SUPPORTED_PROTOCOL_VERSION);
    protocolAttribute.getAttributeValues().add(protocolVersion);
    entAttr.getAttributes().add(protocolAttribute);

    Attribute appliccationIdentifier = new AttributeBuilder().buildObject();
    appliccationIdentifier.setName(APPLICATION_IDENTIFIER_URI);
    appliccationIdentifier.setNameFormat(Attribute.URI_REFERENCE);
    XSAny applicationIdentifierVersion = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                                                                        new QName("xs:string"));
    applicationIdentifierVersion.setTextContent("German eIDAS Middleware version: " + middlewareVersion);
    appliccationIdentifier.getAttributeValues().add(applicationIdentifierVersion);
    entAttr.getAttributes().add(appliccationIdentifier);

    if (requesterIdFlag)
    {
      Attribute requesterIdAttribute = new AttributeBuilder().buildObject();
      requesterIdAttribute.setName("http://macedir.org/entity-category");
      requesterIdAttribute.setNameFormat(Attribute.URI_REFERENCE);
      XSAny requesterIdValue = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
      requesterIdValue.setTextContent("http://eidas.europa.eu/entity-attributes/termsofaccess/requesterid");
      requesterIdAttribute.getAttributeValues().add(requesterIdValue);
      entAttr.getAttributes().add(requesterIdAttribute);
    }

    Extensions ext = new ExtensionsBuilder().buildObject();
    ext.getUnknownXMLObjects().add(entAttr);

    DigestMethod dm = new DigestMethodBuilder().buildObject();
    dm.setAlgorithm(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
    ext.getUnknownXMLObjects().add(dm);

    SigningMethod sm = new SigningMethodBuilder().buildObject();
    sm.setAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256);
    sm.setMinKeySize(256);
    ext.getUnknownXMLObjects().add(sm);

    sm = new SigningMethodBuilder().buildObject();
    sm.setAlgorithm(EidasSigner.DIGEST_NONSPEC.equals(signer.getSigDigestAlg())
      ? XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256 : XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1);
    sm.setMinKeySize(3072);
    sm.setMaxKeySize(4096);
    ext.getUnknownXMLObjects().add(sm);

    entityDescriptor.setExtensions(ext);

    List<Signature> sigs = new ArrayList<>();
    if (doSign)
    {
      XMLSignatureHandler.addSignature(entityDescriptor,
                                       signer.getSigKey(),
                                       signer.getSigCert(),
                                       signer.getSigType(),
                                       signer.getSigDigestAlg());
      sigs.add(entityDescriptor.getSignature());
    }

    EntityDescriptorMarshaller arm = new EntityDescriptorMarshaller();
    Element all = arm.marshall(entityDescriptor);
    if (!sigs.isEmpty())
    {
      Signer.signObjects(sigs);
    }

    Transformer trans = Utils.getTransformer();
    trans.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());

    byte[] result = null;
    try (ByteArrayOutputStream bout = new ByteArrayOutputStream())
    {
      trans.transform(new DOMSource(all), new StreamResult(bout));
      result = bout.toByteArray();
    }
    return result;
  }

  static EidasMetadataService parse(InputStream is)
    throws XMLParserException, UnmarshallingException, CertificateException, ComponentInitializationException
  {
    EidasMetadataService eidasMetadataService = new EidasMetadataService();
    BasicParserPool ppMgr = Utils.getBasicParserPool();
    Document inCommonMDDoc = ppMgr.parse(is);
    Element metadataRoot = inCommonMDDoc.getDocumentElement();
    UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
    Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
    EntityDescriptor metaData = (EntityDescriptor)unmarshaller.unmarshall(metadataRoot);

    eidasMetadataService.setSupportContact(unmarshalContactPerson(metaData.getContactPersons(), "support"));
    eidasMetadataService.setTechnicalContact(unmarshalContactPerson(metaData.getContactPersons(),
                                                                    "technical"));
    eidasMetadataService.setOrganisation(unmarshalOrganisation(metaData.getOrganization()));
    eidasMetadataService.setId(metaData.getID());
    eidasMetadataService.setEntityId(metaData.getEntityID());
    eidasMetadataService.setValidUntil(metaData.getValidUntil().toDate());
    Extensions extensions = metaData.getExtensions();
    eidasMetadataService.setMiddlewareVersion(getMiddlewareVersionFromExtension(extensions));
    IDPSSODescriptor idpssoDescriptor = metaData.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol");
    idpssoDescriptor.getSingleSignOnServices().forEach(s -> {
      String bindString = s.getBinding();
      if ("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST".equals(bindString))
      {
        eidasMetadataService.setPostEndpoint(s.getLocation());
      }
      else if ("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect".equals(bindString))
      {
        eidasMetadataService.setRedirectEndpoint(s.getLocation());
      }
    });
    List<EidasPersonAttributes> attributes = new ArrayList<>();
    idpssoDescriptor.getAttributes().forEach(a -> {
      EidasPersonAttributes eidasPersonAttributes = null;
      try
      {
        eidasPersonAttributes = EidasNaturalPersonAttributes.getValueOf(a.getName());
      }
      catch (Exception e1)
      { // legal person?
        try
        {
          eidasPersonAttributes = EidasLegalPersonAttributes.getValueOf(a.getName());
        }
        catch (Exception e)
        { // no natural and no legal
          // ignore error, perhaps log?
        }
      }

      attributes.add(eidasPersonAttributes);
    });
    eidasMetadataService.setAttributes(attributes);
    for ( KeyDescriptor k : idpssoDescriptor.getKeyDescriptors() )
    {
      if (k.getUse() == UsageType.ENCRYPTION)
      {
        eidasMetadataService.encCert = getFirstCertFromKeyDescriptor(k);
      }
      else if (k.getUse() == UsageType.SIGNING)
      {
        eidasMetadataService.sigCert = getFirstCertFromKeyDescriptor(k);
      }
    }
    return eidasMetadataService;
  }

  private static String getMiddlewareVersionFromExtension(Extensions extensions)
  {
    String middlewareVersion = null;
    List<XMLObject> orderedChildren = extensions.getOrderedChildren();
    if (orderedChildren != null)
    {
      for ( XMLObject xmlObject : orderedChildren )
      {
        if (xmlObject.getDOM() != null && "EntityAttributes".equals(xmlObject.getDOM().getLocalName()))
        {
          middlewareVersion = getMiddlewareVersionFromEntityAttributes((EntityAttributes)xmlObject);
        }
      }
    }
    return middlewareVersion;
  }

  private static String getMiddlewareVersionFromEntityAttributes(EntityAttributes entityAttributes)
  {
    String middlewareVersion = null;
    List<Attribute> attributes = entityAttributes.getAttributes();
    for ( Attribute attribute : attributes )
    {
      if (attribute.getName().contains("application-identifier"))
      {
        middlewareVersion = getMiddlewareVersionFromAttribute(attribute);
      }
    }
    return middlewareVersion;
  }

  private static String getMiddlewareVersionFromAttribute(Attribute attribute)
  {
    List<XMLObject> attributeValues = attribute.getAttributeValues();
    XSAny xsAnytextContent = (XSAny)attributeValues.get(0);
    if (xsAnytextContent.getTextContent() != null)
    {
      return xsAnytextContent.getTextContent().substring("German eIDAS Middleware version: ".length());
    }
    return null;
  }

  private static EidasContactPerson unmarshalContactPerson(List<ContactPerson> cps, String contactType)
  {
    for ( ContactPerson cp : cps )
    {
      String company = cp.getCompany().getName();
      String givenName = cp.getGivenName().getName();
      String surName = cp.getSurName().getName();
      String tel = cp.getTelephoneNumbers().get(0).getNumber();
      String email = cp.getEmailAddresses().get(0).getAddress();
      String type = cp.getType().toString();
      EidasContactPerson ecp = new EidasContactPerson(company, givenName, surName, tel, email, type);
      if (type != null && (type).equalsIgnoreCase(contactType))
      {
        return ecp;
      }
    }
    return null;
  }

  private static EidasOrganisation unmarshalOrganisation(Organization org)
  {
    String displayName = org.getDisplayNames().get(0).getValue();
    String name = org.getOrganizationNames().get(0).getValue();
    String url = org.getURLs().get(0).getValue();
    String langId = org.getDisplayNames().get(0).getXMLLang();
    return new EidasOrganisation(name, displayName, url, langId);
  }

  /**
   * Search in a KeyDescriptor node for the first certificate
   *
   * @param keyDescriptor
   * @return the first Cert from the given keyDescriptor
   * @throws CertificateException
   */
  static X509Certificate getFirstCertFromKeyDescriptor(KeyDescriptor keyDescriptor)
    throws CertificateException
  {
    X509Certificate cert = null;
    if (keyDescriptor.getKeyInfo().getX509Datas() != null
        && !keyDescriptor.getKeyInfo().getX509Datas().isEmpty())
    {
      X509Data data = keyDescriptor.getKeyInfo().getX509Datas().get(0);
      if (data != null)
      {
        NodeList childs = data.getDOM().getChildNodes();
        for ( int i = 0 ; i < childs.getLength() ; i++ )
        {
          if ("X509Certificate".equals(childs.item(i).getLocalName()))
          {
            String base64String = childs.item(i).getTextContent();
            byte[] bytes = Base64.getMimeDecoder().decode(base64String);
            cert = Utils.readCert(bytes, true);
          }
        }
      }
    }
    return cert;
  }
}
