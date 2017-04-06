
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für signatureAlgorithmTypeRSA.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="signatureAlgorithmTypeRSA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHA1withRSA"/>
 *     &lt;enumeration value="SHA224withRSA"/>
 *     &lt;enumeration value="SHA256withRSA"/>
 *     &lt;enumeration value="SHA384withRSA"/>
 *     &lt;enumeration value="SHA512withRSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "signatureAlgorithmTypeRSA")
@XmlEnum
public enum SignatureAlgorithmTypeRSA {

    @XmlEnumValue("SHA1withRSA")
    SHA_1_WITH_RSA("SHA1withRSA"),
    @XmlEnumValue("SHA224withRSA")
    SHA_224_WITH_RSA("SHA224withRSA"),
    @XmlEnumValue("SHA256withRSA")
    SHA_256_WITH_RSA("SHA256withRSA"),
    @XmlEnumValue("SHA384withRSA")
    SHA_384_WITH_RSA("SHA384withRSA"),
    @XmlEnumValue("SHA512withRSA")
    SHA_512_WITH_RSA("SHA512withRSA");
    private final String value;

    SignatureAlgorithmTypeRSA(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignatureAlgorithmTypeRSA fromValue(String v) {
        for (SignatureAlgorithmTypeRSA c: SignatureAlgorithmTypeRSA.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
