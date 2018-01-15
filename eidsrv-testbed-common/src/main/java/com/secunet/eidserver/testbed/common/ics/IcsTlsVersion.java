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
 * <p>Java-Klasse für ics.tls.version.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.tls.version">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SSLv3"/>
 *     &lt;enumeration value="TLSv10"/>
 *     &lt;enumeration value="TLSv11"/>
 *     &lt;enumeration value="TLSv12"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.tls.version")
@XmlEnum
public enum IcsTlsVersion {

    @XmlEnumValue("SSLv3")
    SS_LV_3("SSLv3"),
    @XmlEnumValue("TLSv10")
    TL_SV_10("TLSv10"),
    @XmlEnumValue("TLSv11")
    TL_SV_11("TLSv11"),
    @XmlEnumValue("TLSv12")
    TL_SV_12("TLSv12");
    private final String value;

    IcsTlsVersion(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsTlsVersion fromValue(String v) {
        for (IcsTlsVersion c: IcsTlsVersion.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
