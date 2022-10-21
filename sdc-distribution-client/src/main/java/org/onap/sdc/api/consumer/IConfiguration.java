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

package org.onap.sdc.api.consumer;

import java.util.List;

import org.apache.kafka.common.KafkaException;
import org.onap.sdc.api.notification.INotificationData;

public interface IConfiguration {
    /**
     * SDC Distribution Engine address. Value can be either hostname (with or
     * without port), IP:port or FQDN (Fully Qualified Domain Name). * @return SDC
     * Distribution Engine address.
     */
    String getSdcAddress();

    /**
     * Kafka security protocol to be used by the client to Auth towards the kafka cluster
     *
     * @return Kafka security.protocol. Default is SASL_PLAINTEXT
     */
    default String getKafkaSecurityProtocolConfig() {
        return System.getenv().getOrDefault("SECURITY_PROTOCOL", "SASL_PLAINTEXT");
    }

    /**
     * Kafka SASL mechanism to be used by the client to Auth towards the kafka cluster
     *
     * @return Kafka sasl.mechanism. Default is SCRAM-SHA-512
     */
    default String getKafkaSaslMechanism() {
        return System.getenv().getOrDefault("SASL_MECHANISM", "SCRAM-SHA-512");
    }

    /**
     * Kafka JAAS config to be used by the client to Auth towards the kafka cluster.
     * If overridden, must align with sasl.jaas.config convention set out by the sasl.mechanism being used
     * otherwise, mandatory setting of the environment variable SASL_JAAS_CONFIG is required to provide default behaviour
     * @return Kafka sasl.jaas.config
     */
    default String getKafkaSaslJaasConfig() {
        String saslJaasConfFromEnv = System.getenv("SASL_JAAS_CONFIG");
        if(saslJaasConfFromEnv != null) {
            return saslJaasConfFromEnv;
        } else {
            throw new KafkaException("sasl.jaas.config not set for Kafka Consumer");
        }
    }

    /**
     * User Name for SDC distribution consumer authentication.
     *
     * @return User Name.
     */
    String getUser();

    /**
     * Return True if ssl is needed, false otherwise. This param can be null, then
     * default (HTTPS) behavior will be applied. If set to false, distribution
     * client will use HTTP when connecting to SDC.
     *
     * @return default is true
     */
    default Boolean isUseHttpsWithSDC() {
        return true;
    }

    /**
     * User Password for SDC distribution consumer authentication.
     *
     * @return User Password.
     */
    String getPassword();

    /**
     * Distribution Client Polling Interval towards messaging bus in seconds. Can Be
     * reconfigured in runtime.
     *
     * @return Distribution Client Polling Interval.
     */
    int getPollingInterval();

    /**
     * Distribution Client Timeout in seconds waiting for messaging bus server response in each
     * fetch interval. Can Be reconfigured in runtime.
     *
     * @return Distribution Client Timeout in seconds.
     */
    int getPollingTimeout();

    /**
     * List of artifact types.<br>
     * If the service contains any of the artifacts in the list, the callback will
     * be activated. Can Be reconfigured in runtime.
     *
     * @return List of artifact types.
     */
    List<String> getRelevantArtifactTypes();

    /**
     * Returns the kafka consumer group defined for this component.
     *
     * @return SdcKafkaConsumer group.
     */
    String getConsumerGroup();

    /**
     * Returns the environment name (testing, production etc...). Can Be
     * reconfigured in runtime.
     *
     * @return Environment name
     */
    String getEnvironmentName();

    /**
     * Returns the kafka consumer id defined for this component.
     *
     * @return SdcKafkaConsumer id.
     */
    String getConsumerID();

    /**
     * Return full path to Client's Key Store that contains either CA certificate or
     * the SDC's public key (e.g /etc/keystore/sdc-client.jks) file will be
     * deployed with sdc-distribution jar.
     *
     * @return Client's Key store path
     */
    String getKeyStorePath();

    /**
     * Return the password for the Client's Key Store
     *
     * @return Client's Key Store password
     */
    String getKeyStorePassword();

    /**
     * Sets whether SDC server TLS authentication is activated. If set to false, Key
     * Store path and password are not needed to be set.
     *
     * @return true by default
     */
    boolean activateServerTLSAuth();

    /**
     * If set to true the method {@link INotificationData#getResources()} will
     * return all found resources.<br>
     * That means that metadata of resources that do not contain relevant artifacts
     * types (artifacts that are defined in {@link #getRelevantArtifactTypes()} will
     * be returned.<br>
     * Setting the method to false will activate the legacy behavior, in which empty
     * resources are not part of the notification.<br>
     *
     * @return true or false
     */
    boolean isFilterInEmptyResources();

    /**
     * By default: false. Distribution Client will trigger the regular
     * registration towards SDC (register component as consumer to the
     * SDC-DISTR-NOTIF-TOPIC-[ENV] topic and register component as producer to the
     * SDC-DISTR-STATUS-TOPIC-[ENV]).<br>
     * If set to true, distribution client trigger Register to SDC indicating that
     * this component request to be consumer and producer of the
     * SDC-DISTR-STATUS-TOPIC-[ENV] topic.<br>
     *
     * @return false by default
     */
    default boolean isConsumeProduceStatusTopic() {
        return false;
    }

    /**
     * By default: false. If set to true, Distribution Client will use System wide
     * available proxies from JVM arguments. If set to false, distribution client
     * will use proxy parameters configured through properties file.
     *
     * @return false by default
     */
    default Boolean isUseSystemProxy() {
        return false;
    }

    /**
     * Optional configuration parameter. If the httpProxyHost parameter is
     * configured and {@link #isUseHttpsWithSDC()} is false then SDC Distribution
     * Client will register the proxy configuration with the HttpClient instance
     * using HTTP and route requests through the proxy.
     * 
     * @return Hostname of the http proxy
     */
    String getHttpProxyHost();

    /**
     * Mandatory configuration parameter if httpProxyHost is configured. If the
     * httpProxyHost and httpProxyPort parameters are configured and
     * {@link #isUseHttpsWithSDC()} is false then SDC Distribution Client will
     * register the proxy configuration with the HttpClient instance using HTTP and
     * route requests through the proxy.
     * 
     * @return Port of the http proxy
     */
    int getHttpProxyPort();

    /**
     * Optional configuration parameter. If the httpsProxyHost parameter is
     * configured and {@link #isUseHttpsWithSDC()} is true then SDC Distribution
     * Client will register the proxy configuration with the HttpClient instance
     * using HTTPS and route requests through the proxy.
     * 
     * @return Hostname of the https proxy
     */
    String getHttpsProxyHost();

    /**
     * Mandatory configuration parameter if httpsProxyHost is configured. If the
     * httpsProxyHost and httpsProxyPort parameters are configured and
     * {@link #isUseHttpsWithSDC()} is true then SDC Distribution Client will
     * register the proxy configuration with the HttpClient instance using HTTPS and
     * route requests through the proxy.
     * 
     * @return Port of the https proxy
     */
    int getHttpsProxyPort();
}
