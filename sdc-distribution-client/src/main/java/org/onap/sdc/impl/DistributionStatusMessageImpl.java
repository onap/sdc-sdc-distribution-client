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

import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.utils.DistributionStatusEnum;

class DistributionStatusMessageImpl implements IDistributionStatusMessage {

    private String distributionID;
    private String consumerID;
    private long timestamp;
    private String artifactURL;
    private DistributionStatusEnum status;
    private String errorReason;
    private String componentName;

    DistributionStatusMessageImpl(IDistributionStatusMessage message) {
        super();
        distributionID = message.getDistributionID();
        consumerID = message.getConsumerID();
        timestamp = message.getTimestamp();
        artifactURL = message.getArtifactURL();
        status = message.getStatus();

    }

    DistributionStatusMessageImpl(IComponentDoneStatusMessage message) {
        super();
        distributionID = message.getDistributionID();
        consumerID = message.getConsumerID();
        timestamp = message.getTimestamp();
        artifactURL = message.getArtifactURL();
        status = message.getStatus();
        componentName = message.getComponentName();
    }

    DistributionStatusMessageImpl(IFinalDistrStatusMessage message) {
        super();
        distributionID = message.getDistributionID();
        consumerID = message.getConsumerID();
        timestamp = message.getTimestamp();

        artifactURL = "";
        status = message.getStatus();
        componentName = message.getComponentName();
    }

    @Override
    public String getDistributionID() {

        return distributionID;
    }

    @Override
    public String getConsumerID() {

        return consumerID;
    }

    @Override
    public long getTimestamp() {

        return timestamp;
    }

    @Override
    public String getArtifactURL() {

        return artifactURL;
    }

    @Override
    public DistributionStatusEnum getStatus() {

        return status;
    }

    public String getErrorReason() {
        return errorReason;
    }

    void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getComponentName() {
        return componentName;
    }


}
