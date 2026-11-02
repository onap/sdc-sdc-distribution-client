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

package org.onap.sdc.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

import com.salesforce.kafka.test.KafkaTestCluster;
import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.listeners.BrokerListener;
import com.salesforce.kafka.test.listeners.SaslPlainListener;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.sdc.impl.Configuration;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;
import org.onap.sdc.utils.kafka.KafkaCommonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SdcKafkaTest {

    private static final Logger logger = LoggerFactory.getLogger(SdcKafkaTest.class);

    private static final Configuration configuration = new Configuration(new TestConfiguration());
    private static KafkaTestCluster kafkaTestCluster = null;
    private static final String topicName = "my-test-topic";

    static {
        System.setProperty("java.security.auth.login.config", "src/test/resources/jaas.conf");
    }

    @BeforeAll
    static void before() throws Exception {
        startKafkaService();
        KafkaTestUtils utils = new KafkaTestUtils(kafkaTestCluster);
        utils.createTopic(topicName, 1, (short) 1);
        configuration.setMsgBusAddress(kafkaTestCluster.getKafkaConnectString());
    }

    @AfterAll
    static void after() throws Exception {
        kafkaTestCluster.close();
        kafkaTestCluster.stop();
    }

    @Test
    void whenProducingCorrectRecordsArePresent() {
        SdcKafkaConsumer consumer = new SdcKafkaConsumer(configuration);
        consumer.subscribe(topicName);
        consumer.poll();

        Assertions.assertEquals(configuration.getKafkaConsumerMaxPollInterval(), 600);
        Assertions.assertEquals(configuration.getKafkaConsumerSessionTimeout(), 50);

        SdcKafkaProducer producer = new SdcKafkaProducer(configuration);
        producer.send(topicName, "blah", "blah");
        producer.send(topicName, "blah", "blah");
        producer.send(topicName, "blah", "blah");
        producer.flush();

        List<String> events = consumer.poll();

        assertThat(events).hasSize(3);
    }

    @Test
    void recordPollingRetainsKafkaHeadersAndCoordinates() throws Exception {
        Configuration isolatedConfiguration = new Configuration(new TestConfiguration());
        isolatedConfiguration.setMsgBusAddress(kafkaTestCluster.getKafkaConnectString());
        isolatedConfiguration.setConsumerGroup("metadata-polling-test");
        SdcKafkaConsumer consumer = new SdcKafkaConsumer(isolatedConfiguration);
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(
            new KafkaCommonConfig(isolatedConfiguration).getProducerProperties())) {
            consumer.subscribe(topicName);
            consumer.pollRecords();
            ProducerRecord<String, String> message = new ProducerRecord<>(topicName, "key", "notification");
            String traceparent = "00-11111111111111111111111111111111-2222222222222222-01";
            message.headers().add("traceparent", traceparent.getBytes(StandardCharsets.UTF_8));
            long offset = producer.send(message).get(10, TimeUnit.SECONDS).offset();

            List<ConsumerRecord<String, String>> records = consumer.pollRecords();

            assertThat(records).singleElement().satisfies(record -> {
                assertThat(record.value()).isEqualTo("notification");
                assertThat(record.topic()).isEqualTo(topicName);
                assertThat(record.partition()).isZero();
                assertThat(record.offset()).isEqualTo(offset);
                assertThat(new String(record.headers().lastHeader("traceparent").value(), StandardCharsets.UTF_8))
                        .isEqualTo(traceparent);
            });
        } finally {
            consumer.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"snappy", "lz4"})
    void compressedRecordsRetainPayload(String compressionType) throws Exception {
        Configuration isolatedConfiguration = new Configuration(new TestConfiguration());
        isolatedConfiguration.setMsgBusAddress(kafkaTestCluster.getKafkaConnectString());
        isolatedConfiguration.setConsumerGroup("compression-test-" + compressionType);
        Properties producerProperties = new KafkaCommonConfig(isolatedConfiguration).getProducerProperties();
        producerProperties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        String payload = String.join("", Collections.nCopies(100, "distribution-notification-"));
        SdcKafkaConsumer consumer = new SdcKafkaConsumer(isolatedConfiguration);
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties)) {
            consumer.subscribe(topicName);
            consumer.pollRecords();
            producer.send(new ProducerRecord<>(topicName, "key", payload)).get(10, TimeUnit.SECONDS);

            assertThat(consumer.pollRecords()).singleElement().satisfies(record ->
                assertThat(record.value()).isEqualTo(payload));
        } finally {
            consumer.close();
        }
    }

    @Test
    void wakeupAbortsPoll() {
        SdcKafkaConsumer consumer = new SdcKafkaConsumer(configuration);
        consumer.subscribe(topicName);

        consumer.wakeup();

        Assertions.assertThrows(WakeupException.class, consumer::poll);
    }

    private static void startKafkaService() throws Exception {
        final BrokerListener listener = new SaslPlainListener()
            .withUsername("kafkaclient")
            .withPassword("client-secret");

        final Properties brokerProperties = new Properties();
        kafkaTestCluster = new KafkaTestCluster(
            1,
            brokerProperties,
            Collections.singletonList(listener)
        );

        kafkaTestCluster.start();
        logger.debug("Cluster started at: {}", kafkaTestCluster.getKafkaConnectString());
    }
}