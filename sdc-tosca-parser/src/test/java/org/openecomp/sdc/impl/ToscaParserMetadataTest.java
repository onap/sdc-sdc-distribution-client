package org.openecomp.sdc.impl;

import org.testng.annotations.Test;
import org.openecomp.sdc.toscaparser.api.elements.Metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ToscaParserMetadataTest extends SdcToscaParserBasicTest {

    //region getServiceMetadata
    @Test
    public void testGetServiceMetadata() {
        Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
        assertNotNull(serviceMetadata);
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", serviceMetadata.getValue("invariantUUID"));
        assertEquals("Service FDNT", serviceMetadata.getValue("name"));
        assertEquals("true", String.valueOf(serviceMetadata.getValue("serviceEcompNaming")));
    }

    @Test
    public void testServiceMetadata() {
        Metadata metadata = rainyCsarHelperSingleVf.getServiceMetadata();
        assertNull(metadata);
    }
    //endregion

    //region getMetadataPropertyValue
    @Test
    public void testGetMetadataProperty(){
        Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
        String metadataPropertyValue = fdntCsarHelper.getMetadataPropertyValue(serviceMetadata, "invariantUUID");
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", metadataPropertyValue);
    }

    @Test
    public void testGetNullMetadataPropertyValue() {
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(null, "XXX");
        assertNull(value);
    }

    @Test
    public void testGetMetadataByNullPropertyValue() {
        Metadata metadata = rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, null);
        assertNull(value);
    }

    @Test
    public void testGetMetadataByEmptyPropertyValue() {
        Metadata metadata =  rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, "");
        assertNull(value);
    }
    //endregion

    //region getConformanceLevel
    @Test
    public void testSunnyGetConformanceLevel() {
        String conformanceLevel = fdntCsarHelper.getConformanceLevel();
        assertNotNull(conformanceLevel);
        assertEquals("3.0", conformanceLevel);
    }
    //endregion

}
