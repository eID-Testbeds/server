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
 * <p>Java-Klasse für ics.optionalprofile.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.optionalprofile">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SOAP"/>
 *     &lt;enumeration value="SOAP_TLS"/>
 *     &lt;enumeration value="SAML"/>
 *     &lt;enumeration value="SAML_ATTACHED"/>
 *     &lt;enumeration value="ESER_ATTACHED"/>
 *     &lt;enumeration value="TLS_PSK"/>
 *     &lt;enumeration value="TLS_ETM"/>
 *     &lt;enumeration value="ALL_LINK"/>
 *     &lt;enumeration value="CONF_MAX_SE"/>
 *     &lt;enumeration value="CONF_TM_OUT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.optionalprofile")
@XmlEnum
public enum IcsOptionalprofile {

    SOAP,
    SOAP_TLS,
    SAML,
    SAML_ATTACHED,
    ESER_ATTACHED,
    TLS_PSK,
    TLS_ETM,
    ALL_LINK,
    CONF_MAX_SE,
    CONF_TM_OUT;

    public String value() {
        return name();
    }

    public static IcsOptionalprofile fromValue(String v) {
        return valueOf(v);
    }

}
