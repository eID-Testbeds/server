package com.secunet.eidserver.testbed.common.classes;

import java.net.URL;
import java.security.Provider;

public class Singletons {
    public static final Provider BC_PROVIDER = new org.bouncycastle.jce.provider.BouncyCastleProvider();

    public static URL currentUrl = null;
}
