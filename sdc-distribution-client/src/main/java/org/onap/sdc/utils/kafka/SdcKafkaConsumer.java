/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2022 Nordix Foundation. All rights reserved.
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.InvalidGroupIdException;
import org.onap.sdc.impl.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides a KafkaConsumer to communicate with a kafka cluster
 */
public class SdcKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SdcKafkaConsumer.class);
    final KafkaConsumer<String, String> consumer;
    private final int pollTimeout;
    private String topicName;

    /**
     *
     * @param configuration The config provided to the client
     */
    public SdcKafkaConsumer(Configuration configuration) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getMsgBusAddress());
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, configuration.getKafkaSecurityProtocolConfig());
        props.put(SaslConfigs.SASL_MECHANISM, configuration.getKafkaSaslMechanism());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, configuration.getKafkaSaslJaasConfig());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroup());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID() + "-consumer-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
        pollTimeout = configuration.getPollingTimeout();
    }

    /**
     *
     * @param topic The kafka topic to subscribe to
     */
    public void subscribe(String topic) {
        try {
            consumer.subscribe(Collections.singleton(topic));
            this.topicName = topic;
        }
        catch (InvalidGroupIdException e) {
            log.error("Invalid Group {}", e.getMessage());
        }
    }

    /**
     *
     * @return The list of records returned from the poll
     */
    public List<String> poll() {
        List<String> msgs = new ArrayList<>();
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(pollTimeout));
        for (ConsumerRecord<String, String> rec : records) {
            msgs.add(rec.value());
        }
        return msgs;
    }

    public String getTopicName() {
        return topicName;
    }
}