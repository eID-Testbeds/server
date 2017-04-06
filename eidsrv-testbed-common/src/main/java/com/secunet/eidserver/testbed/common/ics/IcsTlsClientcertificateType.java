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
 * <p>Java-Klasse für ics.tls.clientcertificate.type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.tls.clientcertificate.type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="rsa_sign"/>
 *     &lt;enumeration value="dss_sign"/>
 *     &lt;enumeration value="rsa_fixed_dh"/>
 *     &lt;enumeration value="dss_fixed_dh"/>
 *     &lt;enumeration value="rsa_ephemeral_dh_RESERVED"/>
 *     &lt;enumeration value="dss_ephemeral_dh_RESERVED"/>
 *     &lt;enumeration value="fortezza_dms_RESERVED"/>
 *     &lt;enumeration value="ecdsa_sign"/>
 *     &lt;enumeration value="rsa_fixed_ecdh"/>
 *     &lt;enumeration value="ecdsa_fixed_ecdh"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.tls.clientcertificate.type")
@XmlEnum
public enum IcsTlsClientcertificateType {

    @XmlEnumValue("rsa_sign")
    RSA_SIGN("rsa_sign"),
    @XmlEnumValue("dss_sign")
    DSS_SIGN("dss_sign"),
    @XmlEnumValue("rsa_fixed_dh")
    RSA_FIXED_DH("rsa_fixed_dh"),
    @XmlEnumValue("dss_fixed_dh")
    DSS_FIXED_DH("dss_fixed_dh"),
    @XmlEnumValue("rsa_ephemeral_dh_RESERVED")
    RSA_EPHEMERAL_DH_RESERVED("rsa_ephemeral_dh_RESERVED"),
    @XmlEnumValue("dss_ephemeral_dh_RESERVED")
    DSS_EPHEMERAL_DH_RESERVED("dss_ephemeral_dh_RESERVED"),
    @XmlEnumValue("fortezza_dms_RESERVED")
    FORTEZZA_DMS_RESERVED("fortezza_dms_RESERVED"),
    @XmlEnumValue("ecdsa_sign")
    ECDSA_SIGN("ecdsa_sign"),
    @XmlEnumValue("rsa_fixed_ecdh")
    RSA_FIXED_ECDH("rsa_fixed_ecdh"),
    @XmlEnumValue("ecdsa_fixed_ecdh")
    ECDSA_FIXED_ECDH("ecdsa_fixed_ecdh");
    private final String value;

    IcsTlsClientcertificateType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsTlsClientcertificateType fromValue(String v) {
        for (IcsTlsClientcertificateType c: IcsTlsClientcertificateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
