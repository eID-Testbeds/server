/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*

package com.secunet.eidserver.testbed.controller;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestProfile;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;

*/
/**
 * 
 *//*

public class ProfileControllerTest {
	
    @Test
    public void testCreateProfile() throws Exception {
        final ProfileControllerBean profileController = (ProfileControllerBean) ContainerProvider
                .getContainer().getContext().lookup(
                "java:global/classes/ProfileController");
        assertNotNull(profileController);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(
            new FileInputStream("./src/test/resources/ics_test.xml"), "UTF-8"));

        String xmlData = new String();
        
        try {
            String line;
            while((line = in.readLine()) != null) {
                xmlData = xmlData.concat(line);
            }
        } finally {
            in.close();
        }
        
        TestCandidate result = profileController.(xmlData);
        
        assertNotNull(result);
        assertNotNull(result.getXmlSignatureAlgorithms());
        assertNotNull(result.getCandidateUrl());
        assertNotNull(result.getName());
        assertTrue(result.getXmlSignatureAlgorithms().size() > 0);
    }
    
    @Test
    public void testReuseExistingTests() throws Exception {
        final ProfileControllerBean profileController = (ProfileControllerBean) ContainerProvider
                .getContainer().getContext().lookup(
                "java:global/classes/ProfileController");
        assertNotNull(profileController);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(
            new FileInputStream("./src/test/resources/ics_test.xml"), "UTF-8"));

        String xmlData = new String();
        
        try {
            String line;
            while((line = in.readLine()) != null) {
                xmlData = xmlData.concat(line);
            }
        } finally {
            in.close();
        }  
        
        TestProfile result = profileController.createProfile(xmlData);
        assertNotNull(result);
        
        Set<TestCase> tcases = profileController.loadDefaultTestcases(result);
        // check if any testcases have been generated previously and are found now
        assertTrue(tcases.size() > 0);
    }
}
*/
