/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.sdc.utils.heat.HeatParameter;
import org.onap.sdc.utils.heat.HeatParameterConstraint;
import org.onap.sdc.utils.heat.HeatParser;

class HeatParserTest {

    @Test
    void testParametersParsing() throws IOException {
        String resourceName = "heatExample.yaml";
        URL url = Resources.getResource(resourceName);
        String heatFileContents = Resources.toString(url, Charsets.UTF_8);
        assertNotNull("Didn't find " + resourceName, heatFileContents);

        HeatParser heatParser = new HeatParser();
        //Flat parameter entry
        Map<String, HeatParameter> parameters = heatParser.getHeatParameters(heatFileContents);
        HeatParameter heatParameter1 = parameters.get("image_name_1");
        validateField("string", heatParameter1.getType(), "type");
        validateField("Image Name", heatParameter1.getLabel(), "label");
        validateField("SCOIMAGE Specify an image name for instance1", heatParameter1.getDescription(), "description");
        validateField("cirros-0.3.1-x86_64", heatParameter1.getDefault(), "default");
        validateField(null, heatParameter1.getConstraints(), "constraints");
        validateField("false", heatParameter1.getHidden(), "hidden");

        //Flat parameter entry with constraints
        heatParameter1 = parameters.get("network_id");
        validateField("string", heatParameter1.getType(), "type");
        validateField("Network ID", heatParameter1.getLabel(), "label");
        validateField("SCONETWORK Network to be used for the compute instance", heatParameter1.getDescription(), "description");
        validateField(null, heatParameter1.getDefault(), "default");
        validateField("true", heatParameter1.getHidden(), "hidden");

        //Constraints
        List<HeatParameterConstraint> constraints = heatParameter1.getConstraints();
        assertEquals(6, constraints.size(), "Number of constraints");

        //Length
        HeatParameterConstraint lengthConstraint = heatParameter1.getLengthConstraint();
        assertNotNull(lengthConstraint);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("min", "6");
        expectedMap.put("max", "8");
        validateField(expectedMap, lengthConstraint.getLength(), "length");
        validateField("Password length must be between 6 and 8 characters.", lengthConstraint.getDescription(), "length description");

        //Range
        HeatParameterConstraint rangeConstraint = heatParameter1.getRangeConstraint();
        assertNotNull(rangeConstraint);
        validateField(expectedMap, rangeConstraint.getRange(), "range");
        validateField("Range description", rangeConstraint.getDescription(), "range description");

        //Allowed values
        HeatParameterConstraint allowedValues = heatParameter1.getAllowedValuesConstraint();
        assertNotNull(allowedValues);
        List<String> expectedValues = new ArrayList<>();
        expectedValues.add("m1.small");
        expectedValues.add("m1.medium");
        expectedValues.add("m1.large");
        validateField(expectedValues, allowedValues.getAllowed_values(), "allowed_values");
        validateField("Allowed values description", allowedValues.getDescription(), "allowed_values description");

        //Allowed pattern
        List<HeatParameterConstraint> allowedPatternList = heatParameter1.getAllowedPatternConstraint();
        assertNotNull(allowedPatternList);
        assertEquals(2, allowedPatternList.size(), "Allowed pattern list");
        HeatParameterConstraint allowedPattern = allowedPatternList.get(0);
        validateField("[a-zA-Z0-9]+", allowedPattern.getAllowed_pattern(), "allowed_pattern");
        validateField("Password must consist of characters and numbers only.", allowedPattern.getDescription(), "allowed_pattern description");
        allowedPattern = allowedPatternList.get(1);
        validateField("[A-Z]+[a-zA-Z0-9]*", allowedPattern.getAllowed_pattern(), "allowed_pattern");
        validateField("Password must start with an uppercase character.", allowedPattern.getDescription(), "allowed_pattern description");

        //Custom constraint
        List<HeatParameterConstraint> customConstraintList = heatParameter1.getCustomConstraintConstraint();
        assertNotNull(customConstraintList);
        assertEquals(1, customConstraintList.size(), "Custom constraint list");
        HeatParameterConstraint customConstraint = customConstraintList.get(0);
        validateField("nova.keypair", customConstraint.getCustom_constraint(), "custom_constraint");
        validateField("Custom description", customConstraint.getDescription(), "custom_constraint description");
    }

    @Test
    void testParametersParsingInvalidYaml() throws IOException {
        String invalidHeatFileContents = "just text";
        HeatParser heatParser = new HeatParser();
        //Flat parameter entry
        Map<String, HeatParameter> parameters = heatParser.getHeatParameters(invalidHeatFileContents);
        assertNull(parameters);
    }

    @Test
    void testParametersParsingNoParamteresSection() throws IOException {
        String heatFileContentsNoParams = "heat_template_version: 2013-05-23\r\n\r\ndescription: Simple template to deploy a stack with two virtual machine instances";
        HeatParser heatParser = new HeatParser();
        //Flat parameter entry
        Map<String, HeatParameter> parameters = heatParser.getHeatParameters(heatFileContentsNoParams);
        assertNull(parameters);
    }

    private void validateField(Object expected, Object actual, String type) {
        assertEquals(expected, actual, "Field of type " + type + ":");
    }
}
