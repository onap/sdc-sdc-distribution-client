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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.impl.Configuration;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SetEnvironmentVariable(key = "SASL_JAAS_CONFIG", value = "org.apache.kafka.common.security.scram.ScramLoginModule required username=admin password=admin-secret;")
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
        configuration.setMsgBusAddress(Collections.singletonList(kafkaTestCluster.getKafkaConnectString()));
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

        SdcKafkaProducer producer = new SdcKafkaProducer(configuration);
        producer.send(topicName, "blah", "blah");
        producer.send(topicName, "blah", "blah");
        producer.send(topicName, "blah", "blah");
        producer.flush();

        List<String> events = consumer.poll();

        assertThat(events).hasSize(3);
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