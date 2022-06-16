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
package org.onap.test.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.test.core.config.DistributionClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Durations;
import org.testcontainers.utility.DockerImageName;
@Testcontainers
@ExtendWith(MockitoExtension.class)
@SetEnvironmentVariable(key = "JAASCONFIG", value = "org.apache.kafka.common.security.scram.ScramLoginModule required username='admin' password='admin-secret';")
class ClientInitializerTest {

    private static final int SUCCESSFUL_INIT_MSG_INDEX = 0;
    private static final int SUCCESSFUL_DIST_MSG_INDEX = 3;
    private static final int EXPECTED_HEAT_ARTIFACTS = 4;
    private static final DistributionClientConfig clientConfig = new DistributionClientConfig();
    private static final Logger testLog = LoggerFactory.getLogger(ClientInitializerTest.class);
    @Container
    KafkaContainer kafka = buildBrokerInstance();
    @Container
    public GenericContainer<?> mockSdc = new GenericContainer<>("registry.gitlab.com/orange-opensource/lfn/onap/mock_servers/mock-sdc:latest")
        .withNetworkMode("host");

    @Mock
    private Logger log;
    @Mock
    private Logger distClientLog;
    private ClientInitializer clientInitializer;
    private ClientNotifyCallback clientNotifyCallback;
    @BeforeEach
    public void initializeClient() throws Exception {
        clientConfig.setMsgBusAddress(Collections.singletonList(kafka.getBootstrapServers()));
        clientConfig.setUseSystemProxy(false);
        clientConfig.setHttpProxyHost("localhost");
        clientConfig.setHttpProxyPort(30206);
        clientConfig.setHttpsProxyHost("localhost");
        clientConfig.setHttpsProxyPort(30206);
        List<ArtifactsValidator> validators = new ArrayList<>();
        DistributionClientImpl client = new DistributionClientImpl(distClientLog);
        clientNotifyCallback = new ClientNotifyCallback(validators, client);
        clientInitializer = new ClientInitializer(clientConfig, clientNotifyCallback, client);
        setUpTopicsAndSendData();
    }
    protected void setUpTopicsAndSendData() throws Exception {
        Properties adminConfig = new Properties();
        adminConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        adminConfig.put("security.protocol", "SASL_PLAINTEXT");
        adminConfig.put("sasl.mechanism", "PLAIN");
        adminConfig.put(SaslConfigs.SASL_JAAS_CONFIG,"org.apache.kafka.common.security.scram.ScramLoginModule required username='admin' password='admin-secret';");
        AdminClient adminClient = AdminClient.create(adminConfig);
        Collection<NewTopic> topics = Collections.singletonList(new NewTopic("NOTIF-TOPIC", 1, (short) 1));
        adminClient.createTopics(topics).all().get(2, TimeUnit.SECONDS);

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.scram.ScramLoginModule required username='admin' password='admin-secret';");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        String content = Files.readString(Path.of("src/test/resources/artifacts.json"));
        producer.send(new ProducerRecord<>("NOTIF-TOPIC", "testcontainers", content)).get();
    }

    public KafkaContainer buildBrokerInstance() {
        final Map<String, String> env = new LinkedHashMap<>();
        env.put("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:SASL_PLAINTEXT");
        env.put("KAFKA_LISTENER_NAME_PLAINTEXT_SASL_ENABLED_MECHANISMS", "PLAIN");
        env.put("KAFKA_LISTENER_NAME_PLAINTEXT_PLAIN_SASL_JAAS_CONFIG", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"admin\" " +
            "password=\"admin-secret\" " +
            "user_admin=\"admin-secret\" " +
            "user_producer=\"producer-secret\" " +
            "user_consumer=\"consumer-secret\";");

        env.put("KAFKA_SASL_JAAS_CONFIG", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"admin\" " +
            "password=\"admin-secret\" " +
            "user_admin=\"admin-secret\";");

        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEmbeddedZookeeper()
            .withStartupAttempts(1)
            .withEnv(env);
    }


    @Test
    void shouldInitialiseAndStartClientSuccessfully() {
        //given
        final ArgumentCaptor<String> exceptionCaptor = ArgumentCaptor.forClass(String.class);
        //when
        clientInitializer.log = log;
        clientInitializer.initialize();
        verify(log, Mockito.atLeastOnce()).info(exceptionCaptor.capture());
        List<String> allValues = exceptionCaptor.getAllValues();
        //then
        assertThat(allValues.get(SUCCESSFUL_INIT_MSG_INDEX)).isEqualTo("distribution client initialized successfully");
        assertThat(allValues.get(SUCCESSFUL_DIST_MSG_INDEX)).isEqualTo("distribution client started successfully");
    }

    @Test
    void shouldDownloadArtifactsWithArtifactTypeHeat() {
        clientInitializer.initialize();
        waitForArtifacts();
        List<DistributionClientDownloadResultImpl> calls = clientNotifyCallback.getPulledArtifacts();
        Assertions.assertEquals(EXPECTED_HEAT_ARTIFACTS, calls.size());
    }

    private void waitForArtifacts() {
        testLog.info("Waiting for artifacts");
        await()
            .atMost(Durations.ONE_MINUTE)
            .until(() -> !clientNotifyCallback.getPulledArtifacts().isEmpty());
    }
}
