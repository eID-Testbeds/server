//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.mandatoryprofile.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.mandatoryprofile">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAOS"/>
 *     &lt;enumeration value="EAC"/>
 *     &lt;enumeration value="EID_ACCESS"/>
 *     &lt;enumeration value="REVOKED_CARD"/>
 *     &lt;enumeration value="EXPIRED_CARD"/>
 *     &lt;enumeration value="NONAUTH_CARD"/>
 *     &lt;enumeration value="RI_MIGRATION"/>
 *     &lt;enumeration value="CRYPTO"/>
 *     &lt;enumeration value="DG_VARIATIONS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.mandatoryprofile")
@XmlEnum
public enum IcsMandatoryprofile {

    PAOS,
    EAC,
    EID_ACCESS,
    REVOKED_CARD,
    EXPIRED_CARD,
    NONAUTH_CARD,
    RI_MIGRATION,
    CRYPTO,
    DG_VARIATIONS;

    public String value() {
        return name();
    }

    public static IcsMandatoryprofile fromValue(String v) {
        return valueOf(v);
    }

}
