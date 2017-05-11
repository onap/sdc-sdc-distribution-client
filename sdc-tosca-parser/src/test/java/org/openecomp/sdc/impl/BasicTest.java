package org.openecomp.sdc.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class BasicTest {

    public static final String VF_CUSTOMIZATION_UUID = "56179cd8-de4a-4c38-919b-bbc4452d2d73";
    static SdcToscaParserFactory factory;
    static ISdcCsarHelper rainyCsarHelperSingleVf;
    static ISdcCsarHelper rainyCsarHelperMultiVfs;
    static ISdcCsarHelper fdntCsarHelper;
    static Map<String, HashMap<String, List<String>>> fdntCsarHelper_Data;
    @BeforeSuite
    public static void init(ITestContext context) throws Exception {

        factory = SdcToscaParserFactory.getInstance();
        long startTime = System.currentTimeMillis();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to init factory " + estimatedTime);

        String fileStr1 = BasicTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-with-allotted.csar").getFile();
        File file1 = new File(fileStr1);
        startTime = System.currentTimeMillis();

        fdntCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());

        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("init CSAR Execution time: " + estimatedTime);

        String fileStr2 = BasicTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-rainy.csar").getFile();
        File file2 = new File(fileStr2);
        rainyCsarHelperMultiVfs = factory.getSdcCsarHelper(file2.getAbsolutePath());

        String fileStr3 = BasicTest.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar.csar").getFile();
        File file3 = new File(fileStr3);
        rainyCsarHelperSingleVf = factory.getSdcCsarHelper(file3.getAbsolutePath());
        
        /* Objects for QA Validation Tests */
        
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

    @AfterSuite
    public static void after(){
        long startTime = System.currentTimeMillis();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("close Execution time: "+estimatedTime);
    };

    @BeforeMethod
    public void setupTest(Method method) {
        System.out.println("#### Starting Test " + method.getName() + " ###########");
    }

    @AfterMethod
    public void tearDown(Method method){
        System.out.println("#### Ended test " + method.getName() + " ###########");
    }
}
