//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * The data type for the Chip Authentication.
 * 
 * <p>Java-Klasse für ics.crypto.ca complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.ca">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="2">
 *         &lt;element name="Algorithm" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.ca"/>
 *         &lt;element name="DomainParameter" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.ca.domainparams"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.ca", propOrder = {
    "algorithmOrDomainParameter"
})
public class IcsCryptoCa {

    @XmlElements({
        @XmlElement(name = "Algorithm", type = IcsCa.class),
        @XmlElement(name = "DomainParameter", type = IcsCaDomainparams.class)
    })
    protected List<Object> algorithmOrDomainParameter;

    /**
     * Gets the value of the algorithmOrDomainParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the algorithmOrDomainParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlgorithmOrDomainParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsCa }
     * {@link IcsCaDomainparams }
     * 
     * 
     */
    public List<Object> getAlgorithmOrDomainParameter() {
        if (algorithmOrDomainParameter == null) {
            algorithmOrDomainParameter = new ArrayList<Object>();
        }
        return this.algorithmOrDomainParameter;
    }

}
