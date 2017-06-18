package org.openecomp.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcPropertyNames;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

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
					factory.getSdcCsarHelper(file.getAbsolutePath());
				} catch (SdcToscaParserException e){
					System.out.println("SdcToscaParserException caught. Code: "+e.getCode()+", message: "+ e.getMessage());
				}
				List<String> notAnalyzedReport = ThreadLocalsHolder.getCollector().getNotAnalyzedExceptionsReport();
				System.out.println("NOT ANALYZED during CSAR parsing are: " + (notAnalyzedReport != null ? notAnalyzedReport.toString() : "none"));
				List<String> warningsReport = ThreadLocalsHolder.getCollector().getWarningsReport();
				//System.out.println("WARNINGS during CSAR parsing are: " + (warningsReport != null ? warningsReport.toString() : "none"));
				List<String> criticalsReport = ThreadLocalsHolder.getCollector().getCriticalsReport();
				System.out.println("CRITICALS during CSAR parsing are: " + (criticalsReport != null ? criticalsReport.toString() : "none"));

				try {
					generateReport(time, name, currentCsarDir, criticalsReport, "critical");
					generateReport(time, name, currentCsarDir, warningsReport, "warning");
					generateReport(time, name, currentCsarDir, notAnalyzedReport, "notAnalyzed");

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		}		
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
