package org.openecomp.sdc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.Metadata;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.parameters.Input;

public class ToscaParserStubsTest {

	private static final String VF_CUSTOMIZATION_UUID = "56179cd8-de4a-4c38-919b-bbc4452d2d73";
	static SdcToscaParserFactory factory;
	static ISdcCsarHelper rainyCsarHelperSingleVf;
	static ISdcCsarHelper rainyCsarHelperMultiVfs;
	static ISdcCsarHelper rainyCsarHelperNoVf;
	static ISdcCsarHelper fdntCsarHelper;

	@BeforeClass
	public static void init() throws SdcToscaParserException{
		long startTime = System.currentTimeMillis();
		factory = SdcToscaParserFactory.getInstance();
		long estimatedTime = System.currentTimeMillis() - startTime; 
		System.out.println("Time to init factory "+estimatedTime);
		String fileStr2 = ToscaParserStubsTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-0904-2.csar").getFile();
		File file2 = new File(fileStr2);
		startTime = System.currentTimeMillis();
		fdntCsarHelper = factory.getSdcCsarHelper(file2.getAbsolutePath());
		estimatedTime = System.currentTimeMillis() - startTime;  
		System.out.println("init CSAR Execution time: "+estimatedTime);
		String fileStr = ToscaParserStubsTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-rainy.csar").getFile();
		File file = new File(fileStr);
		rainyCsarHelperMultiVfs = factory.getSdcCsarHelper(file.getAbsolutePath());
		String fileStr3 = ToscaParserStubsTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar.csar").getFile();
		File file3 = new File(fileStr3);
		rainyCsarHelperSingleVf = factory.getSdcCsarHelper(file3.getAbsolutePath());
		/*String fileStr4 = ToscaParserStubsTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-no-vf.csar").getFile();
		File file4 = new File(fileStr4);
		rainyCsarHelperNoVf = factory.getSdcCsarHelper(file4.getAbsolutePath());*/
	}
		
	@Test
	public void testNumberOfVfSunnyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	public void testNodeTemplateFlatProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertEquals("2", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals("3", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_naming_code"));
	}
	
	@Test
	public void testGroupFlatProperty() throws SdcToscaParserException {
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		String volumeGroup = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "volume_group");
		assertEquals("false", volumeGroup);
	}

	@Test
	public void testServiceVl(){
		List<NodeTemplate> vlList = fdntCsarHelper.getServiceVlList();
		assertEquals(1, vlList.size());
		assertEquals("exVL", vlList.get(0).getName());
	}
	
	@Test
	public void testNodeTemplateNestedProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = fdntCsarHelper.getServiceVlList();
		NodeTemplate nodeTemplate = serviceVlList.get(0);
		System.out.println("node template "+nodeTemplate.toString());
		assertEquals("24", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv4_subnet_default_assignment#cidr_mask"));
		assertEquals("7a6520b-9982354-ee82992c-105720", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_flows#vpn_binding"));

	}

	@Test
	public void testServiceNodeTemplatesByType() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Fdnt");
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	public void testGetTypeOfNodeTemplate() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		String typeOfNodeTemplate = fdntCsarHelper.getTypeOfNodeTemplate(serviceVfList.get(0));
		assertEquals("org.openecomp.resource.vf.Fdnt", typeOfNodeTemplate);
	}

	@Test
	public void testGetServiceMetadata() {
		Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
		assertNotNull(serviceMetadata);
		assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", serviceMetadata.getValue("invariantUUID"));
		assertEquals("Service FDNT", serviceMetadata.getValue("name"));
		assertEquals("true", String.valueOf(serviceMetadata.getValue("serviceEcompNaming")));
	}

	@Test
	public void testGetAllottedResources() {
		List<NodeTemplate> allottedResources = fdntCsarHelper.getAllottedResources();
		assertEquals(0, allottedResources.size());
	}

	@Test
	public void testGetServiceSubstitutionMappingsTypeName() {
		String serviceSubstitutionMappingsTypeName = fdntCsarHelper.getServiceSubstitutionMappingsTypeName();
		assertEquals("org.openecomp.service.ServiceFdnt", serviceSubstitutionMappingsTypeName);
	}
	
	@Test
	public void testGetVfcFromVf(){
		List<NodeTemplate> vfcListByVf = fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfcListByVf.size());
	}
	
	@Test
	public void testGetCpFromVf(){
		List<NodeTemplate> cpListByVf = fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(1, cpListByVf.size());
		NodeTemplate nodeTemplate = cpListByVf.get(0);
		assertEquals("DNT_PORT", nodeTemplate.getName());
	}
	
	@Test
	public void testVfModulesFromVf(){
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfModulesByVf.size());
		for (Group group : vfModulesByVf){
			assertTrue(group.getName().startsWith("fdnt1"));
			assertNotNull(group.getMetadata());
			assertNotNull(group.getMetadata().getValue("vfModuleCustomizationUUID"));
		}
	}
	
	@Test
	public void testGetNodeTemplatePairsByReqName(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(1, nodeTemplatePairsByReqName.size());
		Pair<NodeTemplate, NodeTemplate> pair = nodeTemplatePairsByReqName.get(0);
		NodeTemplate cp = pair.getLeft();
		NodeTemplate vfc = pair.getRight();
		assertEquals("DNT_PORT", cp.getName());
		assertEquals("DNT_FW_RHRG", vfc.getName());
	}
	
	@Test
	public void testGetMembersOfVfModule(){
		NodeTemplate vf = fdntCsarHelper.getServiceVfList().get(0);
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfModulesByVf.size());
		for (Group group : vfModulesByVf){
			List<NodeTemplate> membersOfVfModule = fdntCsarHelper.getMembersOfVfModule(vf, group);
			assertNotNull(membersOfVfModule);
			if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")){
				assertEquals(1, membersOfVfModule.size());
				NodeTemplate nodeTemplate = membersOfVfModule.get(0);
				assertEquals("DNT_FW_RSG_SI_1", nodeTemplate.getName());
			} else {
				assertEquals("fdnt1..Fdnt..mod_vmsi_dnt_fw_parent..module-1", group.getName());
				assertEquals(1, membersOfVfModule.size());
				NodeTemplate nodeTemplate = membersOfVfModule.get(0);
				assertEquals("DNT_FW_RHRG", nodeTemplate.getName());
			}
		}
	}
	
	@Test
	public void testGetServiceInputs(){
		List<Input> serviceInputs = fdntCsarHelper.getServiceInputs();
		assertNotNull(serviceInputs);
		assertEquals(1, serviceInputs.size());
	}

	@Test
	public void testGetMetadataProperty(){
		Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
		String metadataPropertyValue = fdntCsarHelper.getMetadataPropertyValue(serviceMetadata, "invariantUUID");
		assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", metadataPropertyValue);
	}

	@Test
	public void testGetGroupMetadata(){
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
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
	public void testGetServiceInputLeafValue(){
		String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
		assertEquals("test service naming", serviceInputLeafValue);
	}

	
	/***************** RAINY TESTS ***************************/
	
	
	@Test
	public void testGetServiceInputLeafValueNotExists(){
		String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default#kuku");
		assertNull(serviceInputLeafValue);
	}

	@Test
	public void testGetServiceInputLeafValueNull(){
		String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault(null);
		assertNull(serviceInputLeafValue);
	}
	
	@Test
	public void testNodeTemplateNestedPropertyNotExists() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		String nodeTemplatePropertyLeafValue = fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_role#nf_naming#kuku");
		assertNull(nodeTemplatePropertyLeafValue);
	}
	
	@Test
	public void testGetGroupEmptyMetadata(){
		List<Group> vfModulesByVf = rainyCsarHelperMultiVfs.getVfModulesByVf("56179cd8-de4a-4c38-919b-bbc4452d2d72");
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
	public void testNodeTemplateFlatPropertyByNotFoundProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "XXXX");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), null);
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullNodeTemplate() throws SdcToscaParserException {
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(null, "availability_zone_max_count");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNumberOfVLRainyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = rainyCsarHelperMultiVfs.getServiceVlList();
		assertNotNull(serviceVlList);
		assertEquals(0, serviceVlList.size());
	}

	@Test
	public void testSingleVFWithNotMetadata() throws SdcToscaParserException {
		//If there is no metadata on VF level - There is no VF's because the type is taken from metadata values.
		List<NodeTemplate> serviceVfList = rainyCsarHelperSingleVf.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(0, serviceVfList.size());
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

	@Test
	public void testGetCpFromVfByNullId() {
		List<NodeTemplate> cpListByVf = rainyCsarHelperMultiVfs.getCpListByVf(null);
		assertNotNull(cpListByVf);
		assertEquals(0, cpListByVf.size());
	}

    @Test
    public void testGetAllottedResourcesZero() {
        List<NodeTemplate> allottedResources = rainyCsarHelperMultiVfs.getAllottedResources();
        assertNotNull(allottedResources);
        assertEquals(0, allottedResources.size());
    }

    @Test
    public void testGetTypeOfNullNodeTemplate() {
        String typeOfNodeTemplate = rainyCsarHelperMultiVfs.getTypeOfNodeTemplate(null);
        assertNull(typeOfNodeTemplate);
    }

    @Test
    public void testGetCpFromVfXxx() {
        List<NodeTemplate> cpListByVf = rainyCsarHelperMultiVfs.getCpListByVf("XXXXX");
        assertNotNull(cpListByVf);
        assertEquals(0, cpListByVf.size());
    }

    @Test
    public void testServiceNodeTemplatesByNull() {
        List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType(null);
        assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
    }

    @Test
    public void testServiceNodeTemplatesByNotFoundProperty() {
        List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType("XXX");
		assertNotNull(nodeTemplates);
        assertEquals(0, nodeTemplates.size());
    }

    @Test
    public void testVfcListByNull() {
		List<NodeTemplate> vfcList = rainyCsarHelperMultiVfs.getVfcListByVf(null);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
    }

	@Test
	public void testVfcListByNotFoundProperty() {
		List<NodeTemplate> vfcList = rainyCsarHelperMultiVfs.getVfcListByVf("XXX");
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}

	@Test
    public void testServiceSubstitutionMappingsTypeName() {
        String substitutionMappingsTypeName = rainyCsarHelperMultiVfs.getServiceSubstitutionMappingsTypeName();
        assertNull(substitutionMappingsTypeName);
    }

    @Test
    public void testServiceMetadata() {
		Metadata metadata = rainyCsarHelperSingleVf.getServiceMetadata();
		assertNull(metadata);
	}
    
    @Test
    public void testGetVfModuleNonExisitingVf() {
		List<Group> vfModulesByVf = rainyCsarHelperSingleVf.getVfModulesByVf("dummy");
		assertNotNull(vfModulesByVf);
		assertEquals(0, vfModulesByVf.size());
	}

	@Test
	public void testGetVfModuleNullVf() {
		List<Group> vfModulesByVf = rainyCsarHelperSingleVf.getVfModulesByVf(null);
		assertNotNull(vfModulesByVf);
		assertEquals(0, vfModulesByVf.size());
	}

	@Test
	public void testGroupPropertyLeafValueByNullGroup() {
		String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(null, "volume_group");
		assertNull(groupProperty);
	}

	@Test
	public void testGroupPropertyLeafValueByNullProperty() {
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), null);
		assertNull(groupProperty);
	}

	@Test
	public void testGroupPropertyLeafValueByDummyProperty() {
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "XXX");
		assertNull(groupProperty);
	}

	@Test
	public void testMembersOfVfModuleByNullVf() {
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		List<NodeTemplate> nodeTemplates = fdntCsarHelper.getMembersOfVfModule(null, vfModulesByVf.get(0));
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testMembersOfVfModuleByNullGroup() {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getMembersOfVfModule(serviceVfList.get(0), null);
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullVF(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				null, fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyVF(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				new ArrayList<>(), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullCap(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), null, "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyCap(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), new ArrayList<>(), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullReq(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), null);
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithDummyReq(){
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "XXX");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testServiceInputs() {
		List<Input> inputs = rainyCsarHelperSingleVf.getServiceInputs();
		assertNotNull(inputs);
		assertEquals(0, inputs.size());
	}

	//TODO restore the test - the CSAR without VF is failing Tosca parser
	/*@Test
	public void testServiceWithoutVF() {
		List<NodeTemplate> vfList = rainyCsarHelperNoVf.getServiceVfList();
		assertNotNull(vfList);
		assertEquals(0, vfList.size());
	}*/

    @AfterClass
	public static void close(){
		long startTime = System.currentTimeMillis();
		factory.close();
		long estimatedTime = System.currentTimeMillis() - startTime; 
		System.out.println("close Execution time: "+estimatedTime);
	}
}
