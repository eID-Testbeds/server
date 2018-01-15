//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.08.14 um 02:18:00 PM CEST 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für certName.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="certName">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CERT_ECARD_TLS_EIDSERVER_1"/>
 *     &lt;enumeration value="CERT_ECARD_TLS_SAMLPROCESSOR_1_"/>
 *     &lt;enumeration value="CERT_EID_TLS_EIDSERVER_1"/>
 *     &lt;enumeration value="CERT_EID_TLS_ESERVICE_2_A"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "certName")
@XmlEnum
public enum CertName {

    CERT_ECARD_TLS_EIDSERVER_1("CERT_ECARD_TLS_EIDSERVER_1"),
    @XmlEnumValue("CERT_ECARD_TLS_SAMLPROCESSOR_1_")
    CERT_ECARD_TLS_SAMLPROCESSOR_1("CERT_ECARD_TLS_SAMLPROCESSOR_1_"),
    CERT_EID_TLS_EIDSERVER_1("CERT_EID_TLS_EIDSERVER_1"),
    CERT_EID_TLS_ESERVICE_2_A("CERT_EID_TLS_ESERVICE_2_A");
    private final String value;

    CertName(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CertName fromValue(String v) {
        for (CertName c: CertName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
