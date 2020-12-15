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

import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.http.HttpAsdcClient;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.sdc.utils.DistributionStatusEnum;
import org.onap.test.core.config.DistributionClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

public class ClientNotifyCallback implements INotificationCallback {

    Logger log = LoggerFactory.getLogger(ClientNotifyCallback.class);

    private final List<ArtifactsValidator> validators;
    private final DistributionClientImpl distributionClient;
    DistributionClientConfig config = new DistributionClientConfig();
    HttpAsdcClient asdcClient = new HttpAsdcClient(config);
    SdcConnectorClient sdcConnectorClient = new SdcConnectorClient(config,asdcClient);
    ArtifactsDownloader artifactsDownloader = new ArtifactsDownloader("/app/path", sdcConnectorClient);

    public ClientNotifyCallback(List<ArtifactsValidator> validators, DistributionClientImpl distributionClient) {
        this.validators = validators;
        this.distributionClient = distributionClient;
    }

    @Override
    public void activateCallback(INotificationData inotificationData) {
        logServiceInfo(inotificationData);
        artifactsDownloader.pullArtifacts(inotificationData);
    }

    private void logServiceInfo(INotificationData service) {
        log.info("=================================================");
        log.info("Distrubuted service information");
        log.info("Service UUID: {}", service.getServiceUUID());
        log.info("Service name: {}", service.getServiceName());
        List<IResourceInstance> resources = service.getResources();
        log.info("Service resources:");
        resources.forEach(resource -> {
            log.info(" - Resource: {}", resource.getResourceName());
            log.info("   Artifacts:");
            resource.getArtifacts().forEach(artifact -> log.info("   - Name: {}", artifact.getArtifactName()));
        });
        log.info("=================================================");
    }

    private void validate(INotificationData service) {
        validators.stream()
                .map(validator -> validator.validate(service))
                .flatMap(Collection::stream)
                .forEach(validationResult -> sendNotificationResponse(validationResult, service.getDistributionID()));
    }

    private void sendNotificationResponse(ValidationResult validationResult, String distributionId) {
        if (!validationResult.getMessage().equals(ValidationMessage.VALID)) {
            log.warn("Artifact {} is invalid.", validationResult.getArtifact().getArtifactName());
            log.warn("Validation message: {}", validationResult.getMessage());

            IDistributionStatusMessage message = getDistributionStatusMessage(validationResult, distributionId);
            distributionClient.sendDeploymentStatus(message, "Schema reference is invalid.");
        }
    }

    private DistributionStatusMessage getDistributionStatusMessage(ValidationResult validationResult, String distributionId) {
        return new DistributionStatusMessage(
                validationResult.getArtifact().getArtifactURL(),
                distributionId,
                distributionClient.getConfiguration().getConsumerID(),
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                DistributionStatusEnum.DISTRIBUTION_COMPLETE_ERROR);
    }
}
