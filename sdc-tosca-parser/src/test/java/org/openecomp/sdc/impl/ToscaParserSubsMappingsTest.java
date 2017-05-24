package org.openecomp.sdc.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.api.Capability;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.elements.CapabilityTypeDef;
//import org.testng.ReporterConfig.Property;
import org.testng.annotations.Test;
import org.openecomp.sdc.toscaparser.api.Property;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

public class ToscaParserSubsMappingsTest extends SdcToscaParserBasicTest {

    //region getServiceSubstitutionMappingsTypeName
    @Test
    public void testGetServiceSubstitutionMappingsTypeName() {
        String serviceSubstitutionMappingsTypeName = fdntCsarHelper.getServiceSubstitutionMappingsTypeName();
        assertEquals("org.openecomp.service.ServiceFdnt", serviceSubstitutionMappingsTypeName);
    }

    @Test
    public void testServiceSubstitutionMappingsTypeName() {
        String substitutionMappingsTypeName = rainyCsarHelperMultiVfs.getServiceSubstitutionMappingsTypeName();
        assertNull(substitutionMappingsTypeName);
    }
    //endregion
    
  //Added by QA - Check for Capabilities in VF level (Capabilities QTY and Names).
		//@Test // - BUG 283369
		public void testCapabilitiesofVFNames_QTY() throws SdcToscaParserException {
			List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
			String sName = serviceVfList.get(0).getName();
			assertEquals(sName,fdntCsarHelper_Data.get("FDNT").get("VF Name").get(0));			
			LinkedHashMap<String, Capability> lCapabilitys = serviceVfList.get(0).getCapabilities();
			List<String> CPkeys = new ArrayList<>(lCapabilitys.keySet());  			
			List<String> CapabilitiesNames = new ArrayList<String>(CPkeys.size());  			
			
			for (int i = 0; i < CPkeys.size(); i++) {
				
				Capability cCp = lCapabilitys.get(CPkeys.get(i));  

				CapabilitiesNames.add(cCp.getName());
				
				assertEquals(CPkeys.get(i).toLowerCase(), CapabilitiesNames.get(i).toLowerCase());// Compare keys to values, Should it be checked as Case sensitive????
				
				//System.out.println(String.format("Value of key: %s , Value of capability: %s", keys.get(i).toLowerCase(), Capabilities.get(i).toLowerCase()));
				//System.out.println(String.format("Value of key: %s , Value of capability: %s", ActualValues.get(i).toLowerCase(), Capabilities.get(i).toLowerCase()));
				//System.out.println(String.format("*******%d*******",i));
			}
			
			for (int i = 0; i < CPkeys.size(); i++) {  			
				assertEquals(true, CapabilitiesNames.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(fdntCsarHelper_Data.get("FDNT").get("capabilities").get(i).toLowerCase())); // Compare capabilities predefined list to actual one. 
			}
			
			assertEquals(fdntCsarHelper_Data.get("FDNT").get("capabilities").size(), CapabilitiesNames.size()); // Compare capabilities qty expected vs actual
		}
		
	//Added by QA - Check for Capabilities in VF level (Capabilities Types and Properties).
		//@Test 
		public void testCapabilitiesofVFTypes_Properties() throws SdcToscaParserException {
			List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
			String sName = serviceVfList.get(0).getName();
			assertEquals(sName,fdntCsarHelper_Data.get("FDNT").get("VF Name").get(0));			
			LinkedHashMap<String, Capability> lCapabilitys = serviceVfList.get(0).getCapabilities();
			
			List<String> CPkeys = new ArrayList<>(lCapabilitys.keySet());
			List<String> CPPropkeys = new ArrayList<>(lCapabilitys.keySet());
			List<String> CapabilitiesTypes = new ArrayList<String>(CPkeys.size());
			
			//int iKeysSize = keys.size(); //for debug
			
			for (int i = 0; i < CPkeys.size(); i++) {
				
				Capability cCp = lCapabilitys.get(CPkeys.get(i));  
				CapabilityTypeDef CpDef = cCp.getDefinition();
				CapabilitiesTypes.add(CpDef.getType());
				
				//LinkedHashMap<String,Object> lProperties = cCp.getDefinition().getProperties();  				
				LinkedHashMap<String, Property> lPropertiesR = cCp.getProperties();
				
	  			List<String> CP_Propkeys = new ArrayList<>(lPropertiesR.keySet());
			
	  			for (int j = 0; j < CP_Propkeys.size(); j++) {
	  				
	  			Property p = lPropertiesR.get(CP_Propkeys.get(j));

				if(p !=  null){
	  				String sPType = p.getType();
	  				Boolean bPRequired = p.isRequired();
	  				
	  				System.out.println(sPType + "  " + bPRequired);
					
					}
				
			}
	  			
			}			
			
			for (int i = 0; i < CPkeys.size(); i++) {  				

			}	
			
			assertEquals(fdntCsarHelper_Data.get("FDNT").get("capabilitiesTypes").size(), CapabilitiesTypes.size()); // Compare capabilities qty expected vs actual
		}
		
	    //@Test // - BUG 283387
		public void testRequirmentsofVF() throws SdcToscaParserException {
			List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
			String sName = serviceVfList.get(0).getName();
			assertEquals(sName,"FDNT 1");
			
			List<String> ActualReqsValues = new ArrayList<>(Arrays.asList( ));
			
			ArrayList<Object> lRequirements = serviceVfList.get(0).getRequirements();
			
			assertEquals(fdntCsarHelper_Data.get("FDNT").get("requirements").size(),lRequirements.size()); //
			
			// Continue from here after bug is fixed ! ! ! !  - Test the Requirements values
		}

}
