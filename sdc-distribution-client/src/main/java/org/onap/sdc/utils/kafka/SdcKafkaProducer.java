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
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.SaslConfigs;
import org.onap.sdc.api.consumer.IConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides a KafkaProducer to communicate with a kafka cluster
 */
public class SdcKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(SdcKafkaProducer.class);
    private static final SdcKafkaProducer INSTANCE = new SdcKafkaProducer();

    private static Producer<String, String> producer;

    private SdcKafkaProducer() {}

    /**
     * get and Singleton instance of the SdcKafkaProducer
     * @param configuration The config provided to the client
     * @return a Singleton SdcKafkaProducer
     */
    public static SdcKafkaProducer getInstance(IConfiguration configuration) {
        if (producer == null) {
            init(configuration);
        }
        return INSTANCE;
    }

    private static void init(IConfiguration configuration) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getMsgBusAddress());
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, getJaasConf());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    private static String getJaasConf() {
        String jassConf = System.getenv("JAASCONFIG");
        if(jassConf != null) {
            return jassConf;
        } else {
            throw new KafkaException("sasl.jaas.config not set for Kafka Producer");
        }
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
        }
        catch (KafkaException e) {
            log.error("Failed the send data: exc {}", e.getMessage());
            throw e;
        }
        return data;
    }

    /**
     *
     */
    public void flush() {
        try {
            producer.flush();
        }
        catch (KafkaException e) {
            log.error("Failed to send data: exc {}", e.getMessage());
        }
    }
}