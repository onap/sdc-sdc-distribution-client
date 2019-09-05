/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights reserved.
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
package org.onap.sdc.api.asdc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class RegistrationRequestTest {

    private static final List<String> DIST_ENV_END_POINTS = Collections.emptyList();
    private static final boolean IS_CONSUMER_TO_SDC_DISTR_STATUS_TOPIC = true;
    private static final String ENV_NAME = "ENV_NAME";
    private static final String API_KEY = "API_KEY";

    @Test
    public void testConstructorShouldSetProperties() {
        RegistrationRequest registrationRequest =
                new RegistrationRequest(API_KEY, ENV_NAME, IS_CONSUMER_TO_SDC_DISTR_STATUS_TOPIC, DIST_ENV_END_POINTS);
        assertEquals(registrationRequest.getApiPublicKey(), API_KEY);
        assertEquals(registrationRequest.getDistEnvEndPoints(), DIST_ENV_END_POINTS);
        assertEquals(registrationRequest.getDistrEnvName(), ENV_NAME);
        assertTrue(registrationRequest.getIsConsumerToSdcDistrStatusTopic());
    }
}