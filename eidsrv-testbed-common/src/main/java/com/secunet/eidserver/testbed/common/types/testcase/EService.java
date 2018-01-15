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
 * <p>Java-Klasse für eService.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="eService">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="B"/>
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="EDSA"/>
 *     &lt;enumeration value="ERSA"/>
 *     &lt;enumeration value="EECDSA"/>
 *     &lt;enumeration value="F"/>
 *     &lt;enumeration value="A2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "eService")
@XmlEnum
public enum EService {

    A("A"),
    B("B"),
    C("C"),
    D("D"),
    EDSA("EDSA"),
    ERSA("ERSA"),
    EECDSA("EECDSA"),
    F("F"),
    @XmlEnumValue("A2")
    A_2("A2");
    private final String value;

    EService(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EService fromValue(String v) {
        for (EService c: EService.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
