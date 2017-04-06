
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für algorithmType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="algorithmType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TA_RSA_v1_5_SHA_1"/>
 *     &lt;enumeration value="TA_RSA_v1_5_SHA_256"/>
 *     &lt;enumeration value="TA_RSA_PSS_SHA_1"/>
 *     &lt;enumeration value="TA_RSA_PSS_SHA_256"/>
 *     &lt;enumeration value="TA_ECDSA_SHA_1"/>
 *     &lt;enumeration value="TA_ECDSA_SHA_224"/>
 *     &lt;enumeration value="TA_ECDSA_SHA_256"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "algorithmType", namespace = "http://www.secunet.com")
@XmlEnum
public enum AlgorithmType {

    @XmlEnumValue("TA_RSA_v1_5_SHA_1")
    TA_RSA_V_1_5_SHA_1("TA_RSA_v1_5_SHA_1"),
    @XmlEnumValue("TA_RSA_v1_5_SHA_256")
    TA_RSA_V_1_5_SHA_256("TA_RSA_v1_5_SHA_256"),
    TA_RSA_PSS_SHA_1("TA_RSA_PSS_SHA_1"),
    TA_RSA_PSS_SHA_256("TA_RSA_PSS_SHA_256"),
    TA_ECDSA_SHA_1("TA_ECDSA_SHA_1"),
    TA_ECDSA_SHA_224("TA_ECDSA_SHA_224"),
    TA_ECDSA_SHA_256("TA_ECDSA_SHA_256");
    private final String value;

    AlgorithmType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlgorithmType fromValue(String v) {
        for (AlgorithmType c: AlgorithmType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
