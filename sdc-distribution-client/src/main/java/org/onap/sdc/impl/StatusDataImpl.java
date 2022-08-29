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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.onap.sdc.api.notification.IStatusData;
import org.onap.sdc.utils.DistributionStatusEnum;

@Getter
@Setter
@NoArgsConstructor
public class StatusDataImpl implements IStatusData {

    private String distributionID;
    private String consumerID;
    private Long timestamp;
    private String artifactURL;
    private DistributionStatusEnum status;
    private String componentName;
    private String errorReason;

    @Override
    public String toString() {
        return "StatusDataImpl [distributionID=" + distributionID + ", consumerID=" + consumerID + ", timestamp=" + timestamp + ", artifactURL="
            + artifactURL + ", status=" + status + ", errorReason=" + errorReason + "]";
    }

}
