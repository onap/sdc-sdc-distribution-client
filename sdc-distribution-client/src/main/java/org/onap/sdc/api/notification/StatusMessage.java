/**
 * ============LICENSE_START=======================================================
 * org.onap.sdc
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom AG Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.api.notification;

import org.onap.sdc.utils.DistributionStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class StatusMessage {
  private final String distributionID;
	private final String consumerID;
	private final long timestamp;
	private final String artifactURL;
	private final DistributionStatusEnum status;
	private final String errorReason;

  public StatusMessage(String distributionID, String consumerID, long timestamp, String artifactUrl, DistributionStatusEnum status) {
    this.distributionID = distributionID;
    this.consumerID = consumerID;
    this.timestamp = timestamp;
    this.artifactURL = artifactUrl;
    this.status = status;
    this.errorReason = null;
  }
}
