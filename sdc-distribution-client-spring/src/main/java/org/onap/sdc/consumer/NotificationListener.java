/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
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

import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.impl.NotificationDataImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ConditionalOnBean(INotificationCallback.class)
public class NotificationListener {

  private final INotificationCallback eventCallback;

  @KafkaListener(topics = "${distribution-client.kafka.topic}")
  public void notificationListener(NotificationDataImpl message) {
      eventCallback.activateCallback(message);
  }
}
