//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.xmlsec.signature.uri.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.signature.uri">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmldsig11#dsa-sha256"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-md5"/>
 *     &lt;enumeration value="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-sha224"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"/>
 *     &lt;enumeration value="http://www.w3.org/2000/09/xmldsig#hmac-sha1"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#hmac-sha224"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.signature.uri")
@XmlEnum
public enum IcsXmlsecSignatureUri {

    @XmlEnumValue("http://www.w3.org/2000/09/xmldsig#dsa-sha1")
    HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_DSA_SHA_1("http://www.w3.org/2000/09/xmldsig#dsa-sha1"),
    @XmlEnumValue("http://www.w3.org/2009/xmldsig11#dsa-sha256")
    HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256("http://www.w3.org/2009/xmldsig11#dsa-sha256"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-md5")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_MD_5("http://www.w3.org/2001/04/xmldsig-more#rsa-md5"),
    @XmlEnumValue("http://www.w3.org/2000/09/xmldsig#rsa-sha1")
    HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_RSA_SHA_1("http://www.w3.org/2000/09/xmldsig#rsa-sha1"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_224("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_RIPEMD_160("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_1("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_224("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"),
    @XmlEnumValue("http://www.w3.org/2000/09/xmldsig#hmac-sha1")
    HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_HMAC_SHA_1("http://www.w3.org/2000/09/xmldsig#hmac-sha1"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_224("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_256("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_384("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_512("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512");
    private final String value;

    IcsXmlsecSignatureUri(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecSignatureUri fromValue(String v) {
        for (IcsXmlsecSignatureUri c: IcsXmlsecSignatureUri.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
