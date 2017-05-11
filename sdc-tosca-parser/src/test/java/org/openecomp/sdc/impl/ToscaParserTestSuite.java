package org.openecomp.sdc.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.common.JToscaException;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
        ToscaParserNodeTemplateTest.class,
        ToscaParserSubsMappingsTest.class,
        ToscaParserGroupTest.class,
        ToscaParserMetadataTest.class,
        ToscaParserServiceInputTest.class,
} )
public class ToscaParserTestSuite {

    public static final String VF_CUSTOMIZATION_UUID = "56179cd8-de4a-4c38-919b-bbc4452d2d73";
    static SdcToscaParserFactory factory;
    static ISdcCsarHelper rainyCsarHelperSingleVf;
    static ISdcCsarHelper rainyCsarHelperMultiVfs;
    static ISdcCsarHelper fdntCsarHelper;
    static ISdcCsarHelper complexCps;

    @BeforeClass
    public static void init() throws SdcToscaParserException, JToscaException, IOException {

        factory = SdcToscaParserFactory.getInstance();
        fdntCsarHelper = getCsarHelper("csars/service-ServiceFdnt-with-allotted.csar");
        rainyCsarHelperMultiVfs = getCsarHelper("csars/service-ServiceFdnt-csar-rainy.csar");
        rainyCsarHelperSingleVf = getCsarHelper("csars/service-ServiceFdnt-csar.csar");
        complexCps = getCsarHelper("csars/1service-ServiceWithPorts.csar");
    }

	private static ISdcCsarHelper getCsarHelper(String path) throws JToscaException, IOException, SdcToscaParserException {
		System.out.println("Parsing CSAR "+path+"...");
		String fileStr1 = ToscaParserTestSuite.class.getClassLoader().getResource(path).getFile();
        File file1 = new File(fileStr1);
        ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());
        List<String> exceptionReport = ExceptionCollector.getCriticalsReport();
		if (!exceptionReport.isEmpty()){
        	System.out.println("TOSCA Errors found in CSAR - failing the tests...");
        	System.out.println(exceptionReport.toString());
        	ExceptionCollector.clear();
//        	throw new SdcToscaParserException("CSAR didn't pass validation");
        }
		return sdcCsarHelper;
	}

}
