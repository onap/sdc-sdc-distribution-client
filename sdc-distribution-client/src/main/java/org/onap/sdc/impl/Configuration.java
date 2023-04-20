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

import org.onap.sdc.api.consumer.IConfiguration;

public class Configuration implements IConfiguration {

    private List<String> msgBusAddressList;
    private final String kafkaSecurityProtocolConfig;
    private final String kafkaSaslMechanism;
    private final String kafkaSaslJaasConfig;
    private final String kafkaKeyStorePath;
    private final String kafkaKeyStorePassword;
    private final String kafkaTrustStorePath;
    private final String kafkaTrustStorePassword;
    private final int kafkaConsumerMaxPollInterval;
    private final int kafkaConsumerSessionTimeout;
    private String sdcStatusTopicName;
    private String sdcNotificationTopicName;
    private String sdcAddress;
    private String user;
    private String password;
    private int pollingInterval;
    private int pollingTimeout;
    private List<String> relevantArtifactTypes;
    private String consumerGroup;
    private String environmentName;
    private String comsumerID;
    private String keyStorePath;
    private String keyStorePassword;
    private String trustStorePath;
    private String trustStorePassword;
    private boolean activateServerTLSAuth;
    private final boolean filterInEmptyResources;
    private Boolean useHttpsWithSDC;
    private final boolean consumeProduceStatusTopic;
    private String httpProxyHost;
    private int httpProxyPort;
    private String httpsProxyHost;
    private int httpsProxyPort;
    private boolean useSystemProxy;

    public Configuration(IConfiguration other) {
        this.kafkaSecurityProtocolConfig = other.getKafkaSecurityProtocolConfig();
        this.kafkaSaslMechanism = other.getKafkaSaslMechanism();
        this.kafkaSaslJaasConfig = other.getKafkaSaslJaasConfig();
        this.comsumerID = other.getConsumerID();
        this.consumerGroup = other.getConsumerGroup();
        this.pollingInterval = other.getPollingInterval();
        this.pollingTimeout = other.getPollingTimeout();
        this.environmentName = other.getEnvironmentName();
        this.consumeProduceStatusTopic = other.isConsumeProduceStatusTopic();
        this.sdcAddress = other.getSdcAddress();
        this.user = other.getUser();
        this.password = other.getPassword();
        this.relevantArtifactTypes = other.getRelevantArtifactTypes();
        this.useHttpsWithSDC = other.isUseHttpsWithSDC();
        this.keyStorePath = other.getKeyStorePath();
        this.keyStorePassword = other.getKeyStorePassword();
        this.trustStorePath = other.getTrustStorePath();
        this.trustStorePassword = other.getTrustStorePassword();
        this.activateServerTLSAuth = other.activateServerTLSAuth();
        this.filterInEmptyResources = other.isFilterInEmptyResources();
        this.httpProxyHost = other.getHttpProxyHost();
        this.httpProxyPort = other.getHttpProxyPort();
        this.httpsProxyHost = other.getHttpsProxyHost();
        this.httpsProxyPort = other.getHttpsProxyPort();
        this.useSystemProxy = other.isUseSystemProxy();
        this.kafkaConsumerMaxPollInterval = other.getKafkaConsumerMaxPollInterval();
        this.kafkaConsumerSessionTimeout = other.getKafkaConsumerSessionTimeout();
        this.kafkaKeyStorePath = other.getKafkaKeyStorePath();
        this.kafkaKeyStorePassword = other.getKafkaKeyStorePassword();
        this.kafkaTrustStorePath = other.getKafkaTrustStorePath();
        this.kafkaTrustStorePassword = other.getKafkaTrustStorePassword();
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
    public String getKafkaKeyStorePath() {
        return kafkaKeyStorePath;
    }

    @Override
    public String getKafkaKeyStorePassword() {
        return kafkaKeyStorePassword;
    }

    @Override
    public String getKafkaTrustStorePath() {
        return kafkaTrustStorePath;
    }

    @Override
    public String getKafkaTrustStorePassword() {
        return kafkaTrustStorePassword;
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
    public String getConsumerID() {
        return comsumerID;
    }

    @Override
    public Boolean isUseHttpsWithSDC() {
        return useHttpsWithSDC;
    }

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    @Override
    public String getTrustStorePath() {
        return trustStorePath;
    }

    @Override
    public String getTrustStorePassword() {
        return trustStorePassword;
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

    @Override
    public boolean activateServerTLSAuth() {
        return this.activateServerTLSAuth;
    }

    @Override
    public boolean isFilterInEmptyResources() {
        return this.filterInEmptyResources;
    }

    public String getStatusTopicName() {
        return sdcStatusTopicName;
    }

    public void setStatusTopicName(String sdcStatusTopicName) {
        this.sdcStatusTopicName = sdcStatusTopicName;
    }

    public String getNotificationTopicName() {
        return sdcNotificationTopicName;
    }

    public void setNotificationTopicName(String sdcNotificationTopicName) {
        this.sdcNotificationTopicName = sdcNotificationTopicName;
    }

    public List<String> getMsgBusAddress() {
        return msgBusAddressList;
    }

    public void setMsgBusAddress(List<String> newMsgBusAddress) {
        msgBusAddressList = newMsgBusAddress;
    }

    public void setComsumerID(String comsumerID) {
        this.comsumerID = comsumerID;
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

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public void setactivateServerTLSAuth(boolean activateServerTLSAuth) {
        this.activateServerTLSAuth = activateServerTLSAuth;
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

    public void setUseHttpsWithSDC(boolean useHttpsWithSDC) {
        this.useHttpsWithSDC = useHttpsWithSDC;
    }

    @Override
    public boolean isConsumeProduceStatusTopic() {
        return this.consumeProduceStatusTopic;
    }

    @Override
    public String toString() {
        //@formatter:off
        return "Configuration ["
                + "sdcAddress=" + sdcAddress
                + ", user=" + user
                + ", password=" + password
                + ", useHttpsWithSDC=" + useHttpsWithSDC
                + ", pollingInterval=" + pollingInterval
                + ", sdcStatusTopicName=" + sdcStatusTopicName
                + ", sdcNotificationTopicName=" + sdcNotificationTopicName
                + ", pollingTimeout=" + pollingTimeout
                + ", relevantArtifactTypes=" + relevantArtifactTypes
                + ", consumerGroup=" + consumerGroup
                + ", environmentName=" + environmentName
                + ", comsumerID=" + comsumerID
                + ", keyStorePath=" + keyStorePath
                + ", trustStorePath=" + trustStorePath
                + ", activateServerTLSAuth=" + activateServerTLSAuth
                + ", filterInEmptyResources=" + filterInEmptyResources
                + ", consumeProduceStatusTopic=" + consumeProduceStatusTopic
                + ", useSystemProxy=" + useSystemProxy
                + ", httpProxyHost=" + httpProxyHost
                + ", httpProxyPort=" + httpProxyPort
                + ", httpsProxyHost=" + httpsProxyHost
                + ", httpsProxyPort=" + httpsProxyPort
                + "]";
        //@formtter:on
    }

}
