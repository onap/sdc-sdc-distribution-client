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

import java.util.List;

import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.api.consumer.IConfiguration;

public class Configuration implements IConfiguration {

	private List<String> msgBusAddressList;
	private String asdcAddress;
	private String user;
	private String password;
	private int pollingInterval = DistributionClientConstants.MIN_POLLING_INTERVAL_SEC;
	private int pollingTimeout = DistributionClientConstants.POLLING_TIMEOUT_SEC;
	private List<String> relevantArtifactTypes;
	private String consumerGroup;
	private String environmentName;
	private String comsumerID;
	private String keyStorePath;
	private String keyStorePassword;
	private boolean activateServerTLSAuth;
	private boolean filterInEmptyResources;
	private Boolean useHttpsWithDmaap;
	private boolean consumeProduceStatusTopic;

	public Configuration(IConfiguration other) {
		this.asdcAddress = other.getAsdcAddress();
		this.msgBusAddressList = other.getMsgBusAddress();
		this.comsumerID = other.getConsumerID();
		this.consumerGroup = other.getConsumerGroup();
		this.environmentName = other.getEnvironmentName();
		this.password = other.getPassword();
		this.pollingInterval = other.getPollingInterval();
		this.pollingTimeout = other.getPollingTimeout();
		this.relevantArtifactTypes = other.getRelevantArtifactTypes();
		this.user = other.getUser();
		this.keyStorePath = other.getKeyStorePath();
		this.keyStorePassword = other.getKeyStorePassword();
		this.activateServerTLSAuth = other.activateServerTLSAuth();
		this.filterInEmptyResources = other.isFilterInEmptyResources();
		this.useHttpsWithDmaap = other.isUseHttpsWithDmaap();
		this.consumeProduceStatusTopic = other.isConsumeProduceStatusTopic();
	}

	@Override
	public String getAsdcAddress() {
		return asdcAddress;
	}

	@Override
	public List<String> getMsgBusAddress() {
		return msgBusAddressList;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public int getPollingInterval() {
		return pollingInterval;
	}

	@Override
	public int getPollingTimeout() {
		return pollingTimeout;
	}

	@Override
	public List<String> getRelevantArtifactTypes() {
		return relevantArtifactTypes;
	}

	@Override
	public String getConsumerGroup() {
		return consumerGroup;
	}

	@Override
	public String getEnvironmentName() {
		return environmentName;
	}

	@Override
	public String getConsumerID() {
		return comsumerID;
	}

	@Override
	public String getKeyStorePath() {
		return keyStorePath;
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public String getComsumerID() {
		return comsumerID;
	}

	public void setComsumerID(String comsumerID) {
		this.comsumerID = comsumerID;
	}

	public void setAsdcAddress(String asdcAddress) {
		this.asdcAddress = asdcAddress;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	public void setPollingTimeout(int pollingTimeout) {
		this.pollingTimeout = pollingTimeout;
	}

	public void setRelevantArtifactTypes(List<String> relevantArtifactTypes) {
		this.relevantArtifactTypes = relevantArtifactTypes;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public void setactivateServerTLSAuth(boolean activateServerTLSAuth) {
		this.activateServerTLSAuth = activateServerTLSAuth;
	}

	@Override
	public boolean activateServerTLSAuth() {
		return this.activateServerTLSAuth;
	}

	@Override
	public boolean isFilterInEmptyResources() {
		return this.filterInEmptyResources;
	}

	@Override
	public Boolean isUseHttpsWithDmaap() {
		return this.useHttpsWithDmaap;
	}

	public void setUseHttpsWithDmaap(boolean useHttpsWithDmaap) {
		this.useHttpsWithDmaap = useHttpsWithDmaap;
	}

	@Override
	public boolean isConsumeProduceStatusTopic() {
		return this.consumeProduceStatusTopic;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "Configuration ["+
				"asdcAddress=" + asdcAddress + 
				", user=" + user + 
				", password=" + password + 
				", pollingInterval=" + pollingInterval + 
				", pollingTimeout=" + pollingTimeout + 
				", relevantArtifactTypes=" + relevantArtifactTypes + 
				", consumerGroup=" + consumerGroup + 
				", environmentName=" + environmentName + 
				", comsumerID=" + comsumerID + 
				", keyStorePath=" + keyStorePath + 
				", keyStorePassword=" + keyStorePassword + 
				", activateServerTLSAuth=" + activateServerTLSAuth + 
				", filterInEmptyResources=" + filterInEmptyResources + 
				", useHttpsWithDmaap=" + useHttpsWithDmaap + 
				", consumeProduceStatusTopic=" + consumeProduceStatusTopic + 
				"]";
		//@formatter:on
	}

}
