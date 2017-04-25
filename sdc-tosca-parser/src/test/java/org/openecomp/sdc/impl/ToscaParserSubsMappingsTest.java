package org.openecomp.sdc.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ToscaParserSubsMappingsTest extends BasicTest {

    //region getServiceSubstitutionMappingsTypeName
    @Test
    public void testGetServiceSubstitutionMappingsTypeName() {
        String serviceSubstitutionMappingsTypeName = ToscaParserTestSuite.fdntCsarHelper.getServiceSubstitutionMappingsTypeName();
        assertEquals("org.openecomp.service.ServiceFdnt", serviceSubstitutionMappingsTypeName);
    }

    @Test
    public void testServiceSubstitutionMappingsTypeName() {
        String substitutionMappingsTypeName = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceSubstitutionMappingsTypeName();
        assertNull(substitutionMappingsTypeName);
    }
    //endregion

}
