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
 * <p>Java-Klasse für ics.tls.ciphersuite.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.tls.ciphersuite">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_DSS_WITH_AES_128_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_AES_128_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_DSS_WITH_AES_128_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_128_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_DSS_WITH_AES_256_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_AES_256_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_DSS_WITH_AES_256_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_256_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_DSS_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DH_DSS_WITH_AES_256_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_DH_RSA_WITH_AES_256_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_256_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384"/>
 *     &lt;enumeration value="TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384"/>
 *     &lt;enumeration value="TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"/>
 *     &lt;enumeration value="TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384"/>
 *     &lt;enumeration value="TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_RC4_128_SHA"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_128_CBC_SHA"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_256_CBC_SHA"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_128_CBC_SHA256"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_256_CBC_SHA384"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_RSA_PSK_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_256_GCM_SHA384"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_128_GCM_SHA256"/>
 *     &lt;enumeration value="TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.tls.ciphersuite")
@XmlEnum
public enum IcsTlsCiphersuite {

    @XmlEnumValue("TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA")
    TLS_DH_DSS_WITH_3_DES_EDE_CBC_SHA("TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA"),
    @XmlEnumValue("TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA")
    TLS_DH_RSA_WITH_3_DES_EDE_CBC_SHA("TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA"),
    @XmlEnumValue("TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA")
    TLS_DHE_DSS_WITH_3_DES_EDE_CBC_SHA("TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA"),
    @XmlEnumValue("TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA")
    TLS_DHE_RSA_WITH_3_DES_EDE_CBC_SHA("TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA"),
    TLS_DH_DSS_WITH_AES_128_CBC_SHA("TLS_DH_DSS_WITH_AES_128_CBC_SHA"),
    TLS_DH_RSA_WITH_AES_128_CBC_SHA("TLS_DH_RSA_WITH_AES_128_CBC_SHA"),
    TLS_DHE_DSS_WITH_AES_128_CBC_SHA("TLS_DHE_DSS_WITH_AES_128_CBC_SHA"),
    TLS_DHE_RSA_WITH_AES_128_CBC_SHA("TLS_DHE_RSA_WITH_AES_128_CBC_SHA"),
    TLS_DH_DSS_WITH_AES_256_CBC_SHA("TLS_DH_DSS_WITH_AES_256_CBC_SHA"),
    TLS_DH_RSA_WITH_AES_256_CBC_SHA("TLS_DH_RSA_WITH_AES_256_CBC_SHA"),
    TLS_DHE_DSS_WITH_AES_256_CBC_SHA("TLS_DHE_DSS_WITH_AES_256_CBC_SHA"),
    TLS_DHE_RSA_WITH_AES_256_CBC_SHA("TLS_DHE_RSA_WITH_AES_256_CBC_SHA"),
    @XmlEnumValue("TLS_DH_DSS_WITH_AES_128_CBC_SHA256")
    TLS_DH_DSS_WITH_AES_128_CBC_SHA_256("TLS_DH_DSS_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_DH_RSA_WITH_AES_128_CBC_SHA256")
    TLS_DH_RSA_WITH_AES_128_CBC_SHA_256("TLS_DH_RSA_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256")
    TLS_DHE_DSS_WITH_AES_128_CBC_SHA_256("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_DH_DSS_WITH_AES_256_CBC_SHA256")
    TLS_DH_DSS_WITH_AES_256_CBC_SHA_256("TLS_DH_DSS_WITH_AES_256_CBC_SHA256"),
    @XmlEnumValue("TLS_DH_RSA_WITH_AES_256_CBC_SHA256")
    TLS_DH_RSA_WITH_AES_256_CBC_SHA_256("TLS_DH_RSA_WITH_AES_256_CBC_SHA256"),
    @XmlEnumValue("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256")
    TLS_DHE_DSS_WITH_AES_256_CBC_SHA_256("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"),
    @XmlEnumValue("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256")
    TLS_DHE_RSA_WITH_AES_256_CBC_SHA_256("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256"),
    @XmlEnumValue("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256")
    TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA_256("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384")
    TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA_384("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384"),
    @XmlEnumValue("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256")
    TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA_256("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384")
    TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA_384("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384"),
    @XmlEnumValue("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256")
    TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA_256("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384")
    TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA_384("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"),
    @XmlEnumValue("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256")
    TLS_ECDH_RSA_WITH_AES_128_CBC_SHA_256("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384")
    TLS_ECDH_RSA_WITH_AES_256_CBC_SHA_384("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384"),
    @XmlEnumValue("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256")
    TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA_256("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384")
    TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA_384("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256")
    TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA_256("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384")
    TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA_384("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256")
    TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA_256("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384")
    TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA_384("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256")
    TLS_ECDH_RSA_WITH_AES_128_GCM_SHA_256("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384")
    TLS_ECDH_RSA_WITH_AES_256_GCM_SHA_384("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_RC4_128_SHA")
    TLS_RSA_PSK_WITH_RC_4_128_SHA("TLS_RSA_PSK_WITH_RC4_128_SHA"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA")
    TLS_RSA_PSK_WITH_3_DES_EDE_CBC_SHA("TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA"),
    TLS_RSA_PSK_WITH_AES_128_CBC_SHA("TLS_RSA_PSK_WITH_AES_128_CBC_SHA"),
    TLS_RSA_PSK_WITH_AES_256_CBC_SHA("TLS_RSA_PSK_WITH_AES_256_CBC_SHA"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_AES_128_CBC_SHA256")
    TLS_RSA_PSK_WITH_AES_128_CBC_SHA_256("TLS_RSA_PSK_WITH_AES_128_CBC_SHA256"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_AES_256_CBC_SHA384")
    TLS_RSA_PSK_WITH_AES_256_CBC_SHA_384("TLS_RSA_PSK_WITH_AES_256_CBC_SHA384"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_AES_128_GCM_SHA256")
    TLS_RSA_PSK_WITH_AES_128_GCM_SHA_256("TLS_RSA_PSK_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_RSA_PSK_WITH_AES_256_GCM_SHA384")
    TLS_RSA_PSK_WITH_AES_256_GCM_SHA_384("TLS_RSA_PSK_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384")
    TLS_DHE_RSA_WITH_AES_256_GCM_SHA_384("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384"),
    @XmlEnumValue("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256")
    TLS_DHE_RSA_WITH_AES_128_GCM_SHA_256("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256"),
    @XmlEnumValue("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256")
    TLS_DHE_RSA_WITH_AES_128_CBC_SHA_256("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256");
    private final String value;

    IcsTlsCiphersuite(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsTlsCiphersuite fromValue(String v) {
        for (IcsTlsCiphersuite c: IcsTlsCiphersuite.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
