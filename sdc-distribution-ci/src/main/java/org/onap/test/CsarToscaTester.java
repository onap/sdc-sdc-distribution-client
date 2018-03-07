package org.onap.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.tosca.parser.impl.SdcTypes;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.parameters.Input;
import org.onap.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class CsarToscaTester {
	public static void main(String[] args) throws Exception {
		System.out.println("CsarToscaParser - path to CSAR's Directory is " + Arrays.toString(args));
		SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();

		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-y-HH_mm_ss");
		String time = dateFormat.format(now);
		String csarsDir = args[1] + "/csar-reports-" + time;
		File dir = new File(csarsDir);
		dir.mkdir();


		for (File file : listOfFiles) {
			if (file.isFile()) {  
				System.out.println("File  " + file.getAbsolutePath());
				String name = file.getName();
				String currentCsarDir = csarsDir+"/"+name+"-"+time;
				dir = new File(currentCsarDir);
				dir.mkdir();
				try {
					processCsar(factory, file);
				} catch (SdcToscaParserException e){
					System.out.println("SdcToscaParserException caught. Code: "+e.getCode()+", message: "+ e.getMessage());
				}
				List<String> validationIssueReport = ThreadLocalsHolder.getCollector().getValidationIssueReport();
				System.out.println("Validation issues during CSAR parsing are: " + (validationIssueReport != null ? validationIssueReport.toString() : "none"));

				try {
					generateReport(time, name, currentCsarDir, validationIssueReport, "validationIssues");

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		}		
	}

	private static void processCsar(SdcToscaParserFactory factory, File file) throws SdcToscaParserException {
		ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file.getAbsolutePath());
		//Service level
		System.out.println("Invoking sdc-tosca methods on this CSAR....");
		String conformanceLevel = sdcCsarHelper.getConformanceLevel();
		System.out.println("getConformanceLevel() - conformance level is "+conformanceLevel);
		String serviceSubstitutionMappingsTypeName = sdcCsarHelper.getServiceSubstitutionMappingsTypeName();
		System.out.println("serviceSubstitutionMappingsTypeName() - subst mappings type of service is "+serviceSubstitutionMappingsTypeName);
		List<Input> serviceInputs = sdcCsarHelper.getServiceInputs();
		System.out.println("getServiceInputs() - service inputs are "+serviceInputs);
		Metadata serviceMetadata = sdcCsarHelper.getServiceMetadata();
		System.out.println("getServiceMetadata() - service metadata is "+serviceMetadata);
		Map<String, Object> serviceMetadataProperties = sdcCsarHelper.getServiceMetadataProperties();
		System.out.println("getServiceMetadataProperties() - service metadata properties is "+serviceMetadataProperties);
		List<NodeTemplate> allottedResources = sdcCsarHelper.getAllottedResources();
		System.out.println("getAllottedResources() - service allotted resources are "+allottedResources);
		List<NodeTemplate> serviceVfList = sdcCsarHelper.getServiceVfList();
		System.out.println("getServiceVfList() - VF list is "+serviceVfList);
		List<NodeTemplate> serviceNodeTemplateBySdcType = sdcCsarHelper.getServiceNodeTemplateBySdcType(SdcTypes.VF);
		System.out.println("getServiceNodeTemplateBySdcType() - VF list is "+serviceNodeTemplateBySdcType);
		List<NodeTemplate> serviceNodeTemplates = sdcCsarHelper.getServiceNodeTemplates();
		System.out.println("getServiceNodeTemplates() - all node templates list of service is "+serviceNodeTemplates);
		
		serviceVfList.forEach(x -> {
			String nodeTemplateCustomizationUuid = sdcCsarHelper.getNodeTemplateCustomizationUuid(x);
			System.out.println("getNodeTemplateCustomizationUuid() - VF ID is "+nodeTemplateCustomizationUuid);
			String typeOfNodeTemplate = sdcCsarHelper.getTypeOfNodeTemplate(x);
			System.out.println("getTypeOfNodeTemplate() - VF tosca type is "+typeOfNodeTemplate);
			List<Group> vfModulesByVf = sdcCsarHelper.getVfModulesByVf(nodeTemplateCustomizationUuid);
			System.out.println("getVfModulesByVf() - VF modules list is "+vfModulesByVf);
			vfModulesByVf.forEach(y -> {
				List<NodeTemplate> membersOfVfModule = sdcCsarHelper.getMembersOfVfModule(x, y);
				System.out.println("getMembersOfVfModule() - members of VfModule are "+membersOfVfModule);
			});
			List<NodeTemplate> vfcListByVf = sdcCsarHelper.getVfcListByVf(nodeTemplateCustomizationUuid);
			System.out.println("getVfcListByVf() - VFC list is "+vfcListByVf);
			vfcListByVf.forEach(z -> {
				List<NodeTemplate> nodeTemplateBySdcType = sdcCsarHelper.getNodeTemplateBySdcType(z, SdcTypes.CP);
				System.out.println("getNodeTemplateBySdcType() - CP children node templates of this VFC are "+nodeTemplateBySdcType);
				Map<String, Map<String, Object>> cpPropertiesFromVfcAsObject = sdcCsarHelper.getCpPropertiesFromVfcAsObject(z);
				System.out.println("getCpPropertiesFromVfcAsObject() - consolidated CP properties for this VFC are "+cpPropertiesFromVfcAsObject);
				boolean hasTopology = sdcCsarHelper.hasTopology(z);
				System.out.println("hasTopology() - this VFC is "+(hasTopology ? "nested" : "not nested"));
			});			
		});
		
	}

	private static void generateReport(String time, String name, String currentCsarDir, List<String> criticalsReport, String type)
			throws IOException {
		FileWriter fw;
		fw = new FileWriter(new File(currentCsarDir + "/" + criticalsReport.size() + "-"+type+"-" + name +"-"+time + ".txt"));
		for (String exception : criticalsReport) {
			fw.write(exception);
			fw.write("\r\n");
		}
		fw.close();
	}
}
