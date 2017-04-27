package org.openecomp.sdc.impl;

import org.junit.Test;
import org.openecomp.sdc.toscaparser.api.parameters.Input;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ToscaParserServiceInputTest extends BasicTest {

    //region getServiceInputs
    @Test
    public void testGetServiceInputs(){
        List<Input> serviceInputs = ToscaParserTestSuite.fdntCsarHelper.getServiceInputs();
        assertNotNull(serviceInputs);
        assertEquals(1, serviceInputs.size());
    }

    @Test
    public void testServiceInputs() {
        List<Input> inputs = ToscaParserTestSuite.rainyCsarHelperSingleVf.getServiceInputs();
        assertNotNull(inputs);
        assertEquals(0, inputs.size());
    }
    //endregion

    //region getServiceInputLeafValueOfDefault
    @Test
    public void testGetServiceInputLeafValue(){
        String serviceInputLeafValue = ToscaParserTestSuite.fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }

    @Test
    public void testGetServiceInputLeafValueNotExists(){
        String serviceInputLeafValue = ToscaParserTestSuite.fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default#kuku");
        assertNull(serviceInputLeafValue);
    }

    @Test
    public void testGetServiceInputLeafValueNull(){
        String serviceInputLeafValue = ToscaParserTestSuite.fdntCsarHelper.getServiceInputLeafValueOfDefault(null);
        assertNull(serviceInputLeafValue);
    }
    //endregion

}
