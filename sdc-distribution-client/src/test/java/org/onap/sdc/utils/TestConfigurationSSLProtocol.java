/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2023 Nordix Foundation. All rights reserved.
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

import lombok.Getter;
import lombok.Setter;
import org.onap.sdc.api.consumer.IConfiguration;

@Getter
@Setter
public class TestConfigurationSSLProtocol implements IConfiguration {

	private String user;
	private String password;
	private int httpProxyPort;
	private String sdcAddress;
	private String consumerID;
	private int httpsProxyPort;
	private String keyStorePath;
	private String httpProxyHost;
	private String consumerGroup;
	private String httpsProxyHost;
	private Boolean useSystemProxy;
	private String environmentName;
	private String keyStorePassword;
	private Boolean useHttpsWithSDC;
	private String sdcStatusTopicName;
	private boolean activateServerTLSAuth;
	private String sdcNotificationTopicName;
	private final String kafkaSaslMechanism;
	private boolean isFilterInEmptyResources;
	private final String kafkaSaslJaasConfig;
	private List<String> relevantArtifactTypes;
	private final int kafkaConsumerSessionTimeout;
	private final int kafkaConsumerMaxPollInterval;
	private final String kafkaSecurityProtocolConfig;
	private int pollingTimeout = DistributionClientConstants.POLLING_TIMEOUT_SEC;
	private int pollingInterval = DistributionClientConstants.MIN_POLLING_INTERVAL_SEC;

	public TestConfigurationSSLProtocol() {
		this.user = "mso-user";
		this.pollingTimeout = 20;
		this.httpProxyPort = 8080;
		this.pollingInterval = 20;
		this.password = "password";
		this.useHttpsWithSDC = true;
		this.httpProxyHost = "proxy";
		this.environmentName = "PROD";
		this.consumerID = "mso-123456";
		this.consumerGroup = "mso-group";
		this.activateServerTLSAuth = true;
		this.kafkaSaslMechanism = "PLAIN";
		this.sdcAddress = "localhost:8443";
		this.keyStorePassword = "Aa123456";
		this.isFilterInEmptyResources = false;
		this.kafkaConsumerSessionTimeout = 50;
		this.kafkaConsumerMaxPollInterval = 600;
		this.keyStorePath = "etc/sdc-client.jks";
		this.kafkaSecurityProtocolConfig = "SSL";
		this.setSdcStatusTopicName("SDC-STATUS-TOPIC");
		this.relevantArtifactTypes = new ArrayList<>();
		this.setSdcNotificationTopicName("SDC-NOTIF-TOPIC");
		this.relevantArtifactTypes.add(ArtifactTypeEnum.HEAT.name());
		this.kafkaSaslJaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required username=admin password=admin-secret;";
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestConfigurationSSLProtocol other = (TestConfigurationSSLProtocol) obj;
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
				+ ", environmentName=" + environmentName + ", consumerID=" + consumerID + "]";
	}
}
