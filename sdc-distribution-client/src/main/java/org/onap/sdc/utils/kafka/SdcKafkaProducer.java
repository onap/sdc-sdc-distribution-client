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

import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.onap.sdc.impl.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides a KafkaProducer to communicate with a kafka cluster
 */
public class SdcKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(SdcKafkaProducer.class);
    final KafkaProducer<String, String> producer;
    private final String msgBusAddresses;
    private final String topicName;

    /**
     *
     * @param configuration The config provided to the client
     */
    public SdcKafkaProducer(Configuration configuration) {
        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig(configuration);
        Properties props = kafkaCommonConfig.getProducerProperties();
        producer = new KafkaProducer<>(props);
        msgBusAddresses = configuration.getMsgBusAddress();
        topicName = configuration.getStatusTopicName();
    }

    /**
     *
     * @param topicName The name of the topic to publish to
     * @param key The key value of the ProducerRecord
     * @param value The value of the ProducerRecord
     * @return The RecordMetedata of the request
     */
    public Future<RecordMetadata> send(String topicName, String key, String value) {
        Future<RecordMetadata> data;
        try {
            data = producer.send(new ProducerRecord<>(topicName, key, value));
        } catch (KafkaException e) {
            log.error("Failed the send data: exc {}", e.getMessage());
            throw e;
        }
        return data;
    }

    /**
     * Flush accumulated records in producer
     */
    public void flush() {
        try {
            producer.flush();
        }
        catch (KafkaException e) {
            log.error("Failed to send data: exc {}", e.getMessage());
        }
    }

    /**
     * @return The list of kafka endpoints
     */
    public String getMsgBusAddresses() {
        return msgBusAddresses;
    }

    /**
     * @return The topic name being published to
     */
    public String getTopicName() {
        return topicName;
    }
}