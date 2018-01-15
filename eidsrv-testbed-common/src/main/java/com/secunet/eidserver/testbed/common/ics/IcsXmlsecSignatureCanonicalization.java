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
 * <p>Java-Klasse für ics.xmlsec.signature.canonicalization.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ics.xmlsec.signature.canonicalization">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
 *     &lt;enumeration value="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"/>
 *     &lt;enumeration value="http://www.w3.org/2006/12/xml-c14n11"/>
 *     &lt;enumeration value="http://www.w3.org/2006/12/xml-c14n11#WithComments"/>
 *     &lt;enumeration value="http://www.w3.org/2001/10/xml-exc-c14n#"/>
 *     &lt;enumeration value="http://www.w3.org/2001/10/xml-exc-c14n#WithComments"/>
 *     &lt;enumeration value="http://www.w3.org/2010/10/xml-c14n2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ics.xmlsec.signature.canonicalization")
@XmlEnum
public enum IcsXmlsecSignatureCanonicalization {

    @XmlEnumValue("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")
    HTTP_WWW_W_3_ORG_TR_2001_REC_XML_C_14_N_20010315("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"),
    @XmlEnumValue("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")
    HTTP_WWW_W_3_ORG_TR_2001_REC_XML_C_14_N_20010315_WITH_COMMENTS("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"),
    @XmlEnumValue("http://www.w3.org/2006/12/xml-c14n11")
    HTTP_WWW_W_3_ORG_2006_12_XML_C_14_N_11("http://www.w3.org/2006/12/xml-c14n11"),
    @XmlEnumValue("http://www.w3.org/2006/12/xml-c14n11#WithComments")
    HTTP_WWW_W_3_ORG_2006_12_XML_C_14_N_11_WITH_COMMENTS("http://www.w3.org/2006/12/xml-c14n11#WithComments"),
    @XmlEnumValue("http://www.w3.org/2001/10/xml-exc-c14n#")
    HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N("http://www.w3.org/2001/10/xml-exc-c14n#"),
    @XmlEnumValue("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")
    HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N_WITH_COMMENTS("http://www.w3.org/2001/10/xml-exc-c14n#WithComments"),
    @XmlEnumValue("http://www.w3.org/2010/10/xml-c14n2")
    HTTP_WWW_W_3_ORG_2010_10_XML_C_14_N_2("http://www.w3.org/2010/10/xml-c14n2");
    private final String value;

    IcsXmlsecSignatureCanonicalization(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IcsXmlsecSignatureCanonicalization fromValue(String v) {
        for (IcsXmlsecSignatureCanonicalization c: IcsXmlsecSignatureCanonicalization.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
