package org.openecomp.sdc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;

public class ToscaParserNodeTemplateTest extends BasicTest {

	//region getServiceVfList
	@Test
	public void testNumberOfVfSunnyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(2, serviceVfList.size());
	}

	@Test
	public void testSingleVFWithNotMetadata() throws SdcToscaParserException {
		//If there is no metadata on VF level - There is no VF's because the type is taken from metadata values.
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.rainyCsarHelperSingleVf.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(0, serviceVfList.size());
	}
	//endregion

	//region getNodeTemplatePropertyLeafValue
	@Test
	public void testNodeTemplateFlatProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		assertEquals("2", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals("3", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_naming_code"));
	}

	@Test
	public void testNodeTemplateNestedProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = ToscaParserTestSuite.fdntCsarHelper.getServiceVlList();
		NodeTemplate nodeTemplate = serviceVlList.get(0);
		//System.out.println("node template " + nodeTemplate.toString());
		assertEquals("24", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv4_subnet_default_assignment#cidr_mask"));
		assertEquals("7a6520b-9982354-ee82992c-105720", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_flows#vpn_binding"));
	}
	
	@Test
	public void testNodeTemplateNestedPropertyFromInput() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		NodeTemplate nodeTemplate = serviceVfList.get(0);
		//System.out.println("node template " + nodeTemplate.toString());
		assertEquals("true", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "nf_naming#ecomp_generated_naming"));
		assertEquals("FDNT_instance_VF_2", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "nf_naming#naming_policy"));
	}

	@Test
	public void testNodeTemplateNestedPropertyNotExists() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		String nodeTemplatePropertyLeafValue = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_role#nf_naming#kuku");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNotFoundProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "XXXX");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), null);
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullNodeTemplate() throws SdcToscaParserException {
		String nodeTemplatePropertyLeafValue = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(null, "availability_zone_max_count");
		assertNull(nodeTemplatePropertyLeafValue);
	}
	//endregion

	//region getServiceVlList
	@Test
	public void testServiceVl() {
		List<NodeTemplate> vlList = ToscaParserTestSuite.fdntCsarHelper.getServiceVlList();
		assertEquals(1, vlList.size());
		assertEquals("exVL", vlList.get(0).getName());
	}

	@Test
	public void testNumberOfVLRainyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceVlList();
		assertNotNull(serviceVlList);
		assertEquals(0, serviceVlList.size());
	}
	//endregion

	//region getServiceNodeTemplatesByType
	@Test
	public void testServiceNodeTemplatesByType() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Fdnt");
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	public void testServiceNodeTemplatesByNull() {
		List<NodeTemplate> nodeTemplates = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType(null);
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testServiceNodeTemplatesByNotFoundProperty() {
		List<NodeTemplate> nodeTemplates = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType("XXX");
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}
	//endregion

	//region getTypeOfNodeTemplate
	@Test
	public void testGetTypeOfNodeTemplate() {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		String typeOfNodeTemplate = ToscaParserTestSuite.fdntCsarHelper.getTypeOfNodeTemplate(serviceVfList.get(0));
		assertEquals("org.openecomp.resource.vf.Fdnt", typeOfNodeTemplate);
	}

	@Test
	public void testGetTypeOfNullNodeTemplate() {
		String typeOfNodeTemplate = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getTypeOfNodeTemplate(null);
		assertNull(typeOfNodeTemplate);
	}
	//endregion

	//region getAllottedResources
	@Test
	public void testGetAllottedResources() {
		List<NodeTemplate> allottedResources = ToscaParserTestSuite.fdntCsarHelper.getAllottedResources();
		assertEquals(1, allottedResources.size());
	}

	@Test
	public void testGetAllottedResourcesZero() {
		List<NodeTemplate> allottedResources = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getAllottedResources();
		assertNotNull(allottedResources);
		assertEquals(0, allottedResources.size());
	}
	//endregion

	//region getVfcListByVf
	@Test
	public void testGetVfcFromVf() {
		List<NodeTemplate> vfcListByVf = ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfcListByVf.size());
	}

	@Test
	public void testVfcListByNull() {
		List<NodeTemplate> vfcList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getVfcListByVf(null);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}

	@Test
	public void testVfcListByNotFoundProperty() {
		List<NodeTemplate> vfcList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getVfcListByVf("XXX");
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}
	//endregion

	//region getCpListByVf
	@Test
	public void testGetCpFromVf() {
		List<NodeTemplate> cpListByVf = ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
		assertEquals(1, cpListByVf.size());
		NodeTemplate nodeTemplate = cpListByVf.get(0);
		assertEquals("DNT_PORT", nodeTemplate.getName());
	}

	@Test
	public void testGetCpFromVfByNullId() {
		List<NodeTemplate> cpListByVf = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getCpListByVf(null);
		assertNotNull(cpListByVf);
		assertEquals(0, cpListByVf.size());
	}

	@Test
	public void testGetCpFromVfXxx() {
		List<NodeTemplate> cpListByVf = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getCpListByVf("XXXXX");
		assertNotNull(cpListByVf);
		assertEquals(0, cpListByVf.size());
	}
	//endregion

	//region getNodeTemplatePairsByReqName
	@Test
	public void testGetNodeTemplatePairsByReqName() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(1, nodeTemplatePairsByReqName.size());
		Pair<NodeTemplate, NodeTemplate> pair = nodeTemplatePairsByReqName.get(0);
		NodeTemplate cp = pair.getLeft();
		NodeTemplate vfc = pair.getRight();
		assertEquals("DNT_PORT", cp.getName());
		assertEquals("DNT_FW_RHRG", vfc.getName());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullVF() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				null, ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyVF() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				new ArrayList<>(), ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullCap() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), null, "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyCap() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), new ArrayList<>(), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullReq() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), null);
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithDummyReq() {

		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePairsByReqName(
				ToscaParserTestSuite.fdntCsarHelper.getCpListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), ToscaParserTestSuite.fdntCsarHelper.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID), "XXX");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}
	//endregion

	//region getMembersOfVfModule
	@Test
	public void testGetMembersOfVfModule() {
		NodeTemplate vf = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList().get(0);
		List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfModulesByVf.size());
		for (Group group : vfModulesByVf) {
			List<NodeTemplate> membersOfVfModule = ToscaParserTestSuite.fdntCsarHelper.getMembersOfVfModule(vf, group);
			assertNotNull(membersOfVfModule);
			if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")) {
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
	public void testMembersOfVfModuleByNullVf() {
		List<Group> vfModulesByVf = ToscaParserTestSuite.fdntCsarHelper.getVfModulesByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
		List<NodeTemplate> nodeTemplates = ToscaParserTestSuite.fdntCsarHelper.getMembersOfVfModule(null, vfModulesByVf.get(0));
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testMembersOfVfModuleByNullGroup() {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getServiceVfList();
		List<NodeTemplate> nodeTemplates = ToscaParserTestSuite.rainyCsarHelperMultiVfs.getMembersOfVfModule(serviceVfList.get(0), null);
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}
	//endregion

	//region getCpPropertiesFromVfc
	@Test
	public void testGetCpPropertiesFromVfc() {
		List<NodeTemplate> vfcs = ToscaParserTestSuite.complexCps.getVfcListByVf(ToscaParserTestSuite.VF_CUSTOMIZATION_UUID);
		Map<String, Map<String, Object>> cps = ToscaParserTestSuite.complexCps.getCpPropertiesFromVfc(vfcs.get(0));

		assertEquals("1", cps.get("port_fe1_sigtran").get("ip_requirements#ip_count_required#count"));
		assertEquals("true", cps.get("port_fe1_sigtran").get("ip_requirements#dhcp_enabled"));
		assertEquals("4", cps.get("port_fe1_sigtran").get("ip_requirements#ip_version"));

		assertEquals("2", cps.get("port_fe_cluster").get("ip_requirements#ip_count_required#count"));
		assertEquals("true", cps.get("port_fe_cluster").get("ip_requirements#dhcp_enabled"));
		assertEquals("4", cps.get("port_fe_cluster").get("ip_requirements#ip_version"));
	}
	//endregion

	//region getNodeTemplatePropertyAsObject
	@Test
	public void testGetNodeTemplatePropertyAsObject() {
		List<NodeTemplate> serviceVfList = ToscaParserTestSuite.fdntCsarHelper.getServiceVfList();
		assertEquals("2", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals(3, ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", ToscaParserTestSuite.fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "nf_naming_code"));
	}
	//endregion

}
