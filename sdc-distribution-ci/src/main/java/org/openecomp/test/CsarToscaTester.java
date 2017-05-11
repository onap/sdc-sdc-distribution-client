package org.openecomp.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.parameters.Input;

public class CsarToscaTester {
	public static void main(String[] args) throws Exception {
		ClassLoader loader = CsarToscaTester.class.getClassLoader();
		System.out.println("CsarToscaParser - path to CSAR's Directory is " + Arrays.toString(args));
		SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
		
		File folder = new File(args[0].toString());
		File[] listOfFiles = folder.listFiles();
		FileWriter fw;
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-y-HH_mm_ss");
		String time = dateFormat.format(now);
		File dir = new File(args[1].toString() + "/csar-reports-" + time);
		dir.mkdir();
		
		
		for (File file : listOfFiles) {
			if (file.isFile()) {  
		        System.out.println("File " + file.getAbsolutePath());
		        ExceptionCollector.clear();

		        ISdcCsarHelper csarHelper = factory.getSdcCsarHelper(file.getAbsolutePath());
		        List<NodeTemplate> vflist = csarHelper.getServiceVfList();
		        List<Input> inputs = csarHelper.getServiceInputs();
		        List<String> exceptionReport = ExceptionCollector.getCriticalsReport();
		        System.out.println("CRITICALS during CSAR parsing are: " + (exceptionReport != null ? exceptionReport.toString() : "none"));
		        List<String> warningsReport = ExceptionCollector.getWarningsReport();
		        System.out.println("WARNINGS during CSAR parsing are: " + (warningsReport != null ? warningsReport.toString() : "none"));
		        
				
		        if (!exceptionReport.isEmpty())  {
		        	
					try {
						fw = new FileWriter(new File(dir + "/" + exceptionReport.size() / 2 + "-critical-" + file.getName() + ".txt"));
						for (String exception : exceptionReport) {
							fw.write(exception);
							fw.write("\r\n");
						}
						fw.close();
						
						fw = new FileWriter(new File(dir + "/" + warningsReport.size() / 2 +  "-warning-" + file.getName() + ".txt"));
						for (String warning : warningsReport) {
							fw.write(warning);
							fw.write("\r\n");
						}
						fw.close();
						
						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
		     }
			
		}		
	}
}
