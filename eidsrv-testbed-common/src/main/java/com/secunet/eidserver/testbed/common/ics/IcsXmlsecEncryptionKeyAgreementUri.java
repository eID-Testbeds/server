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
 * <p>Java-Klasse für ics.xmlsec.encryption.key.agreement.uri.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.encryption.key.agreement.uri">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#dh"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#dh-es"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#ECDH-ES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.encryption.key.agreement.uri")
@XmlEnum
public enum IcsXmlsecEncryptionKeyAgreementUri {

    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#dh")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_DH("http://www.w3.org/2001/04/xmlenc#dh"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#dh-es")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_DH_ES("http://www.w3.org/2009/xmlenc11#dh-es"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#ECDH-ES")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_ECDH_ES("http://www.w3.org/2009/xmlenc11#ECDH-ES");
    private final String value;

    IcsXmlsecEncryptionKeyAgreementUri(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecEncryptionKeyAgreementUri fromValue(String v) {
        for (IcsXmlsecEncryptionKeyAgreementUri c: IcsXmlsecEncryptionKeyAgreementUri.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
