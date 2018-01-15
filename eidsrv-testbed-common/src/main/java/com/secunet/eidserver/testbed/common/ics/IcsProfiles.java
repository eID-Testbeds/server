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
 * The lists of profiles that are supported by the test candidate.
 * 
 * <p>Java-Klasse für ics.profiles complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.profiles">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="MandatoryProfile" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.mandatoryprofile" maxOccurs="9"/>
 *         &lt;element name="OptionalProfile" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.optionalprofile" maxOccurs="10" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.profiles", propOrder = {
    "mandatoryProfileOrOptionalProfile"
})
public class IcsProfiles {

    @XmlElements({
        @XmlElement(name = "MandatoryProfile", type = IcsMandatoryprofile.class),
        @XmlElement(name = "OptionalProfile", type = IcsOptionalprofile.class)
    })
    protected List<Object> mandatoryProfileOrOptionalProfile;

    /**
     * Gets the value of the mandatoryProfileOrOptionalProfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mandatoryProfileOrOptionalProfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMandatoryProfileOrOptionalProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsMandatoryprofile }
     * {@link IcsOptionalprofile }
     * 
     * 
     */
    public List<Object> getMandatoryProfileOrOptionalProfile() {
        if (mandatoryProfileOrOptionalProfile == null) {
            mandatoryProfileOrOptionalProfile = new ArrayList<Object>();
        }
        return this.mandatoryProfileOrOptionalProfile;
    }

}
