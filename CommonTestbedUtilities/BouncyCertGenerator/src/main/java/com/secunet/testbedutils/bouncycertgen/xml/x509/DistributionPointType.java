
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r distributionPointType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="distributionPointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="fullName" type="{http://www.secunet.com}GeneralNamesType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionPointType", propOrder = {
    "fullName"
})
public class DistributionPointType {

    protected GeneralNamesType fullName;

    /**
     * Ruft den Wert der fullName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GeneralNamesType }
     *     
     */
    public GeneralNamesType getFullName() {
        return fullName;
    }

    /**
     * Legt den Wert der fullName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneralNamesType }
     *     
     */
    public void setFullName(GeneralNamesType value) {
        this.fullName = value;
    }

}
