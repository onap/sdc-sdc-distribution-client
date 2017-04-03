package org.openecomp.sdc.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;

public class ToscaParserStubsTest {

	private static ISdcCsarHelper csarHelper;
	private static SdcToscaParserFactory factory;


	@BeforeClass
	public static void init() throws SdcToscaParserException{
		factory = SdcToscaParserFactory.getInstance();
		//csarHelper = factory.getSdcCsarHelper("C:\\Users\\pa0916\\Desktop\\Work\\ASDC\\CSARs\\service-ServiceFdnt-csar-allotted-resources-4.csar");
		String fileStr = ToscaParserStubsTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-allotted-resources-4.csar").getFile();
		File file = new File(fileStr);
		csarHelper = factory.getSdcCsarHelper(file.getAbsolutePath());
	}


	@Test
	//TODO add rainy flows
	public void testNumberOfVfSunnyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = csarHelper.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	//TODO add rainy flows
	public void testNodeTemplateFlatProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = csarHelper.getServiceVfList();
		String nodeTemplatePropertyLeafValue = csarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "availability_zone_max_count");
		assertEquals("2", nodeTemplatePropertyLeafValue);
	}

	@Test
	//TODO add rainy flows
	public void testNodeTemplateNestedProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = csarHelper.getServiceVfList();
		String nodeTemplatePropertyLeafValue = csarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_role#nf_naming#instance_name");
		assertEquals("FDNT_instance_VF", nodeTemplatePropertyLeafValue);
	}

	@Test
	//TODO add rainy flows
	public void testServiceNodeTemplatesByType() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = csarHelper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Fdnt");
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	//TODO add rainy flows
	public void testGetTypeOfNodeTemplate() {
		List<NodeTemplate> serviceVfList = csarHelper.getServiceVfList();
		String typeOfNodeTemplate = csarHelper.getTypeOfNodeTemplate(serviceVfList.get(0));
		assertEquals("org.openecomp.resource.vf.Fdnt", typeOfNodeTemplate);
	}


	//@Test
	//TODO add rainy flows
	public void testGetServiceMetadata() {
		//FAILS!! Metadata is null
		Map<String, String> serviceMetadata = csarHelper.getServiceMetadata();
		assertNotNull(serviceMetadata);
		assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", serviceMetadata.get("invariantUUID"));
		assertEquals("Service FDNT", serviceMetadata.get("name"));
		assertEquals("true", serviceMetadata.get("serviceEcompNaming"));
	}

	@Test
	//TODO add rainy flows
	public void testGetAllottedResources() {
		List<NodeTemplate> allottedResources = csarHelper.getAllottedResources();
		assertEquals(1, allottedResources.size());
	}
	
	
		@AfterClass
		public static void close(){
			factory.close();
		}
	}
