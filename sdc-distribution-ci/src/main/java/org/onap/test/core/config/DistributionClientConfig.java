/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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
package org.onap.test.core.config;

import org.onap.sdc.api.consumer.IConfiguration;

import java.util.ArrayList;
import java.util.List;


public class DistributionClientConfig implements IConfiguration {

    public static final String DEFAULT_ASDC_ADDRESS = "localhost:30206";
    public static final String DEFAULT_COMSUMER_ID = "dcae-openapi-manager";
    public static final String DEFAULT_CONSUMER_GROUP = "noapp";
    public static final String DEFAULT_ENVIRONMENT_NAME = "AUTO";
    public static final String DEFAULT_PASSWORD = "Kp8bJ4SXszM0WXlhak3eHlcse2gAw84vaoGGmJvUy2U";
    public static final int DEFAULT_POLLING_INTERVAL = 20;
    public static final int DEFAULT_POLLING_TIMEOUT = 20;
    public static final String DEFAULT_USER = "dcae";
    public static final String DEFAULT_KEY_STORE_PATH = "etc/asdc-client.jks";
    public static final String DEFAULT_KEY_STORE_PASSWORD = "Aa123456";
    public static final boolean DEFAULT_ACTIVATE_SERVER_TLS_AUTH = false;
    public static final boolean DEFAULT_IS_FILTER_IN_EMPTY_RESOURCES = true;
    public static final boolean DEFAULT_USE_HTTPS_WITH_SDC = false;
    public static final String DEFAULT_MSG_BUS_ADDRESS = "localhost";
    private String asdcAddress;
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
    private boolean activateServerTLSAuth;
    private boolean isFilterInEmptyResources;
    private boolean useHttpsWithDmaap;
    private boolean useHttpsWithSDC;
    private List<String> msgBusAddress;

    public DistributionClientConfig(IConfiguration other) {
        this.asdcAddress = other.getAsdcAddress();
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
        this.isFilterInEmptyResources = other.isFilterInEmptyResources();
    }

    public DistributionClientConfig() {
        this.asdcAddress = DEFAULT_ASDC_ADDRESS;
        this.comsumerID = DEFAULT_COMSUMER_ID;
        this.consumerGroup = DEFAULT_CONSUMER_GROUP;
        this.environmentName = DEFAULT_ENVIRONMENT_NAME;
        this.password = DEFAULT_PASSWORD;
        this.pollingInterval = DEFAULT_POLLING_INTERVAL;
        this.pollingTimeout = DEFAULT_POLLING_TIMEOUT;
        this.relevantArtifactTypes = new ArrayList<>();
        this.relevantArtifactTypes.add(ArtifactTypeEnum.HEAT.name());
        this.user = DEFAULT_USER;
        this.keyStorePath = DEFAULT_KEY_STORE_PATH;
        this.keyStorePassword = DEFAULT_KEY_STORE_PASSWORD;
        this.activateServerTLSAuth = DEFAULT_ACTIVATE_SERVER_TLS_AUTH;
        this.isFilterInEmptyResources = DEFAULT_IS_FILTER_IN_EMPTY_RESOURCES;
        this.useHttpsWithSDC = DEFAULT_USE_HTTPS_WITH_SDC;
        msgBusAddress = new ArrayList<>();
        msgBusAddress.add(DEFAULT_MSG_BUS_ADDRESS);
        msgBusAddress.add(DEFAULT_MSG_BUS_ADDRESS);
        msgBusAddress.add(DEFAULT_MSG_BUS_ADDRESS);
    }

    @Override
    public String getAsdcAddress() {
        return asdcAddress;
    }

    @Override
    public List<String> getMsgBusAddress() {
        return msgBusAddress;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((asdcAddress == null) ? 0 : asdcAddress.hashCode());
        result = prime * result + ((comsumerID == null) ? 0 : comsumerID.hashCode());
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DistributionClientConfig other = (DistributionClientConfig) obj;
        if (asdcAddress == null) {
            if (other.asdcAddress != null) {
                return false;
            }
        } else if (!asdcAddress.equals(other.asdcAddress)) {
            return false;
        }
        if (comsumerID == null) {
            if (other.comsumerID != null) {
                return false;
            }
        } else if (!comsumerID.equals(other.comsumerID)) {
            return false;
        }
        if (consumerGroup == null) {
            if (other.consumerGroup != null) {
                return false;
            }
        } else if (!consumerGroup.equals(other.consumerGroup)) {
            return false;
        }
        if (environmentName == null) {
            if (other.environmentName != null) {
                return false;
            }
        } else if (!environmentName.equals(other.environmentName)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (pollingInterval != other.pollingInterval) {
            return false;
        }
        if (pollingTimeout != other.pollingTimeout) {
            return false;
        }
        if (relevantArtifactTypes == null) {
            if (other.relevantArtifactTypes != null) {
                return false;
            }
        } else if (!relevantArtifactTypes.equals(other.relevantArtifactTypes)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        if (keyStorePath == null) {
            if (other.keyStorePath != null) {
                return false;
            }
        } else if (!keyStorePath.equals(other.keyStorePath)) {
            return false;
        }
        if (keyStorePassword == null) {
            return other.keyStorePassword == null;
        } else {
            return keyStorePassword.equals(other.keyStorePassword);
        }
    }

    @Override
    public String toString() {
        return "TestConfiguration [asdcAddress=" + asdcAddress + ", user=" + user + ", password=" + password + ", pollingInterval=" + pollingInterval + ", pollingTimeout=" + pollingTimeout + ", relevantArtifactTypes=" + relevantArtifactTypes
                + ", consumerGroup=" + consumerGroup + ", environmentName=" + environmentName + ", comsumerID=" + comsumerID + "]";
    }

    @Override
    public boolean isFilterInEmptyResources() {
        return isFilterInEmptyResources;
    }


    public void setFilterInEmptyResources(boolean isFilterInEmptyResources) {
        this.isFilterInEmptyResources = isFilterInEmptyResources;
    }

    @Override
    public Boolean isUseHttpsWithDmaap() {
        return this.useHttpsWithDmaap;
    }


    public Boolean isUseHttpsWithSDC() {
        return this.useHttpsWithSDC;
    }

    public void setUseHttpsWithSDC(Boolean useHttpsWithSDC) {
        this.useHttpsWithSDC = useHttpsWithSDC;
    }
}
