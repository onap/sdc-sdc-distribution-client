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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.onap.sdc.config.DistributionClientAutoConfiguration;
import org.onap.sdc.config.DistributionClientProperties;
import org.onap.sdc.producer.NotificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;

@DirtiesContext
@SpringBootTest(
  classes = {
    TestNotificationCallback.class, NotificationListener.class, DistributionClientAutoConfiguration.class, NotificationPublisher.class
  },
  properties = {
    "distribution-client.kafka.bootstrapAddress=${spring.embedded.kafka.brokers}",
    "distribution-client.kafka.topic=someTopic",
    "distribution-client.kafka.groupId=someGroup",
    "debug=true"
  })
@EmbeddedKafka(partitions = 1, topics = {"${distribution-client.kafka.topic}"})
public class NotificationListenerTest {

  @MockBean
  TestNotificationCallback eventCallback;

  @Autowired
  NotificationListener notificationListener;

  @Autowired
  KafkaTemplate<String,String> kafkaTemplate;

  @Autowired
  DistributionClientProperties clientProperties;

  //  @TestConfiguration
  //   public static class WebClientConfiguration {
  //       @Bean
  //       KafkaTemplate<String,String> kafkaTemplate() {
  //         return new KafkaTemplate<>(null)
  //       }
  //   }

  @Test
  void thatCallbackIsInvoked() {
    kafkaTemplate.send(clientProperties.getKafka().getTopic(),"smth");
    verify(eventCallback).activateCallback(any());
  }

  // @Test
  void thatNotificationCanBeConsumed() {
    kafkaTemplate.send("","smth");
    verify(eventCallback).activateCallback(any());
  }

}
