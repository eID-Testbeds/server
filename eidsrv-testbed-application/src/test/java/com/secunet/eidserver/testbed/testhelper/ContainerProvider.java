package com.secunet.eidserver.testbed.testhelper;

import javax.ejb.embeddable.EJBContainer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class ContainerProvider {
    private static final EJBContainer container;

    static {
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
                "./src/test/resources/glassfish");
        properties.put(EJBContainer.MODULES, new File("target/classes"));

        container = EJBContainer.createEJBContainer(properties);
    }

    public static EJBContainer getContainer() {
        return container;
    }
}
