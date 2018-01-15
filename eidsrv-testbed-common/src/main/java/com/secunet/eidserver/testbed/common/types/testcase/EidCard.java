//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.08.14 um 02:18:00 PM CEST 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für eidCard.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="eidCard">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EIDCARD_1"/>
 *     &lt;enumeration value="EIDCARD_2"/>
 *     &lt;enumeration value="EIDCARD_3"/>
 *     &lt;enumeration value="EIDCARD_4"/>
 *     &lt;enumeration value="EIDCARD_5"/>
 *     &lt;enumeration value="EIDCARD_6"/>
 *     &lt;enumeration value="EIDCARD_7"/>
 *     &lt;enumeration value="EIDCARD_8"/>
 *     &lt;enumeration value="EIDCARD_9"/>
 *     &lt;enumeration value="EIDCARD_10"/>
 *     &lt;enumeration value="EIDCARD_11"/>
 *     &lt;enumeration value="EIDCARD_12_1"/>
 *     &lt;enumeration value="EIDCARD_12_2"/>
 *     &lt;enumeration value="EIDCARD_12_3"/>
 *     &lt;enumeration value="EIDCARD_13"/>
 *     &lt;enumeration value="EIDCARD_14"/>
 *     &lt;enumeration value="EIDCARD_15"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "eidCard")
@XmlEnum
public enum EidCard {

    EIDCARD_1,
    EIDCARD_2,
    EIDCARD_3,
    EIDCARD_4,
    EIDCARD_5,
    EIDCARD_6,
    EIDCARD_7,
    EIDCARD_8,
    EIDCARD_9,
    EIDCARD_10,
    EIDCARD_11,
    EIDCARD_12_1,
    EIDCARD_12_2,
    EIDCARD_12_3,
    EIDCARD_13,
    EIDCARD_14,
    EIDCARD_15;

    public String value() {
        return name();
    }

    public static EidCard fromValue(String v) {
        return valueOf(v);
    }

}
