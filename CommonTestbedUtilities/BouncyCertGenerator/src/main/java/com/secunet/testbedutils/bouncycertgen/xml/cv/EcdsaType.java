
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ecdsaType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ecdsaType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ASN1::secp112r1"/>
 *     &lt;enumeration value="ASN1::secp112r2"/>
 *     &lt;enumeration value="ASN1::secp128r1"/>
 *     &lt;enumeration value="ASN1::secp128r2"/>
 *     &lt;enumeration value="ASN1::secp160r1"/>
 *     &lt;enumeration value="ASN1::secp160k1"/>
 *     &lt;enumeration value="ASN1::secp160r2"/>
 *     &lt;enumeration value="ASN1::secp192k1"/>
 *     &lt;enumeration value="ASN1::secp192r1"/>
 *     &lt;enumeration value="ASN1::secp224k1"/>
 *     &lt;enumeration value="ASN1::secp224r1"/>
 *     &lt;enumeration value="ASN1::secp256k1"/>
 *     &lt;enumeration value="ASN1::secp256r1"/>
 *     &lt;enumeration value="ASN1::secp384r1"/>
 *     &lt;enumeration value="ASN1::secp521r1"/>
 *     &lt;enumeration value="BRAINPOOL::p160r1"/>
 *     &lt;enumeration value="BRAINPOOL::p160t1"/>
 *     &lt;enumeration value="BRAINPOOL::p192r1"/>
 *     &lt;enumeration value="BRAINPOOL::p192t1"/>
 *     &lt;enumeration value="BRAINPOOL::p224r1"/>
 *     &lt;enumeration value="BRAINPOOL::p224t1"/>
 *     &lt;enumeration value="BRAINPOOL::p256r1"/>
 *     &lt;enumeration value="BRAINPOOL::p256t1"/>
 *     &lt;enumeration value="BRAINPOOL::p320r1"/>
 *     &lt;enumeration value="BRAINPOOL::p320t1"/>
 *     &lt;enumeration value="BRAINPOOL::p384r1"/>
 *     &lt;enumeration value="BRAINPOOL::p384t1"/>
 *     &lt;enumeration value="BRAINPOOL::p512r1"/>
 *     &lt;enumeration value="BRAINPOOL::p512t1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ecdsaType", namespace = "http://www.secunet.com")
@XmlEnum
public enum EcdsaType {

    @XmlEnumValue("ASN1::secp112r1")
    ASN_1_SECP_112_R_1("ASN1::secp112r1"),
    @XmlEnumValue("ASN1::secp112r2")
    ASN_1_SECP_112_R_2("ASN1::secp112r2"),
    @XmlEnumValue("ASN1::secp128r1")
    ASN_1_SECP_128_R_1("ASN1::secp128r1"),
    @XmlEnumValue("ASN1::secp128r2")
    ASN_1_SECP_128_R_2("ASN1::secp128r2"),
    @XmlEnumValue("ASN1::secp160r1")
    ASN_1_SECP_160_R_1("ASN1::secp160r1"),
    @XmlEnumValue("ASN1::secp160k1")
    ASN_1_SECP_160_K_1("ASN1::secp160k1"),
    @XmlEnumValue("ASN1::secp160r2")
    ASN_1_SECP_160_R_2("ASN1::secp160r2"),
    @XmlEnumValue("ASN1::secp192k1")
    ASN_1_SECP_192_K_1("ASN1::secp192k1"),
    @XmlEnumValue("ASN1::secp192r1")
    ASN_1_SECP_192_R_1("ASN1::secp192r1"),
    @XmlEnumValue("ASN1::secp224k1")
    ASN_1_SECP_224_K_1("ASN1::secp224k1"),
    @XmlEnumValue("ASN1::secp224r1")
    ASN_1_SECP_224_R_1("ASN1::secp224r1"),
    @XmlEnumValue("ASN1::secp256k1")
    ASN_1_SECP_256_K_1("ASN1::secp256k1"),
    @XmlEnumValue("ASN1::secp256r1")
    ASN_1_SECP_256_R_1("ASN1::secp256r1"),
    @XmlEnumValue("ASN1::secp384r1")
    ASN_1_SECP_384_R_1("ASN1::secp384r1"),
    @XmlEnumValue("ASN1::secp521r1")
    ASN_1_SECP_521_R_1("ASN1::secp521r1"),
    @XmlEnumValue("BRAINPOOL::p160r1")
    BRAINPOOL_P_160_R_1("BRAINPOOL::p160r1"),
    @XmlEnumValue("BRAINPOOL::p160t1")
    BRAINPOOL_P_160_T_1("BRAINPOOL::p160t1"),
    @XmlEnumValue("BRAINPOOL::p192r1")
    BRAINPOOL_P_192_R_1("BRAINPOOL::p192r1"),
    @XmlEnumValue("BRAINPOOL::p192t1")
    BRAINPOOL_P_192_T_1("BRAINPOOL::p192t1"),
    @XmlEnumValue("BRAINPOOL::p224r1")
    BRAINPOOL_P_224_R_1("BRAINPOOL::p224r1"),
    @XmlEnumValue("BRAINPOOL::p224t1")
    BRAINPOOL_P_224_T_1("BRAINPOOL::p224t1"),
    @XmlEnumValue("BRAINPOOL::p256r1")
    BRAINPOOL_P_256_R_1("BRAINPOOL::p256r1"),
    @XmlEnumValue("BRAINPOOL::p256t1")
    BRAINPOOL_P_256_T_1("BRAINPOOL::p256t1"),
    @XmlEnumValue("BRAINPOOL::p320r1")
    BRAINPOOL_P_320_R_1("BRAINPOOL::p320r1"),
    @XmlEnumValue("BRAINPOOL::p320t1")
    BRAINPOOL_P_320_T_1("BRAINPOOL::p320t1"),
    @XmlEnumValue("BRAINPOOL::p384r1")
    BRAINPOOL_P_384_R_1("BRAINPOOL::p384r1"),
    @XmlEnumValue("BRAINPOOL::p384t1")
    BRAINPOOL_P_384_T_1("BRAINPOOL::p384t1"),
    @XmlEnumValue("BRAINPOOL::p512r1")
    BRAINPOOL_P_512_R_1("BRAINPOOL::p512r1"),
    @XmlEnumValue("BRAINPOOL::p512t1")
    BRAINPOOL_P_512_T_1("BRAINPOOL::p512t1");
    private final String value;

    EcdsaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EcdsaType fromValue(String v) {
        for (EcdsaType c: EcdsaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
