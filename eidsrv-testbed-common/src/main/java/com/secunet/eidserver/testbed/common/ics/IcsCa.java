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
 * <p>Java-Klasse für ics.ca.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.ca">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="id-CA-DH-3DES-CBC-CBC"/>
 *     &lt;enumeration value="id-CA-DH-AES-CBC-CMAC-128"/>
 *     &lt;enumeration value="id-CA-DH-AES-CBC-CMAC-192"/>
 *     &lt;enumeration value="id-CA-DH-AES-CBC-CMAC-256"/>
 *     &lt;enumeration value="id-CA-ECDH-3DES-CBC-CBC"/>
 *     &lt;enumeration value="id-CA-ECDH-AES-CBC-CMAC-128"/>
 *     &lt;enumeration value="id-CA-ECDH-AES-CBC-CMAC-192"/>
 *     &lt;enumeration value="id-CA-ECDH-AES-CBC-CMAC-256"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.ca")
@XmlEnum
public enum IcsCa {

    @XmlEnumValue("id-CA-DH-3DES-CBC-CBC")
    ID_CA_DH_3_DES_CBC_CBC("id-CA-DH-3DES-CBC-CBC"),
    @XmlEnumValue("id-CA-DH-AES-CBC-CMAC-128")
    ID_CA_DH_AES_CBC_CMAC_128("id-CA-DH-AES-CBC-CMAC-128"),
    @XmlEnumValue("id-CA-DH-AES-CBC-CMAC-192")
    ID_CA_DH_AES_CBC_CMAC_192("id-CA-DH-AES-CBC-CMAC-192"),
    @XmlEnumValue("id-CA-DH-AES-CBC-CMAC-256")
    ID_CA_DH_AES_CBC_CMAC_256("id-CA-DH-AES-CBC-CMAC-256"),
    @XmlEnumValue("id-CA-ECDH-3DES-CBC-CBC")
    ID_CA_ECDH_3_DES_CBC_CBC("id-CA-ECDH-3DES-CBC-CBC"),
    @XmlEnumValue("id-CA-ECDH-AES-CBC-CMAC-128")
    ID_CA_ECDH_AES_CBC_CMAC_128("id-CA-ECDH-AES-CBC-CMAC-128"),
    @XmlEnumValue("id-CA-ECDH-AES-CBC-CMAC-192")
    ID_CA_ECDH_AES_CBC_CMAC_192("id-CA-ECDH-AES-CBC-CMAC-192"),
    @XmlEnumValue("id-CA-ECDH-AES-CBC-CMAC-256")
    ID_CA_ECDH_AES_CBC_CMAC_256("id-CA-ECDH-AES-CBC-CMAC-256");
    private final String value;

    IcsCa(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsCa fromValue(String v) {
        for (IcsCa c: IcsCa.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
