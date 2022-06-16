/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
import java.util.ArrayList;
import java.util.List;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.ArtifactTypeEnum;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotificationConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class.getName());

    private final SdcKafkaConsumer kafkaConsumer;
    private final INotificationCallback clientCallback;
    private final List<String> artifactsTypes;
    private final DistributionClientImpl distributionClient;

    NotificationConsumer(SdcKafkaConsumer kafkaConsumer, INotificationCallback clientCallback, List<String> artifactsTypes, DistributionClientImpl distributionClient) {
        this.kafkaConsumer = kafkaConsumer;
        this.clientCallback = clientCallback;
        this.artifactsTypes = artifactsTypes;
        this.distributionClient = distributionClient;
    }

    @Override
    public void run() {

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            long currentTimeMillis = System.currentTimeMillis();
            for (String notificationMsg : kafkaConsumer.poll()) {
                log.debug("received message from topic");
                log.debug("received notification from broker: {}", notificationMsg);

                final NotificationDataImpl notificationFromMessageBus = gson.fromJson(notificationMsg, NotificationDataImpl.class);
                NotificationDataImpl notificationForCallback = buildCallbackNotificationLogic(currentTimeMillis, notificationFromMessageBus);
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

    protected NotificationDataImpl buildCallbackNotificationLogic(long currentTimeMillis, final NotificationDataImpl notificationFromMessageBus) {
        List<IResourceInstance> relevantResourceInstances = buildResourceInstancesLogic(notificationFromMessageBus, currentTimeMillis);
        List<ArtifactInfoImpl> relevantServiceArtifacts = handleRelevantArtifacts(notificationFromMessageBus, currentTimeMillis, notificationFromMessageBus.getServiceArtifactsImpl());
        notificationFromMessageBus.setResources(relevantResourceInstances);
        notificationFromMessageBus.setServiceArtifacts(relevantServiceArtifacts);
        return notificationFromMessageBus;
    }

    private List<IResourceInstance> buildResourceInstancesLogic(NotificationDataImpl notificationFromMessageBus, long currentTimeMillis) {

        List<IResourceInstance> relevantResourceInstances = new ArrayList<>();

        for (JsonContainerResourceInstance resourceInstance : notificationFromMessageBus.getResourcesImpl()) {
            final List<ArtifactInfoImpl> artifactsImplList = resourceInstance.getArtifactsImpl();
            List<ArtifactInfoImpl> foundRelevantArtifacts = handleRelevantArtifacts(notificationFromMessageBus, currentTimeMillis, artifactsImplList);
            if (!foundRelevantArtifacts.isEmpty() || distributionClient.getConfiguration().isFilterInEmptyResources()) {
                resourceInstance.setArtifacts(foundRelevantArtifacts);
                relevantResourceInstances.add(resourceInstance);
            }
        }
        return relevantResourceInstances;

    }

    private List<ArtifactInfoImpl> handleRelevantArtifacts(NotificationDataImpl notificationFromMessageBus, long currentTimeMillis, final List<ArtifactInfoImpl> artifactsImplList) {
        List<ArtifactInfoImpl> relevantArtifacts = new ArrayList<>();
        if (artifactsImplList != null) {
            for (ArtifactInfoImpl artifactInfo : artifactsImplList) {
                handleRelevantArtifact(notificationFromMessageBus, currentTimeMillis, artifactsImplList, relevantArtifacts, artifactInfo);
            }
        }
        return relevantArtifacts;
    }

    private void handleRelevantArtifact(NotificationDataImpl notificationFromMessageBus, long currentTimeMillis, final List<ArtifactInfoImpl> artifactsImplList, List<ArtifactInfoImpl> relevantArtifacts, ArtifactInfoImpl artifactInfo) {
        boolean isArtifactRelevant = artifactsTypes.contains(artifactInfo.getArtifactType());
        String artifactType = artifactInfo.getArtifactType();
        if (artifactInfo.getGeneratedFromUUID() != null && !artifactInfo.getGeneratedFromUUID().isEmpty()) {
            IArtifactInfo generatedFromArtInfo = findGeneratedFromArtifact(artifactInfo.getGeneratedFromUUID(), artifactsImplList);
            if (generatedFromArtInfo != null) {
                isArtifactRelevant = isArtifactRelevant && artifactsTypes.contains(generatedFromArtInfo.getArtifactType());
            } else {
                isArtifactRelevant = false;
            }
        }
        if (isArtifactRelevant) {
            setRelatedArtifacts(artifactInfo, notificationFromMessageBus);
            if (artifactType.equals(ArtifactTypeEnum.HEAT.name()) || artifactType.equals(ArtifactTypeEnum.HEAT_VOL.name()) || artifactType.equals(ArtifactTypeEnum.HEAT_NET.name())) {
                setGeneratedArtifact(artifactsImplList, artifactInfo);
            }
            relevantArtifacts.add(artifactInfo);

        }
        IDistributionClientResult notificationStatus = distributionClient.sendNotificationStatus(currentTimeMillis, notificationFromMessageBus.getDistributionID(), artifactInfo, isArtifactRelevant);
        if (notificationStatus.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
            log.error("Error failed to send notification status to MessageBus failed status:{}, error message:{}", notificationStatus.getDistributionActionResult().name(), notificationStatus.getDistributionMessageResult());
        }
    }

    private void setRelatedArtifacts(ArtifactInfoImpl artifact, INotificationData notificationData) {
        if (artifact.getRelatedArtifactsUUID() != null) {
            List<IArtifactInfo> relatedArtifacts = new ArrayList<>();
            for (String relatedArtifactUUID : artifact.getRelatedArtifactsUUID()) {
                relatedArtifacts.add(notificationData.getArtifactMetadataByUUID(relatedArtifactUUID));
            }
            artifact.setRelatedArtifactsInfo(relatedArtifacts);
        }

    }

    private void setGeneratedArtifact(final List<ArtifactInfoImpl> artifactsImplList, ArtifactInfoImpl artifactInfo) {
        IArtifactInfo found = null;
        String artifactUUID = artifactInfo.getArtifactUUID();
        for (ArtifactInfoImpl generatedArtifactInfo : artifactsImplList) {
            if (generatedArtifactInfo.getArtifactType().equals(ArtifactTypeEnum.HEAT_ENV.name()) && artifactUUID.equals(generatedArtifactInfo.getGeneratedFromUUID())) {
                found = generatedArtifactInfo;
                break;
            }
        }

        artifactInfo.setGeneratedArtifact(found);
    }

    private IArtifactInfo findGeneratedFromArtifact(String getGeneratedFromUUID, List<ArtifactInfoImpl> list) {
        IArtifactInfo found = null;
        for (ArtifactInfoImpl artifactInfo : list) {
            if (getGeneratedFromUUID.equals(artifactInfo.getArtifactUUID())) {
                found = artifactInfo;
                break;
            }
        }
        return found;
    }

}
