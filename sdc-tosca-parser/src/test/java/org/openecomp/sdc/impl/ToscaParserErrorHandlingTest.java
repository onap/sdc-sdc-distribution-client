package org.openecomp.sdc.impl;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.io.File;

import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.api.utils.JToscaErrorCodes;


/*put(JToscaErrorCodes.GENERAL_ERROR, GENERAL_ERROR);

put(JToscaErrorCodes.PATH_NOT_VALID, FILE_NOT_FOUND);
//CSAR contents problems
put(JToscaErrorCodes.MISSING_META_FILE, BAD_FORMAT);
put(JToscaErrorCodes.INVALID_META_YAML_CONTENT, BAD_FORMAT);
put(JToscaErrorCodes.ENTRY_DEFINITION_NOT_DEFINED, BAD_FORMAT);
put(JToscaErrorCodes.MISSING_ENTRY_DEFINITION_FILE, BAD_FORMAT);
put(JToscaErrorCodes.CSAR_TOSCA_VALIDATION_ERROR, BAD_FORMAT);

 MISSING_META_FILE("JT1001"),
/*     INVALID_META_YAML_CONTENT("JT1002"),
/*     ENTRY_DEFINITION_NOT_DEFINED("JT1003"),
/*     MISSING_ENTRY_DEFINITION_FILE("JT1004"),
/*     GENERAL_ERROR("JT1005"),
/*     PATH_NOT_VALID("JT1006"),
/*     CSAR_TOSCA_VALIDATION_ERROR("JT1007");

*/

/*
 * 
 * # Errors
errors:
    FILE_NOT_FOUND: {
        code: TP0001,
        message: "Error: CSAR file not found."
    }
    BAD_FORMAT: {
        code: TP0002,
        message: "Error: CSAR file bad format. Check the log for details."
    }
    CONFORMANCE_LEVEL_ERROR: {
        code: TP0003,
        message: "Error: CSAR version is unsupported. Parser supports versions %s to %s." 
    }
    GENERAL_ERROR: {
        code: TP0004,
        message: "Error: an unexpected internal error occured."
    }
 * 
 */

public class ToscaParserErrorHandlingTest extends SdcToscaParserBasicTest {
	
	
	@Test
	public void testMissingMetadata(){
		String csarPath = "csars/service-missing-meta-file.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	
	@Test
	public void testInvalidYamlContentMeta(){
		String csarPath = "csars/service-invalid-yaml-content-meta.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	@Test
	public void testEntryDefinitionNotDefined(){
		String csarPath = "csars/service-entry-definition-not-defined.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}

	@Test
	public void testMissingEntryDefinitionFile(){
		String csarPath = "csars/service-missing-entry-definition.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	//@Test - PA - there are currently no critical erros in JTosca
	public void tesValidationError(){
		String csarPath = "csars/service-invalid-input-args.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	@Test
	public void testInValidConformanceLevelError(){
		String csarPath = "csars/service-invalid-conformence-level.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0003");
	}
	
	@Test
	public void testFileNotFound(){
		Throwable captureThrowable = captureThrowable("csars/XXX.csar");
		testThrowable(captureThrowable, "TP0001");
	}
	
	@Test
	public void testInvalidCsarFormat(){
		String csarPath = "csars/csar-invalid-zip.zip";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}

	private static void testThrowable(Throwable captureThrowable, String expectedCode) {
		assertNotNull(captureThrowable);
		assertTrue(captureThrowable instanceof SdcToscaParserException, "Error thrown is of type "+captureThrowable.getClass().getSimpleName());
		assertEquals(((SdcToscaParserException)captureThrowable).getCode(), expectedCode);
	}
	
	public static Throwable captureThrowable(String csarPath) {
		Throwable result = null;
		try {
			factory.getSdcCsarHelper(csarPath);
		} catch( Throwable throwable ) {
			result = throwable;
		}
		return result;
	}
}
