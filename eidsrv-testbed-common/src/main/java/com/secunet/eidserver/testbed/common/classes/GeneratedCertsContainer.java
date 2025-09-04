package com.secunet.eidserver.testbed.common.classes;

import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;

import java.util.Map;
import java.util.Set;

public class GeneratedCertsContainer {
    private Set<GeneratedCertificate> certs;
    private Set<CVCertificate> cvCerts;
    private byte[] masterList;
    private byte[] defectList;
    private Map<Character, byte[]> blackLists;

    private Map<String, byte[]> allFiles;

    public GeneratedCertsContainer(Set<GeneratedCertificate> certs, Set<CVCertificate> cvCerts, byte[] masterList, byte[] defectList, Map<Character, byte[]> blackLists, Map<String, byte[]> allFiles) {
        this.certs = certs;
        this.cvCerts = cvCerts;
        this.masterList = masterList;
        this.defectList = defectList;
        this.blackLists = blackLists;
        this.allFiles = allFiles;
    }

    public Set<GeneratedCertificate> getCerts() {
        return certs;
    }

    public Set<CVCertificate> getCvCerts() {
        return cvCerts;
    }

    public byte[] getMasterList() {
        return masterList;
    }

    public byte[] getDefectList() {
        return defectList;
    }

    public Map<Character, byte[]> getBlackLists() {
        return blackLists;
    }

    public Map<String, byte[]> getAllFiles() {
        return allFiles;
    }
}
