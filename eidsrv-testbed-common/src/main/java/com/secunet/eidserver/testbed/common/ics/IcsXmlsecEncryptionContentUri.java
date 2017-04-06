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
 * <p>Java-Klasse für ics.xmlsec.encryption.content.uri.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.encryption.content.uri">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#tripledes-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#aes128-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#aes192-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#aes256-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#aes128-gcm"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#aes192-gcm"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#aes256-gcm"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.encryption.content.uri")
@XmlEnum
public enum IcsXmlsecEncryptionContentUri {

    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_TRIPLEDES_CBC("http://www.w3.org/2001/04/xmlenc#tripledes-cbc"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#aes128-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_128_CBC("http://www.w3.org/2001/04/xmlenc#aes128-cbc"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#aes192-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_192_CBC("http://www.w3.org/2001/04/xmlenc#aes192-cbc"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#aes256-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_256_CBC("http://www.w3.org/2001/04/xmlenc#aes256-cbc"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#aes128-gcm")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_128_GCM("http://www.w3.org/2009/xmlenc11#aes128-gcm"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#aes192-gcm")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_192_GCM("http://www.w3.org/2009/xmlenc11#aes192-gcm"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#aes256-gcm")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_256_GCM("http://www.w3.org/2009/xmlenc11#aes256-gcm"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_CAMELLIA_128_CBC("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_CAMELLIA_192_CBC("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_CAMELLIA_256_CBC("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc");
    private final String value;

    IcsXmlsecEncryptionContentUri(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecEncryptionContentUri fromValue(String v) {
        for (IcsXmlsecEncryptionContentUri c: IcsXmlsecEncryptionContentUri.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
