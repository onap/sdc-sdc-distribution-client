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
package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.notification.DistributionStatusEnum;

class DistributionStatusMessageImplTest {

    private static final String ARTIFACT = "ARTIFACT";
    private static final String DISTRIBUTION_ID = "DISTRIBUTION_ID";
    private static final String CONSUMER_ID = "CONSUMER_ID";
    private static final String COMPONENT_NAME = "COMPONENT_NAME";

    @Test
    void shouldProperlySetPropertiesFromIDistributionStatusMessage() {
        IDistributionStatusMessage copyFrom = new IDistributionStatusMessage() {
            @Override
            public String getArtifactURL() {
                return ARTIFACT;
            }

            @Override
            public String getDistributionID() {
                return DISTRIBUTION_ID;
            }

            @Override
            public String getConsumerID() {
                return CONSUMER_ID;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public DistributionStatusEnum getStatus() {
                return DistributionStatusEnum.ALREADY_DEPLOYED;
            }
        };

        DistributionStatusMessageImpl result = new DistributionStatusMessageImpl(copyFrom);
        assertEquals(result.getArtifactURL(), copyFrom.getArtifactURL());
        assertEquals(result.getConsumerID(), copyFrom.getConsumerID());
        assertEquals(result.getDistributionID(), copyFrom.getDistributionID());
        assertEquals(result.getStatus(), copyFrom.getStatus());
        assertEquals(result.getTimestamp(), copyFrom.getTimestamp());
    }

    @Test
    void shouldProperlySetPropertiesFromIComponentDoneStatusMessage() {
        IComponentDoneStatusMessage copyFrom = new IComponentDoneStatusMessage() {
            @Override
            public String getComponentName() {
                return COMPONENT_NAME;
            }

            @Override
            public String getDistributionID() {
                return DISTRIBUTION_ID;
            }

            @Override
            public String getConsumerID() {
                return CONSUMER_ID;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public DistributionStatusEnum getStatus() {
                return DistributionStatusEnum.ALREADY_DEPLOYED;
            }
        };

        DistributionStatusMessageImpl result = new DistributionStatusMessageImpl(copyFrom);
        assertEquals(result.getArtifactURL(), copyFrom.getArtifactURL());
        assertEquals(result.getConsumerID(), copyFrom.getConsumerID());
        assertEquals(result.getDistributionID(), copyFrom.getDistributionID());
        assertEquals(result.getStatus(), copyFrom.getStatus());
        assertEquals(result.getTimestamp(), copyFrom.getTimestamp());
    }

    @Test
    void shouldProperlySetPropertiesFromIFinalDistrStatusMessage() {
        IFinalDistrStatusMessage copyFrom = new IFinalDistrStatusMessage() {

            @Override
            public String getDistributionID() {
                return DISTRIBUTION_ID;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public DistributionStatusEnum getStatus() {
                return DistributionStatusEnum.ALREADY_DEPLOYED;
            }
        };

        DistributionStatusMessageImpl result = new DistributionStatusMessageImpl(copyFrom);
        assertEquals(result.getConsumerID(), copyFrom.getConsumerID());
        assertEquals(result.getDistributionID(), copyFrom.getDistributionID());
        assertEquals(result.getStatus(), copyFrom.getStatus());
        assertEquals(result.getTimestamp(), copyFrom.getTimestamp());
    }

}
