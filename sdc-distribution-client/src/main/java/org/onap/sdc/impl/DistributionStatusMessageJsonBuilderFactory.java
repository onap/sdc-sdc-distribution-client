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

import org.onap.sdc.api.IDistributionStatusMessageJsonBuilder;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.utils.DistributionStatusEnum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DistributionStatusMessageJsonBuilderFactory {
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static IDistributionStatusMessageJsonBuilder getSimpleBuilder(IDistributionStatusMessage statusMessage){
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);
		
		return prepareBuilderFromImpl(message);
	}

	public static IDistributionStatusMessageJsonBuilder getSimpleBuilder(IComponentDoneStatusMessage statusMessage){
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);

		return prepareBuilderFromImpl(message);
	}

	public static IDistributionStatusMessageJsonBuilder getSimpleBuilder(IFinalDistrStatusMessage statusMessage){
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);

		return prepareBuilderFromImpl(message);
	}
	
	public static IDistributionStatusMessageJsonBuilder getErrorReasonBuilder(IDistributionStatusMessage statusMessage, String errorReason){
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);
		message.setErrorReason(errorReason);
		
		return prepareBuilderFromImpl(message);
	}

	public static IDistributionStatusMessageJsonBuilder getErrorReasonBuilder(IComponentDoneStatusMessage statusMessage,
			String errorReason) {
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);
		message.setErrorReason(errorReason);
		return prepareBuilderFromImpl(message);
	}
	
	public static IDistributionStatusMessageJsonBuilder getErrorReasonBuilder(IFinalDistrStatusMessage statusMessage,
			String errorReason) {
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);
		message.setErrorReason(errorReason);
		return prepareBuilderFromImpl(message);
	}
	
	static IDistributionStatusMessageJsonBuilder prepareBuilderForNotificationStatus(final String consumerId, final long currentTimeMillis, final String distributionId,
			final ArtifactInfoImpl artifactInfo, boolean isNotified){
		
		final DistributionStatusEnum fakeStatusToReplace = DistributionStatusEnum.DOWNLOAD_OK;
		final String jsonRequest = buildDistributionStatusJson(consumerId, currentTimeMillis, distributionId, artifactInfo, fakeStatusToReplace);
		
		DistributionStatusNotificationEnum notificationStatus = isNotified ? DistributionStatusNotificationEnum.NOTIFIED : DistributionStatusNotificationEnum.NOT_NOTIFIED;
		final String changedRequest = jsonRequest.replace(fakeStatusToReplace.name(), notificationStatus.name());
		IDistributionStatusMessageJsonBuilder builder = new IDistributionStatusMessageJsonBuilder() {
			@Override
			public String build() {
				return changedRequest;
			}
		};
		return builder;
		
	}

	private static String buildDistributionStatusJson(final String consumerId,
			final long currentTimeMillis, final String distributionId,
			final ArtifactInfoImpl artifactInfo,
			final DistributionStatusEnum fakeStatusToBeReplaced) {
		IDistributionStatusMessage statusMessage = new IDistributionStatusMessage() {
			@Override
			public long getTimestamp() {
				return currentTimeMillis;
			}
			
			@Override
			public DistributionStatusEnum getStatus() {
				
				return fakeStatusToBeReplaced;
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
			public String getArtifactURL() {
				return artifactInfo.getArtifactURL();
			}
		};
		
		DistributionStatusMessageImpl message = new DistributionStatusMessageImpl(statusMessage);
		final String jsonRequest = gson.toJson(message);
		return jsonRequest;
	}
	
	private static IDistributionStatusMessageJsonBuilder prepareBuilderFromImpl( DistributionStatusMessageImpl message) {
		final String jsonRequest = gson.toJson(message);
		IDistributionStatusMessageJsonBuilder builder = new IDistributionStatusMessageJsonBuilder() {
			@Override
			public String build() {
				return jsonRequest;
			}
		};
		return builder;
	}
	
	private enum DistributionStatusNotificationEnum {
		NOTIFIED, NOT_NOTIFIED
	}

	

	
	
}
