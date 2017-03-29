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

package org.openecomp.sdc.impl;

import org.openecomp.sdc.api.consumer.IDistributionStatusMessage;
import org.openecomp.sdc.utils.DistributionStatusEnum;

class DistributionStatusMessageImpl implements IDistributionStatusMessage {
	
	
	String distributionID;
	String consumerID;
	long timestamp;
	String artifactURL;
	DistributionStatusEnum status;
	String errorReason;
	
	
	public DistributionStatusMessageImpl(IDistributionStatusMessage message){
		super();
		distributionID = message.getDistributionID();
		consumerID = message.getConsumerID();
		timestamp = message.getTimestamp();
		artifactURL = message.getArtifactURL();
		status = message.getStatus();
		
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

	public void setDistributionID(String distributionID) {
		this.distributionID = distributionID;
	}

	public void setConsumerID(String consumerID) {
		this.consumerID = consumerID;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setArtifactURL(String artifactURL) {
		this.artifactURL = artifactURL;
	}

	public void setStatus(DistributionStatusEnum status) {
		this.status = status;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

}
