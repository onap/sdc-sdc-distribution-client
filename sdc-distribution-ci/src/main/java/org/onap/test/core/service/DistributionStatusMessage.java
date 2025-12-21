/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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
package org.onap.test.core.service;

import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.notification.DistributionStatusEnum;


public class DistributionStatusMessage implements IDistributionStatusMessage {

    private String artifactUrl;
    private String distributionId;
    private String consumerId;
    private long timestamp;
    private DistributionStatusEnum status;

    public DistributionStatusMessage(String artifactUrl, String distributionId, String consumerId, long timestamp, DistributionStatusEnum status) {
        this.artifactUrl = artifactUrl;
        this.distributionId = distributionId;
        this.consumerId = consumerId;
        this.timestamp = timestamp;
        this.status = status;
    }

    @Override
    public String getArtifactURL() {
        return artifactUrl;
    }

    @Override
    public String getDistributionID() {
        return distributionId;
    }

    @Override
    public String getConsumerID() {
        return consumerId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public DistributionStatusEnum getStatus() {
        return status;
    }
}
