package org.openecomp.sdc.impl;

import org.junit.*;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.openecomp.sdc.toscaparser.common.JToscaException;

import java.io.File;
import java.io.IOException;

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

    @BeforeClass
    public static void init() throws SdcToscaParserException, JToscaException, IOException {

        factory = SdcToscaParserFactory.getInstance();
        long startTime = System.currentTimeMillis();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to init factory " + estimatedTime);

        String fileStr1 = ToscaParserTestSuite.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-0904-2.csar").getFile();
        File file1 = new File(fileStr1);
        startTime = System.currentTimeMillis();

        fdntCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());

        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("init CSAR Execution time: " + estimatedTime);

        String fileStr2 = ToscaParserTestSuite.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar-rainy.csar").getFile();
        File file2 = new File(fileStr2);
        rainyCsarHelperMultiVfs = factory.getSdcCsarHelper(file2.getAbsolutePath());

        String fileStr3 = ToscaParserTestSuite.class.getClassLoader().getResource("csars/service-ServiceFdnt-csar.csar").getFile();
        File file3 = new File(fileStr3);
        rainyCsarHelperSingleVf = factory.getSdcCsarHelper(file3.getAbsolutePath());
    };

    @AfterClass
    public static void after(){
        long startTime = System.currentTimeMillis();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("close Execution time: "+estimatedTime);
    };


}
