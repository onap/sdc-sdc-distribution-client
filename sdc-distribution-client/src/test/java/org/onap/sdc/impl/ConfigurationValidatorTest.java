/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurationValidatorTest {
    private static final String[] VALID_FQDNS = {"myHostname", "myHostname:80", "myHostname:8080", "1.1.1.1", "1.1.1.1:8080", "mb01hydc.it.open.com", "mb01hydc.it.open.com:8080", "mb01hydc.it", "my-good.and-simple.fqdn"};
    private static final String[] INVALID_FQDNS = {"myHostname:808080", /* 70 letters */"abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij", "not**good", "very#not#good#"};

    @Test
    public void shouldReportThatFqdnIsValid() {
        for (String validFqdn : VALID_FQDNS) {
            boolean validationResult = ConfigurationValidator.isValidFqdn(validFqdn);
            assertEquals("assertion failed for FQDN " + validFqdn + " expected to be valid, actual invalid", true, validationResult);
        }
    }

    @Test
    public void shouldReportThatFqdnIsInvalid() {
        for (String invalidFqdn : INVALID_FQDNS) {
            boolean validationResult = ConfigurationValidator.isValidFqdn(invalidFqdn);
            assertEquals("assertion failed for FQDN " + invalidFqdn + " expected to be invalid, actual valid", false, validationResult);
        }
    }
    @Test
    public void shouldReportThatFqdnsAreValid() {
        assertTrue(ConfigurationValidator.isValidFqdns(Arrays.asList(VALID_FQDNS)));
    }

    @Test
    public void shouldReportThatFqdnsAreInvalid() {
        assertFalse(ConfigurationValidator.isValidFqdns(Arrays.asList(INVALID_FQDNS)));
    }
}
