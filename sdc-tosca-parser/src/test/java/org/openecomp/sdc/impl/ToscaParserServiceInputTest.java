package org.openecomp.sdc.impl;

import org.testng.annotations.Test;
import org.openecomp.sdc.toscaparser.api.parameters.Input;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ToscaParserServiceInputTest extends SdcToscaParserBasicTest {

    //region getServiceInputs
    @Test
    public void testGetServiceInputs(){
        List<Input> serviceInputs = fdntCsarHelper.getServiceInputs();
        assertNotNull(serviceInputs);
        assertEquals(1, serviceInputs.size());
    }

    @Test
    public void testServiceInputs() {
        List<Input> inputs = rainyCsarHelperSingleVf.getServiceInputs();
        assertNotNull(inputs);
        assertEquals(0, inputs.size());
    }
    //endregion

    //region getServiceInputLeafValueOfDefault
    @Test
    public void testGetServiceInputLeafValue(){
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }

//    @Test
//    public void testGetServiceInputLeafValueWithGetInput(){
//        String serviceInputLeafValue = fdntCsarHelperWithInputs.getServiceInputLeafValueOfDefault("my_input#default");
//        assertEquals(null, serviceInputLeafValue);
//    }

    @Test
    public void testGetServiceInputLeafValueNotExists(){
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default#kuku");
        assertNull(serviceInputLeafValue);
    }

    @Test
    public void testGetServiceInputLeafValueNull(){
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault(null);
        assertNull(serviceInputLeafValue);
    }
    //endregion

    //region getServiceInputLeafValueOfDefaultAsObject
    @Test
    public void testGetServiceInputLeafValueOfDefaultAsObject() {
        Object serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }
    //endregion
}
