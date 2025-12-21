/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2024 Deutsche Telekom. All rights reserved.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;

import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotificationConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class.getName());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final SdcKafkaConsumer kafkaConsumer;
    private final INotificationCallback clientCallback;
    private final NotificationCallbackBuilder callbackBuilder;

    NotificationConsumer(SdcKafkaConsumer kafkaConsumer, INotificationCallback clientCallback, List<String> artifactsTypes, DistributionClientImpl distributionClient) {
        this.kafkaConsumer = kafkaConsumer;
        this.clientCallback = clientCallback;
        this.callbackBuilder = new NotificationCallbackBuilder(artifactsTypes, distributionClient);
    }

    @Override
    public void run() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            log.debug("Polling for messages from topic: {}", kafkaConsumer.getTopicName());
            for (String notificationMsg : kafkaConsumer.poll()) {
                log.debug("received message from topic");
                log.debug("received notification from broker: {}", notificationMsg);

                final NotificationDataImpl notificationFromMessageBus = gson.fromJson(notificationMsg, NotificationDataImpl.class);
                NotificationDataImpl notificationForCallback = callbackBuilder.buildCallbackNotificationLogic(currentTimeMillis, notificationFromMessageBus);
                if (isActivateCallback(notificationForCallback)) {
                    String stringNotificationForCallback = gson.toJson(notificationForCallback);
                    log.debug("sending notification to client: {}", stringNotificationForCallback);
                    clientCallback.activateCallback(notificationForCallback);
                }
            }

        } catch (Exception e) {
            log.error("Error exception occurred when fetching with Kafka Consumer:{}", e.getMessage());
            log.debug("Error exception occurred when fetching with Kafka Consumer:{}", e.getMessage(), e);
        }
    }

    private boolean isActivateCallback(NotificationDataImpl notificationForCallback) {
        boolean hasRelevantArtifactsInResourceInstance = notificationForCallback.getResources() != null && !notificationForCallback.getResources().isEmpty();
        boolean hasRelevantArtifactsInService = notificationForCallback.getServiceArtifacts() != null && !notificationForCallback.getServiceArtifacts().isEmpty();

        return hasRelevantArtifactsInResourceInstance || hasRelevantArtifactsInService;
    }
}
