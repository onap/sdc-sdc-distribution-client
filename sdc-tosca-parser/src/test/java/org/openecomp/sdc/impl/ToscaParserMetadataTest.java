package org.openecomp.sdc.impl;

import org.junit.Test;
import org.openecomp.sdc.toscaparser.elements.Metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ToscaParserMetadataTest extends BasicTest {

    //region getServiceMetadata
    @Test
    public void testGetServiceMetadata() {
        Metadata serviceMetadata = ToscaParserTestSuite.fdntCsarHelper.getServiceMetadata();
        assertNotNull(serviceMetadata);
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", serviceMetadata.getValue("invariantUUID"));
        assertEquals("Service FDNT", serviceMetadata.getValue("name"));
        assertEquals("true", String.valueOf(serviceMetadata.getValue("serviceEcompNaming")));
    }

    @Test
    public void testServiceMetadata() {
        Metadata metadata = ToscaParserTestSuite.rainyCsarHelperSingleVf.getServiceMetadata();
        assertNull(metadata);
    }
    //endregion

    //region getMetadataPropertyValue
    @Test
    public void testGetMetadataProperty(){
        Metadata serviceMetadata = ToscaParserTestSuite.fdntCsarHelper.getServiceMetadata();
        String metadataPropertyValue = ToscaParserTestSuite.fdntCsarHelper.getMetadataPropertyValue(serviceMetadata, "invariantUUID");
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", metadataPropertyValue);
    }

    @Test
    public void testGetNullMetadataPropertyValue() {
        String value = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getMetadataPropertyValue(null, "XXX");
        assertNull(value);
    }

    @Test
    public void testGetMetadataByNullPropertyValue() {
        Metadata metadata = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, null);
        assertNull(value);
    }

    @Test
    public void testGetMetadataByEmptyPropertyValue() {
        Metadata metadata =  ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, "");
        assertNull(value);
    }
    //endregion

}
