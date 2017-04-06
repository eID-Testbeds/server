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
 * <p>Java-Klasse für ics.ca.domainparams.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.ca.domainparams">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="modp1024_160"/>
 *     &lt;enumeration value="modp2048_224"/>
 *     &lt;enumeration value="modp2048_256"/>
 *     &lt;enumeration value="secp192r1"/>
 *     &lt;enumeration value="secp192r1"/>
 *     &lt;enumeration value="secp224r1"/>
 *     &lt;enumeration value="brainpoolp224r1"/>
 *     &lt;enumeration value="secp256r1"/>
 *     &lt;enumeration value="brainpoolp256r1"/>
 *     &lt;enumeration value="brainpoolp320r1"/>
 *     &lt;enumeration value="secp384r1"/>
 *     &lt;enumeration value="brainpoolp384r1"/>
 *     &lt;enumeration value="brainpoolp512r1"/>
 *     &lt;enumeration value="secp521r1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.ca.domainparams")
@XmlEnum
public enum IcsCaDomainparams {

    @XmlEnumValue("modp1024_160")
    MODP_1024_160("modp1024_160"),
    @XmlEnumValue("modp2048_224")
    MODP_2048_224("modp2048_224"),
    @XmlEnumValue("modp2048_256")
    MODP_2048_256("modp2048_256"),
    @XmlEnumValue("secp192r1")
    SECP_192_R_1("secp192r1"),
    @XmlEnumValue("secp224r1")
    SECP_224_R_1("secp224r1"),
    @XmlEnumValue("brainpoolp224r1")
    BRAINPOOLP_224_R_1("brainpoolp224r1"),
    @XmlEnumValue("secp256r1")
    SECP_256_R_1("secp256r1"),
    @XmlEnumValue("brainpoolp256r1")
    BRAINPOOLP_256_R_1("brainpoolp256r1"),
    @XmlEnumValue("brainpoolp320r1")
    BRAINPOOLP_320_R_1("brainpoolp320r1"),
    @XmlEnumValue("secp384r1")
    SECP_384_R_1("secp384r1"),
    @XmlEnumValue("brainpoolp384r1")
    BRAINPOOLP_384_R_1("brainpoolp384r1"),
    @XmlEnumValue("brainpoolp512r1")
    BRAINPOOLP_512_R_1("brainpoolp512r1"),
    @XmlEnumValue("secp521r1")
    SECP_521_R_1("secp521r1");
    private final String value;

    IcsCaDomainparams(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsCaDomainparams fromValue(String v) {
        for (IcsCaDomainparams c: IcsCaDomainparams.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
