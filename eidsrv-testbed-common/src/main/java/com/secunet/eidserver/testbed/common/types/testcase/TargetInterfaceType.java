//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.02 um 01:01:12 PM CET 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für targetInterfaceType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="targetInterfaceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="eID-Interface"/>
 *     &lt;enumeration value="eCard-API"/>
 *     &lt;enumeration value="SAML"/>
 *     &lt;enumeration value="Attached"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "targetInterfaceType")
@XmlEnum
public enum TargetInterfaceType {

    @XmlEnumValue("eID-Interface")
    E_ID_INTERFACE("eID-Interface"),
    @XmlEnumValue("eCard-API")
    E_CARD_API("eCard-API"),
    SAML("SAML"),
    @XmlEnumValue("Attached")
    ATTACHED("Attached");
    private final String value;

    TargetInterfaceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TargetInterfaceType fromValue(String v) {
        for (TargetInterfaceType c: TargetInterfaceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
