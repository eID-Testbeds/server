
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für signatureAlgorithmTypeECDSA.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="signatureAlgorithmTypeECDSA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHA1withECDSA"/>
 *     &lt;enumeration value="SHA224withECDSA"/>
 *     &lt;enumeration value="SHA256withECDSA"/>
 *     &lt;enumeration value="SHA384withECDSA"/>
 *     &lt;enumeration value="SHA512withECDSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "signatureAlgorithmTypeECDSA")
@XmlEnum
public enum SignatureAlgorithmTypeECDSA {

    @XmlEnumValue("SHA1withECDSA")
    SHA_1_WITH_ECDSA("SHA1withECDSA"),
    @XmlEnumValue("SHA224withECDSA")
    SHA_224_WITH_ECDSA("SHA224withECDSA"),
    @XmlEnumValue("SHA256withECDSA")
    SHA_256_WITH_ECDSA("SHA256withECDSA"),
    @XmlEnumValue("SHA384withECDSA")
    SHA_384_WITH_ECDSA("SHA384withECDSA"),
    @XmlEnumValue("SHA512withECDSA")
    SHA_512_WITH_ECDSA("SHA512withECDSA");
    private final String value;

    SignatureAlgorithmTypeECDSA(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignatureAlgorithmTypeECDSA fromValue(String v) {
        for (SignatureAlgorithmTypeECDSA c: SignatureAlgorithmTypeECDSA.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
