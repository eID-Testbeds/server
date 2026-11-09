package com.secunet.eidserver.testbed.runner;

import com.secunet.eidserver.testbed.common.classes.EidCards;
import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.testing.CryptoFileTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCandidateTestingPojo;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public abstract class RunnerTestBase {

    protected static Set<CryptoFile> cryptoFiles;
    protected static TestCandidate testCandidate;
    @BeforeSuite
    public void setupSuite() throws IOException {
        GeneratedCertsContainer container = CryptoHelper.generateCertificatesAndKeys("123456".getBytes(),new URL("https://test.test.com/test"));
        cryptoFiles = CryptoHelper.fileMapToCryptoFiles(container.getAllFiles());
        testCandidate = new TestCandidateTestingPojo();
        testCandidate.setCryptoFiles(cryptoFiles);
        EidCards.initCards(testCandidate);
    }

}
