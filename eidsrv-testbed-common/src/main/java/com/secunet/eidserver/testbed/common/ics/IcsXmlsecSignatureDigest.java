//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.xmlsec.signature.digest.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.signature.digest">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#md5"/>
 *     &lt;enumeration value="http://www.w3.org/2000/09/xmldsig#sha1"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#sha224"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#sha256"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#sha384"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#sha512"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#ripemd160"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.signature.digest")
@XmlEnum
public enum IcsXmlsecSignatureDigest {

    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#md5")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_MD_5("http://www.w3.org/2001/04/xmldsig-more#md5"),
    @XmlEnumValue("http://www.w3.org/2000/09/xmldsig#sha1")
    HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1("http://www.w3.org/2000/09/xmldsig#sha1"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#sha224")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_SHA_224("http://www.w3.org/2001/04/xmldsig-more#sha224"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#sha256")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256("http://www.w3.org/2001/04/xmlenc#sha256"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#sha384")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_SHA_384("http://www.w3.org/2001/04/xmldsig-more#sha384"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#sha512")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_512("http://www.w3.org/2001/04/xmlenc#sha512"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#ripemd160")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_RIPEMD_160("http://www.w3.org/2001/04/xmlenc#ripemd160");
    private final String value;

    IcsXmlsecSignatureDigest(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecSignatureDigest fromValue(String v) {
        for (IcsXmlsecSignatureDigest c: IcsXmlsecSignatureDigest.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
