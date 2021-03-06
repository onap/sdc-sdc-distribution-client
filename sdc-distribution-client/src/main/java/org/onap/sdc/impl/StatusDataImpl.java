/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2019 Nokia. All rights reserved.
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

import org.onap.sdc.api.notification.IStatusData;
import org.onap.sdc.utils.DistributionStatusEnum;

public class StatusDataImpl implements IStatusData {

    private String distributionID;
    private String consumerID;
    private Long timestamp;
    private String artifactURL;
    private DistributionStatusEnum status;
    private String componentName;
    private String errorReason;

    @Override
    public String getDistributionID() {
        return distributionID;
    }

    public void setDistributionID(String distributionId) {
        this.distributionID = distributionId;
    }

    @Override
    public String getConsumerID() {
        return consumerID;
    }

    public void setConsumerID(String consumerId) {
        this.consumerID = consumerId;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getArtifactURL() {
        return artifactURL;
    }

    public void setArtifactURL(String artifactURL) {
        this.artifactURL = artifactURL;
    }

    @Override
    public DistributionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DistributionStatusEnum status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "StatusDataImpl [distributionID=" + distributionID + ", consumerID=" + consumerID + ", timestamp=" + timestamp + ", artifactURL=" + artifactURL + ", status=" + status + ", errorReason=" + errorReason + "]";
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public String getErrorReason() {
        return errorReason;
    }


}
