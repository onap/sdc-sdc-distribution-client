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

package org.onap.sdc.producer;

import java.util.concurrent.ExecutionException;

import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotificationPublisher {

  private final KafkaTemplate<String,StatusMessage> template;

  public IDistributionClientResult publishStatusMessage(String topic, StatusMessage statusMessage) {
    try {
      if(log.isDebugEnabled()) {
        log.debug("Trying to publish status: {}\n to topic: {}", statusMessage, topic);
      } else {
        log.info("Publishing status to topic: {}", topic);
      }
      template.send(topic, statusMessage).get();
      return new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Message successfully sent");
    } catch (InterruptedException | ExecutionException e) {
      log.error("Failed to publish status: {}\nto topic: {}. Reason: {}", statusMessage, topic, e.getMessage());
      return new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
    }

  }
}
