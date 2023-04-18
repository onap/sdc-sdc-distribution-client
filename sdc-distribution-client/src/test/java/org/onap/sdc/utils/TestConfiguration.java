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

package org.onap.sdc.utils;

import java.util.ArrayList;
import java.util.List;
import org.onap.sdc.api.consumer.IConfiguration;

public class TestConfiguration implements IConfiguration {

	private String sdcAddress;
	private String user;
	private String password;
	private int pollingInterval = DistributionClientConstants.MIN_POLLING_INTERVAL_SEC;
	private int pollingTimeout = DistributionClientConstants.POLLING_TIMEOUT_SEC;
	private List<String> relevantArtifactTypes;
	private String consumerGroup;
	private String environmentName;
	private String consumerID;
	private final String kafkaSecurityProtocolConfig;
	private final String kafkaSaslMechanism;
	private final String kafkaSaslJaasConfig;
	private final int kafkaConsumerMaxPollInterval;
	private final int kafkaConsumerSessionTimeout;
	private String keyStorePath;
	private String keyStorePassword;
	private boolean activateServerTLSAuth;
	private boolean isFilterInEmptyResources;
	private boolean useHttpsWithSDC;
	private String httpProxyHost;
	private int httpProxyPort;
	private String httpsProxyHost;
	private int httpsProxyPort;
	private boolean useSystemProxy;
	private String sdcStatusTopicName;
	private String sdcNotificationTopicName;

	public TestConfiguration() {
		this.sdcAddress = "localhost:8443";
		this.consumerID = "mso-123456";
		this.consumerGroup = "mso-group";
		this.environmentName = "PROD";
		this.password = "password";
		this.pollingInterval = 20;
		this.pollingTimeout = 20;
		this.setSdcStatusTopicName("SDC-STATUS-TOPIC");
		this.setSdcNotificationTopicName("SDC-NOTIF-TOPIC");
		this.relevantArtifactTypes = new ArrayList<>();
		this.relevantArtifactTypes.add(ArtifactTypeEnum.HEAT.name());
		this.user = "mso-user";
		this.keyStorePath = "etc/sdc-client.jks";
		this.keyStorePassword = "Aa123456";
		this.activateServerTLSAuth = true;
		this.isFilterInEmptyResources = false;
		this.useHttpsWithSDC = true;
		this.kafkaSecurityProtocolConfig = "SASL_PLAINTEXT";
		this.kafkaSaslMechanism = "PLAIN";
		this.kafkaSaslJaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required username=admin password=admin-secret;";
		this.kafkaConsumerMaxPollInterval = 600;
		this.kafkaConsumerSessionTimeout = 50;
		this.httpProxyHost = "proxy";
		this.httpProxyPort = 8080;
	}

	@Override
	public String getSdcAddress() {
		return sdcAddress;
	}

	@Override
	public String getKafkaSecurityProtocolConfig() {
		return kafkaSecurityProtocolConfig;
	}

	@Override
	public String getKafkaSaslMechanism() {
		return kafkaSaslMechanism;
	}

	@Override
	public String getKafkaSaslJaasConfig() {
		return kafkaSaslJaasConfig;
	}

	@Override
	public int getKafkaConsumerMaxPollInterval() {
		return kafkaConsumerMaxPollInterval;
	}

	@Override
	public int getKafkaConsumerSessionTimeout() {
		return kafkaConsumerSessionTimeout;
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
	public String getKeyStorePath() {
		return keyStorePath;
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public String getConsumerID() {
		return consumerID;
	}

	@Override
	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	@Override
	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	@Override
	public String getHttpsProxyHost() {
		return httpsProxyHost;
	}

	@Override
	public int getHttpsProxyPort() {
		return httpsProxyPort;
	}

	@Override
	public Boolean isUseSystemProxy() {
		return useSystemProxy;
	}

	public void setConsumerID(String consumerID) {
		this.consumerID = consumerID;
	}

	public void setSdcAddress(String sdcAddress) {
		this.sdcAddress = sdcAddress;
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

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public void setHttpsProxyHost(String httpsProxyHost) {
		this.httpsProxyHost = httpsProxyHost;
	}

	public void setHttpsProxyPort(int httpsProxyPort) {
		this.httpsProxyPort = httpsProxyPort;
	}

	public void setUseSystemProxy(boolean useSystemProxy) {
		this.useSystemProxy = useSystemProxy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sdcAddress == null) ? 0 : sdcAddress.hashCode());
		result = prime * result + ((consumerID == null) ? 0 : consumerID.hashCode());
		result = prime * result + ((consumerGroup == null) ? 0 : consumerGroup.hashCode());
		result = prime * result + ((environmentName == null) ? 0 : environmentName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + pollingInterval;
		result = prime * result + pollingTimeout;
		result = prime * result + ((relevantArtifactTypes == null) ? 0 : relevantArtifactTypes.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean activateServerTLSAuth() {

		return activateServerTLSAuth;
	}

	public void setactivateServerTLSAuth(boolean activateServerTLSAuth) {
		this.activateServerTLSAuth = activateServerTLSAuth;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestConfiguration other = (TestConfiguration) obj;
		if (sdcAddress == null) {
			if (other.sdcAddress != null)
				return false;
		} else if (!sdcAddress.equals(other.sdcAddress))
			return false;
		if (consumerID == null) {
			if (other.consumerID != null)
				return false;
		} else if (!consumerID.equals(other.consumerID))
			return false;
		if (consumerGroup == null) {
			if (other.consumerGroup != null)
				return false;
		} else if (!consumerGroup.equals(other.consumerGroup))
			return false;
		if (environmentName == null) {
			if (other.environmentName != null)
				return false;
		} else if (!environmentName.equals(other.environmentName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pollingInterval != other.pollingInterval)
			return false;
		if (pollingTimeout != other.pollingTimeout)
			return false;
		if (relevantArtifactTypes == null) {
			if (other.relevantArtifactTypes != null)
				return false;
		} else if (!relevantArtifactTypes.equals(other.relevantArtifactTypes))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (keyStorePath == null) {
			if (other.keyStorePath != null)
				return false;
		} else if (!keyStorePath.equals(other.keyStorePath))
			return false;
		if (keyStorePassword == null) {
			return other.keyStorePassword == null;
		} else
			return keyStorePassword.equals(other.keyStorePassword);
	}

	@Override
	public String toString() {
		return "TestConfiguration [sdcAddress=" + sdcAddress + ", user=" + user + ", password=" + password
				+ ", pollingInterval=" + pollingInterval + ", pollingTimeout=" + pollingTimeout
				+ ", relevantArtifactTypes=" + relevantArtifactTypes + ", consumerGroup=" + consumerGroup
				+ ", environmentName=" + environmentName + ", comsumerID=" + consumerID + "]";
	}

	@Override
	public boolean isFilterInEmptyResources() {
		return isFilterInEmptyResources;
	}

	public void setFilterInEmptyResources(boolean isFilterInEmptyResources) {
		this.isFilterInEmptyResources = isFilterInEmptyResources;
	}

	@Override
	public Boolean isUseHttpsWithSDC() {
		return this.useHttpsWithSDC;
	}

	public void setUseHttpsWithSDC(Boolean useHttpsWithSDC) {
		this.useHttpsWithSDC = useHttpsWithSDC;
	}

	public String getSdcStatusTopicName() {
		return sdcStatusTopicName;
	}

	public void setSdcStatusTopicName(String sdcStatusTopicName) {
		this.sdcStatusTopicName = sdcStatusTopicName;
	}

	public String getSdcNotificationTopicName() {
		return sdcNotificationTopicName;
	}

	public void setSdcNotificationTopicName(String sdcNotificationTopicName) {
		this.sdcNotificationTopicName = sdcNotificationTopicName;
	}
}
