package org.openecomp.sdc.impl;

import org.testng.annotations.Test;
import org.openecomp.sdc.tosca.parser.config.Configuration;
import org.openecomp.sdc.tosca.parser.config.ConfigurationManager;

import java.io.IOException;

import static org.testng.Assert.assertNotNull;

public class ToscaParserConfigurationTest extends BasicTest {

    @Test
    public void testConfigurationConformanceLevel() throws IOException {
        Configuration config = ConfigurationManager.getInstance().getConfiguration();
        assertNotNull(config);
        assertNotNull(config.getConformanceLevel());
        assertNotNull(config.getConformanceLevel().getMaxVersion());
        assertNotNull(config.getConformanceLevel().getMinVersion());
    }
}
