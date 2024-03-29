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
package org.onap.sdc.utils.kafka;

import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.onap.sdc.impl.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCommonConfig {
    private static final Logger log = LoggerFactory.getLogger(KafkaCommonConfig.class);

    private final Configuration configuration;
    public KafkaCommonConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    public Properties getConsumerProperties(){
        Properties props = new Properties();
        setCommonProperties(props);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, configuration.getKafkaConsumerMaxPollInterval() * 1000);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID() + "-consumer-" + UUID.randomUUID());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, configuration.getKafkaConsumerSessionTimeout() * 1000);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroup());
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return props;
    }

    public Properties getProducerProperties(){
        Properties props = new Properties();
        setCommonProperties(props);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID() + "-producer-" + UUID.randomUUID());

        return props;
    }

    private void setCommonProperties(Properties props) {
        String securityProtocolConfig = configuration.getKafkaSecurityProtocolConfig();
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocolConfig);
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getMsgBusAddress());

        if("SSL".equals(securityProtocolConfig)) {
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, configuration.getTrustStorePassword());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, configuration.getTrustStorePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, configuration.getKeyStorePassword());
            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, configuration.getKeyStorePath());
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, configuration.getKeyStorePassword());
        } else {
            props.put(SaslConfigs.SASL_JAAS_CONFIG, configuration.getKafkaSaslJaasConfig());
            props.put(SaslConfigs.SASL_MECHANISM, configuration.getKafkaSaslMechanism());
        }
    }

}
