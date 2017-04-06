
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für terminalType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="terminalType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AT"/>
 *     &lt;enumeration value="IS"/>
 *     &lt;enumeration value="ST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "terminalType", namespace = "http://www.secunet.com")
@XmlEnum
public enum TerminalType {

    AT,
    IS,
    ST;

    public String value() {
        return name();
    }

    public static TerminalType fromValue(String v) {
        return valueOf(v);
    }

}
