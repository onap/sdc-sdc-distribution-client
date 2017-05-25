package org.openecomp.sdc.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.common.JToscaException;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class SdcToscaParserBasicTest {

    public static final String VF_CUSTOMIZATION_UUID = "56179cd8-de4a-4c38-919b-bbc4452d2d73";
    static SdcToscaParserFactory factory;
    static ISdcCsarHelper rainyCsarHelperSingleVf;
    static ISdcCsarHelper rainyCsarHelperMultiVfs;
    static ISdcCsarHelper fdntCsarHelper;
    static ISdcCsarHelper complexCps;
    static ISdcCsarHelper fdntCsarHelperWithInputs;
    static Map<String, HashMap<String, List<String>>> fdntCsarHelper_Data;
    
    @BeforeClass
    public static void init() throws SdcToscaParserException, JToscaException, IOException {

        factory = SdcToscaParserFactory.getInstance();
        fdntCsarHelper = getCsarHelper("csars/service-ServiceFdnt-with-allotted.csar");
        rainyCsarHelperMultiVfs = getCsarHelper("csars/service-ServiceFdnt-csar-rainy.csar");
        rainyCsarHelperSingleVf = getCsarHelper("csars/service-ServiceFdnt-csar.csar");
        complexCps = getCsarHelper("csars/service-Renanatst2-csar.csar");
		fdntCsarHelperWithInputs = getCsarHelper("csars/service-ServiceFdnt-with-get-input.csar");

        fdntCsarHelper_Data = new HashMap<String, HashMap<String, List<String>>>(){
    		{
    			HashMap<String, List<String>> FDNT ;
    			
    			FDNT = new HashMap<String, List<String>>();
    			FDNT.put("VF Name", Arrays.asList("FDNT 1"));
    			FDNT.put("capabilities", Arrays.asList(
    					"dnt_fw_rhrg.binding_DNT_FW_INT_DNS_TRUSTED_RVMI",
    					"dnt_fw_rhrg.host_DNT_FW_SERVER",
    					"dnt_fw_rhrg.binding_DNT_FW_CORE_DIRECT_RVMI",
    					"dnt_fw_rhrg.scalable_DNT_FW_SERVER",
    					"dnt_fw_rhrg.endpoint_DNT_FW_SERVER",
    					"dnt_fw_rhrg.binding_DNT_FW_INTERNET_DNS_DIRECT_RVMI",					
      					"dnt_fw_rhrg.os_DNT_FW_SERVER",
      					"dnt_fw_rhrg.feature",
      					"dnt_fw_rhrg.binding_DNT_FW_OAM_PROTECTED_RVMI",
      					"dnt_fw_rhrg.binding_DNT_FW_SERVER",
      					"dnt_fw_rhrg.binding_DNT_FW_NIMBUS_HSL_RVMI",
      					"dnt_fw_rsg_si_1.feature"));
    			FDNT.put("requirements", Arrays.asList(
    					"DNT_FW_RSG_SI_1.dependency",
    					"DNT_FW_RHRG.dependency",
    					"DNT_FW_RHRG.link_DNT_FW_INTERNET_DNS_DIRECT_RVMI",
      					"DNT_FW_RHRG.link_DNT_FW_CORE_DIRECT_RVMI",
      					"DNT_FW_RHRG.link_DNT_FW_OAM_PROTECTED_RVMI",
      					"DNT_FW_RHRG.link_DNT_FW_INT_DNS_TRUSTED_RVMI", 
      					"DNT_FW_RHRG.link_DNT_FW_NIMBUS_HSL_RVMI",
      					"DNT_FW_RSG_SI_1.port",
      					"DNT_FW_RHRG.local_storage_DNT_FW_SERVER"));
    			FDNT.put("capabilitiesTypes", Arrays.asList(
    					"tosca.capabilities.network.Bindable",
    					"tosca.capabilities.OperatingSystem",
    					"tosca.capabilities.network.Bindable",					
      					"tosca.capabilities.Scalable",
      					"tosca.capabilities.Endpoint.Admin",
      					"tosca.capabilities.network.Bindable",
      					"tosca.capabilities.network.Bindable",
      					"tosca.capabilities.network.Bindable",
      					"tosca.capabilities.Node",
      					"tosca.capabilities.Container",
      					"tosca.nodes.SoftwareComponent",
      					"tosca.capabilities.network.Bindable"));
    			FDNT.put("capabilityProperties", Arrays.asList(
    					"dnt_fw_rhrg.binding_DNT_FW_INT_DNS_TRUSTED_RVMI:none",
    					"dnt_fw_rhrg.host_DNT_FW_SERVER:num_cpus,integer,false;",
    					"dnt_fw_rhrg.binding_DNT_FW_CORE_DIRECT_RVMI",
    					"dnt_fw_rhrg.scalable_DNT_FW_SERVER",
    					"dnt_fw_rhrg.endpoint_DNT_FW_SERVER",
    					"dnt_fw_rhrg.binding_DNT_FW_INTERNET_DNS_DIRECT_RVMI",					
      					"dnt_fw_rhrg.os_DNT_FW_SERVER",
      					"dnt_fw_rhrg.feature",
      					"dnt_fw_rhrg.binding_DNT_FW_OAM_PROTECTED_RVMI",
      					"dnt_fw_rhrg.binding_DNT_FW_SERVER",
      					"dnt_fw_rhrg.binding_DNT_FW_NIMBUS_HSL_RVMI",
      					"dnt_fw_rsg_si_1.feature"));
    			
			
    			put("FDNT", FDNT);			
    		}
    	};
    };

	protected static ISdcCsarHelper getCsarHelper(String path) throws SdcToscaParserException {
		System.out.println("Parsing CSAR "+path+"...");
		String fileStr1 = SdcToscaParserBasicTest.class.getClassLoader().getResource(path).getFile();
        File file1 = new File(fileStr1);
        ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());
		return sdcCsarHelper;
	}
    
    @BeforeMethod
    public void setupTest(Method method) {
        System.out.println("#### Starting Test " + method.getName() + " ###########");
    }

    @AfterMethod
    public void tearDown(Method method){
        System.out.println("#### Ended test " + method.getName() + " ###########");
    }
}
