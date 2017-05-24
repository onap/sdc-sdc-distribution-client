package org.openecomp.sdc.impl;

import org.testng.annotations.Test;
import org.openecomp.sdc.tosca.parser.utils.GeneralUtility;

import static org.testng.Assert.assertTrue;

public class ToscaParserGeneralUtilTest extends SdcToscaParserBasicTest {

    @Test
    public void testVersionCompare() {
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "3.0") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.5", "0.5") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.5", "0.6") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("1.5", "2.6") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.2", "0.1") > 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "1.15") > 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "2.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.0", "2.0.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.", "2.0.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.0", "2.0.0.2") < 0);
    }
}
