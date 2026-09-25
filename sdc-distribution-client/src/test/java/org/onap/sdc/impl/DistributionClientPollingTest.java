/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2026 Deutsche Telekom. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.salesforce.kafka.test.KafkaTestCluster;
import com.salesforce.kafka.test.KafkaTestUtils;
import com.salesforce.kafka.test.listeners.SaslPlainListener;
import fj.data.Either;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.utils.TestConfiguration;
import org.onap.sdc.utils.kafka.KafkaCommonConfig;
import org.onap.sdc.utils.kafka.KafkaDataResponse;

@Timeout(120)
class DistributionClientPollingTest {

    private static final String NOTIFICATION_TOPIC = "SDC-DISTR-NOTIF-TOPIC-TEST";
    private static final String STATUS_TOPIC = "SDC-DISTR-STATUS-TOPIC-TEST";
    private static final int POLLING_INTERVAL_SEC = 20;

    private static KafkaTestCluster kafkaTestCluster;

    private final BlockingQueue<String> receivedDistributionIds = new LinkedBlockingQueue<>();
    private final List<DistributionClientImpl> clients = new ArrayList<>();
    private final String consumerGroup = "group-" + UUID.randomUUID();
    private KafkaProducer<String, String> producer;

    static {
        System.setProperty("java.security.auth.login.config", "src/test/resources/jaas.conf");
    }

    @BeforeAll
    static void startKafka() throws Exception {
        kafkaTestCluster = new KafkaTestCluster(1, new Properties(),
            Collections.singletonList(new SaslPlainListener().withUsername("kafkaclient").withPassword("client-secret")));
        kafkaTestCluster.start();
        KafkaTestUtils utils = new KafkaTestUtils(kafkaTestCluster);
        utils.createTopic(NOTIFICATION_TOPIC, 1, (short) 1);
        utils.createTopic(STATUS_TOPIC, 1, (short) 1);
    }

    @AfterAll
    static void stopKafka() throws Exception {
        kafkaTestCluster.close();
    }

    @AfterEach
    void stopClients() {
        clients.forEach(DistributionClientImpl::stop);
        if (producer != null) {
            producer.close();
        }
    }

    @Test
    void notificationFollowingAnotherIsDeliveredWithoutWaitingForPollingInterval() throws InterruptedException {
        startClient();
        assertThat(publishUntilReceived("first", Duration.ofSeconds(60))).isTrue();

        publish("second");

        assertThat(receivedDistributionIds.poll(5, TimeUnit.SECONDS)).isEqualTo("second");
    }

    @Test
    void stopDoesNotWaitForBlockingPoll() throws InterruptedException {
        DistributionClientImpl client = startClient();
        assertThat(publishUntilReceived("first", Duration.ofSeconds(60))).isTrue();

        long start = System.nanoTime();
        client.stop();

        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(5));
    }

    @Test
    void stoppedClientLeavesConsumerGroup() throws InterruptedException {
        DistributionClientImpl stoppedClient = startClient();
        assertThat(publishUntilReceived("first", Duration.ofSeconds(60))).isTrue();
        stoppedClient.stop();

        startClient();

        // well below the session timeout, so the stopped client must have left the group explicitly
        assertThat(publishUntilReceived("second", Duration.ofSeconds(30))).isTrue();
    }

    private DistributionClientImpl startClient() {
        DistributionClientImpl client = spy(new DistributionClientImpl());
        SdcConnectorClient connector = mock(SdcConnectorClient.class);
        when(connector.getValidArtifactTypesList()).thenReturn(Either.left(List.of("HEAT")));
        KafkaDataResponse kafkaData = new KafkaDataResponse();
        kafkaData.setKafkaBootStrapServer(kafkaTestCluster.getKafkaConnectString());
        kafkaData.setDistrNotificationTopicName(NOTIFICATION_TOPIC);
        kafkaData.setDistrStatusTopicName(STATUS_TOPIC);
        when(connector.getKafkaDistData()).thenReturn(Either.left(kafkaData));
        doReturn(connector).when(client).createSdcConnector(any());

        TestConfiguration configuration = new TestConfiguration();
        configuration.setPollingInterval(POLLING_INTERVAL_SEC);
        configuration.setPollingTimeout(POLLING_INTERVAL_SEC);
        configuration.setConsumerGroup(consumerGroup);
        assertSuccess(client.init(configuration, this::onNotification));
        assertSuccess(client.start());
        clients.add(client);

        if (producer == null) {
            Properties props = new KafkaCommonConfig(client.configuration).getProducerProperties();
            // not idempotent: InitProducerId can time out against the freshly started test broker
            props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
            producer = new KafkaProducer<>(props);
        }
        return client;
    }

    private boolean publishUntilReceived(String distributionId, Duration timeout) throws InterruptedException {
        // offsets reset to "latest", so everything sent before the consumer joined its group is skipped
        long deadline = System.nanoTime() + timeout.toNanos();
        String received = null;
        while (received == null && System.nanoTime() < deadline) {
            publish(distributionId);
            received = receivedDistributionIds.poll(500, TimeUnit.MILLISECONDS);
        }
        Thread.sleep(500);
        receivedDistributionIds.clear();
        return received != null;
    }

    private void publish(String distributionId) {
        producer.send(new ProducerRecord<>(NOTIFICATION_TOPIC, "key", "{\"distributionID\":\"" + distributionId + "\","
            + "\"serviceArtifacts\":[{\"artifactName\":\"heat.yaml\",\"artifactType\":\"HEAT\","
            + "\"artifactURL\":\"/heat.yaml\",\"artifactChecksum\":\"abc\",\"artifactUUID\":\"u1\"}],\"resources\":[]}"));
        producer.flush();
    }

    private void onNotification(INotificationData data) {
        receivedDistributionIds.add(data.getDistributionID());
    }

    private static void assertSuccess(IDistributionClientResult result) {
        assertThat(result.getDistributionActionResult())
            .as(result.getDistributionMessageResult())
            .isEqualTo(DistributionActionResultEnum.SUCCESS);
    }
}
