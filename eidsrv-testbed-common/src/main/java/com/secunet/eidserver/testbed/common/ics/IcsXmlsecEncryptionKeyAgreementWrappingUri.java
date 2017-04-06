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
 * <p>Java-Klasse für ics.xmlsec.encryption.key.agreement.wrapping.uri.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.encryption.key.agreement.wrapping.uri">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#kw-tripledes"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#kw-aes128"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#kw-aes-128-pad"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#kw-aes192"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#kw-aes256"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#kw-aes-256-pad"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#kw-camellia128"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#kw-camellia192"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmldsig-more#kw-camellia256"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.encryption.key.agreement.wrapping.uri")
@XmlEnum
public enum IcsXmlsecEncryptionKeyAgreementWrappingUri {

    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#kw-tripledes")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_KW_TRIPLEDES("http://www.w3.org/2001/04/xmlenc#kw-tripledes"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#kw-aes128")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_KW_AES_128("http://www.w3.org/2001/04/xmlenc#kw-aes128"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#kw-aes-128-pad")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_KW_AES_128_PAD("http://www.w3.org/2009/xmlenc11#kw-aes-128-pad"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#kw-aes192")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_KW_AES_192("http://www.w3.org/2001/04/xmlenc#kw-aes192"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#kw-aes256")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_KW_AES_256("http://www.w3.org/2001/04/xmlenc#kw-aes256"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#kw-aes-256-pad")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_KW_AES_256_PAD("http://www.w3.org/2009/xmlenc11#kw-aes-256-pad"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_KW_CAMELLIA_128("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_KW_CAMELLIA_192("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256")
    HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_KW_CAMELLIA_256("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256");
    private final String value;

    IcsXmlsecEncryptionKeyAgreementWrappingUri(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecEncryptionKeyAgreementWrappingUri fromValue(String v) {
        for (IcsXmlsecEncryptionKeyAgreementWrappingUri c: IcsXmlsecEncryptionKeyAgreementWrappingUri.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
