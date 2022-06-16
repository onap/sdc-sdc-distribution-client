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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.InvalidGroupIdException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.onap.sdc.api.consumer.IConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides a KafkaConsumer to communicate with a kafka cluster
 */
public class SdcKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SdcKafkaConsumer.class);
    final KafkaConsumer<String, String> consumer;
    private final int pollTimeout;

    /**
     *
     * @param configuration The config provided to the client
     */
    public SdcKafkaConsumer(IConfiguration configuration) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getMsgBusAddress());
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, getJaasConf());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        pollTimeout = configuration.getPollingTimeout();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroup());
        consumer = new KafkaConsumer<>(props);
    }

    private static String getJaasConf() {
        String jassConf = System.getenv("JAASCONFIG");
        if(jassConf != null) {
            return jassConf;
        } else {
            throw new KafkaException("sasl.jaas.config not set for Kafka Consumer");
        }
    }

    /**
     *
     * @param topics The kafka topic to subscribe to
     */
    public void subscribe(List<String> topics) {
        try {
            consumer.subscribe(topics);
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

    /**
     *
     * @return The list of topics that exist on the kafka cluster
     */
    public Map<String, List<PartitionInfo>> listTopics() {
        Map<String, List<PartitionInfo>> topics = null;
        try {
            topics = consumer.listTopics();
        }
        catch (TopicAuthorizationException | InvalidTopicException e) {
            log.error("Topic Exception {}", e.getMessage());
        }
        return topics;
    }

}