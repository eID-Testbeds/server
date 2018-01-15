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
 * <p>Java-Klasse für ics.xmlsec.encryption.key.transport.uri.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.encryption.key.transport.uri">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#rsa-1_5"/>
 *     &lt;enumeration value="http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"/>
 *     &lt;enumeration value="http://www.w3.org/2009/xmlenc11#rsa-oaep"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.encryption.key.transport.uri")
@XmlEnum
public enum IcsXmlsecEncryptionKeyTransportUri {

    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#rsa-1_5")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_RSA_1_5("http://www.w3.org/2001/04/xmlenc#rsa-1_5"),
    @XmlEnumValue("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p")
    HTTP_WWW_W_3_ORG_2001_04_XMLENC_RSA_OAEP_MGF_1_P("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"),
    @XmlEnumValue("http://www.w3.org/2009/xmlenc11#rsa-oaep")
    HTTP_WWW_W_3_ORG_2009_XMLENC_11_RSA_OAEP("http://www.w3.org/2009/xmlenc11#rsa-oaep");
    private final String value;

    IcsXmlsecEncryptionKeyTransportUri(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecEncryptionKeyTransportUri fromValue(String v) {
        for (IcsXmlsecEncryptionKeyTransportUri c: IcsXmlsecEncryptionKeyTransportUri.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
