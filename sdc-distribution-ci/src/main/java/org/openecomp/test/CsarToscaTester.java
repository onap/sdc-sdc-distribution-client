package org.openecomp.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;

public class CsarToscaTester {
	public static void main(String[] args) throws Exception {
		ClassLoader loader = CsarToscaTester.class.getClassLoader();
		System.out.println("CsarToscaParser - path to CSAR is "+Arrays.toString(args));
		SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
		factory.getSdcCsarHelper(args[0]);
		ArrayList<String> exceptionReport = ExceptionCollector.getExceptionReport();
		System.out.println("Errors during CSAR parsing are: "+(exceptionReport != null ? exceptionReport.toString() : "none"));
	}
}
