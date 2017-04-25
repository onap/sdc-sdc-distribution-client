package org.openecomp.sdc.impl;

import org.junit.Test;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.Group;
import org.openecomp.sdc.toscaparser.elements.Metadata;

import java.util.List;

import static org.junit.Assert.*;

public class ToscaParserGroupTest extends BasicTest{

    //region getVfModulesByVf
    @Test
    public void testVfModulesFromVf(){
        List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
        assertEquals(2, vfModulesByVf.size());
        for (Group group : vfModulesByVf){
            assertTrue(group.getName().startsWith("fdnt1"));
            assertNotNull(group.getMetadata());
            assertNotNull(group.getMetadata().getValue("vfModuleCustomizationUUID"));
        }
    }

    @Test
    public void testGetGroupMetadata(){
        List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
        boolean found = false;
        for (Group group : vfModulesByVf){
            if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")){
                found = true;
                Metadata metadata = group.getMetadata();
                assertNotNull(metadata);
                assertEquals("b458f4ef-ede2-403d-9605-d08c9398b6ee", metadata.getValue("vfModuleModelCustomizationUUID"));
            }
        }
        assertTrue(found);
    }

    @Test
    public void testGetGroupEmptyMetadata(){
        List<Group> vfModulesByVf = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getVfModulesByVf("56179cd8-de4a-4c38-919b-bbc4452d2d72");
        boolean found = false;
        for (Group group : vfModulesByVf){
            if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")){
                found = true;
                Metadata metadata = group.getMetadata();
                assertNull(metadata);
            }
        }
        assertTrue(found);
    }

    @Test
    public void testGetVfModuleNonExisitingVf() {
        List<Group> vfModulesByVf = ToscaParserTestSuite.rainyCsarHelperSingleVf.getVfModulesByVf("dummy");
        assertNotNull(vfModulesByVf);
        assertEquals(0, vfModulesByVf.size());
    }

    @Test
    public void testGetVfModuleNullVf() {
        List<Group> vfModulesByVf = ToscaParserTestSuite.rainyCsarHelperSingleVf.getVfModulesByVf(null);
        assertNotNull(vfModulesByVf);
        assertEquals(0, vfModulesByVf.size());
    }
    //endregion

    //region getGroupPropertyLeafValue
    @Test
    public void testGroupFlatProperty() throws SdcToscaParserException {
        List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
        String volumeGroup = ToscaParserTestSuite.fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "volume_group");
        assertEquals("false", volumeGroup);
    }

    @Test
    public void testGroupPropertyLeafValueByNullProperty() {
        List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
        String groupProperty = ToscaParserTestSuite.fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), null);
        assertNull(groupProperty);
    }

    @Test
    public void testGroupPropertyLeafValueByDummyProperty() {
        List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
        String groupProperty = ToscaParserTestSuite.fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "XXX");
        assertNull(groupProperty);
    }

    @Test
    public void testGroupPropertyLeafValueByNullGroup() {
        String groupProperty = ToscaParserTestSuite.fdntCsarHelper.getGroupPropertyLeafValue(null, "volume_group");
        assertNull(groupProperty);
    }
    //endregion

}
