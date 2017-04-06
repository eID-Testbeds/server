
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für algorithmTypeEC.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="algorithmTypeEC">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sect233k1"/>
 *     &lt;enumeration value="sect233r1"/>
 *     &lt;enumeration value="sect239k1"/>
 *     &lt;enumeration value="sect283k1"/>
 *     &lt;enumeration value="sect283r1"/>
 *     &lt;enumeration value="sect409k1"/>
 *     &lt;enumeration value="sect409r1"/>
 *     &lt;enumeration value="sect571k1"/>
 *     &lt;enumeration value="sect571r1"/>
 *     &lt;enumeration value="secp192k1"/>
 *     &lt;enumeration value="secp192r1"/>
 *     &lt;enumeration value="secp224k1"/>
 *     &lt;enumeration value="secp224r1"/>
 *     &lt;enumeration value="secp256k1"/>
 *     &lt;enumeration value="secp256r1"/>
 *     &lt;enumeration value="secp384r1"/>
 *     &lt;enumeration value="secp521r1"/>
 *     &lt;enumeration value="brainpoolP256r1"/>
 *     &lt;enumeration value="brainpoolP384r1"/>
 *     &lt;enumeration value="brainpoolP512r1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "algorithmTypeEC")
@XmlEnum
public enum AlgorithmTypeEC {

    @XmlEnumValue("sect233k1")
    SECT_233_K_1("sect233k1"),
    @XmlEnumValue("sect233r1")
    SECT_233_R_1("sect233r1"),
    @XmlEnumValue("sect239k1")
    SECT_239_K_1("sect239k1"),
    @XmlEnumValue("sect283k1")
    SECT_283_K_1("sect283k1"),
    @XmlEnumValue("sect283r1")
    SECT_283_R_1("sect283r1"),
    @XmlEnumValue("sect409k1")
    SECT_409_K_1("sect409k1"),
    @XmlEnumValue("sect409r1")
    SECT_409_R_1("sect409r1"),
    @XmlEnumValue("sect571k1")
    SECT_571_K_1("sect571k1"),
    @XmlEnumValue("sect571r1")
    SECT_571_R_1("sect571r1"),
    @XmlEnumValue("secp192k1")
    SECP_192_K_1("secp192k1"),
    @XmlEnumValue("secp192r1")
    SECP_192_R_1("secp192r1"),
    @XmlEnumValue("secp224k1")
    SECP_224_K_1("secp224k1"),
    @XmlEnumValue("secp224r1")
    SECP_224_R_1("secp224r1"),
    @XmlEnumValue("secp256k1")
    SECP_256_K_1("secp256k1"),
    @XmlEnumValue("secp256r1")
    SECP_256_R_1("secp256r1"),
    @XmlEnumValue("secp384r1")
    SECP_384_R_1("secp384r1"),
    @XmlEnumValue("secp521r1")
    SECP_521_R_1("secp521r1"),
    @XmlEnumValue("brainpoolP256r1")
    BRAINPOOL_P_256_R_1("brainpoolP256r1"),
    @XmlEnumValue("brainpoolP384r1")
    BRAINPOOL_P_384_R_1("brainpoolP384r1"),
    @XmlEnumValue("brainpoolP512r1")
    BRAINPOOL_P_512_R_1("brainpoolP512r1");
    private final String value;

    AlgorithmTypeEC(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlgorithmTypeEC fromValue(String v) {
        for (AlgorithmTypeEC c: AlgorithmTypeEC.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
