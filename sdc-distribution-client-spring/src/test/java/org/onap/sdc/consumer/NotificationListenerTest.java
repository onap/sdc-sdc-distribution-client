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
    TestNotificationCallback.class, NotificationListener.class, DistributionClientAutoConfiguration.class, NotificationPublisher.class
  })
@EmbeddedKafka(topics = {"${distribution-client.kafka.topics.notification}"})
@Import(KafkaTemplateConfiguration.class)
public class NotificationListenerTest extends IntegrationTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  TestNotificationCallback eventCallback;

  @Autowired
  NotificationListener notificationListener;

  @Autowired
  KafkaTemplate<String,String> kafkaTemplate;

  @Autowired
  DistributionClientProperties clientProperties;

  @Captor
  private ArgumentCaptor<NotificationDataImpl> notificationCaptor;

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
  void thatNotificationCanBeConsumed() {
    Thread.sleep(7000); // broker may not be up without this
    NotificationDataImpl notification = new NotificationDataImpl();
    notification.setDistributionID("distributionId");
    kafkaTemplate.send(clientProperties.getKafka().getTopics().getNotification(),mapper.writeValueAsString(notification)).get();

    latch.await(15, TimeUnit.SECONDS);
    verify(eventCallback).activateCallback(notificationCaptor.capture());
    NotificationDataImpl capturedNotification = notificationCaptor.getValue();
    assertEquals("distributionId", capturedNotification.getDistributionID());
  }

  @Test
  @SneakyThrows
  void thatArtifactsAreParsed() {
    Thread.sleep(3000); // broker may not be up without this
    String examplePath = "src/test/resources/messages/distribution.json";
    String notification = Files.readString(Paths.get(examplePath));
    kafkaTemplate.send(clientProperties.getKafka().getTopics().getNotification(),notification).get();

    latch.await(15, TimeUnit.SECONDS);
    verify(eventCallback).activateCallback(notificationCaptor.capture());
    NotificationDataImpl capturedNotification = notificationCaptor.getValue();
    JSONAssert.assertEquals(notification, mapper.writeValueAsString(capturedNotification), JSONCompareMode.LENIENT);
  }
}
