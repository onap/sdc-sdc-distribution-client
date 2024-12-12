/**
 * ============LICENSE_START=======================================================
 * org.onap.sdc
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom AG Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.onap.sdc.IntegrationTest;
import org.onap.sdc.config.DistributionClientAutoConfiguration;
import org.onap.sdc.config.DistributionClientProperties;
import org.onap.sdc.impl.NotificationDataImpl;
import org.onap.sdc.impl.StatusDataImpl;
import org.onap.sdc.producer.NotificationPublisher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@DirtiesContext
@EnableAutoConfiguration
@SpringBootTest(
  classes = {
    StatusListener.class, TestStatusCallback.class, DistributionClientAutoConfiguration.class, NotificationPublisher.class
  })
@EmbeddedKafka(topics = {"${distribution-client.kafka.topics.status}"})
@Import(KafkaTemplateConfiguration.class)
public class StatusListenerTest extends IntegrationTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  TestStatusCallback eventCallback;

  @Autowired
  StatusListener statusListener;

  @Autowired
  KafkaTemplate<String,String> kafkaTemplate;

  @Autowired
  DistributionClientProperties clientProperties;

  @Captor
  private ArgumentCaptor<StatusDataImpl> statusCaptor;

  private CountDownLatch latch;

  @BeforeEach
  public void setup() {
    latch = new CountDownLatch(1);

    doAnswer(invocation -> {
      latch.countDown();
      return null;
    }).when(eventCallback).activateCallback(any());
  }

  @Test
  @SneakyThrows
  void thatStatusCanBeConsumed() {
    Thread.sleep(7000); // broker may not be up without this
    StatusDataImpl status = new StatusDataImpl();
    status.setDistributionID("distributionId");
    kafkaTemplate.send(clientProperties.getKafka().getTopics().getStatus(),mapper.writeValueAsString(status)).get();

    latch.await(15, TimeUnit.SECONDS);
    verify(eventCallback).activateCallback(statusCaptor.capture());
    StatusDataImpl capturedStatus = statusCaptor.getValue();
    assertEquals("distributionId", capturedStatus.getDistributionID());
  }

  @Test
  @SneakyThrows
  void thatArtifactsAreParsed() {
    Thread.sleep(3000); // broker may not be up without this
    String statusJson = "src/test/resources/messages/distributionStatus.json";
    String status = Files.readString(Paths.get(statusJson));
    kafkaTemplate.send(clientProperties.getKafka().getTopics().getStatus(),status).get();

    latch.await(15, TimeUnit.SECONDS);
    verify(eventCallback).activateCallback(statusCaptor.capture());
    StatusDataImpl capturedStatus = statusCaptor.getValue();
    JSONAssert.assertEquals(status, mapper.writeValueAsString(capturedStatus), JSONCompareMode.LENIENT);
  }
}
