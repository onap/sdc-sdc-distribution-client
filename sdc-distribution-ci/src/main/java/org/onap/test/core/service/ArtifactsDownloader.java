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

import org.apache.commons.io.FileUtils;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ArtifactsDownloader {

    private static final Logger log = LoggerFactory.getLogger(ArtifactsDownloader.class);

    private final String artifactsDownloadPath;
    private final SdcConnectorClient sdcConnectorClient;

    public ArtifactsDownloader(String artifactsDownloadPath,
                               SdcConnectorClient sdcConnectorClient) {
        this.artifactsDownloadPath = artifactsDownloadPath;
        this.sdcConnectorClient = sdcConnectorClient;
    }

    public List<DistributionClientDownloadResultImpl> pullArtifacts(INotificationData service) {
        log.info("Downloading artifacts...");
        return service.getResources().stream()
                .flatMap(this::getArtifactsStream)
                .map(sdcConnectorClient::downloadArtifact)
                .collect(Collectors.toList());
    }

    public void saveArtifacts(List<DistributionClientDownloadResultImpl> artifacts, String serviceName) {
        artifacts.forEach(artifact -> saveArtifact(artifact, serviceName));
    }

    public String parseArtifactName(DistributionClientDownloadResultImpl artifact) {
        return artifact.getArtifactName().split("\"")[1];
    }

    private String getArtifactPath(String serviceName, String artifactName) {
        return String.format("%s/%s/%s", artifactsDownloadPath, serviceName, artifactName);
    }

    private Stream<IArtifactInfo> getArtifactsStream(IResourceInstance resourceInstance) {
        return resourceInstance.getArtifacts().stream();
    }

    private void saveArtifact(DistributionClientDownloadResultImpl artifact, String serviceName) {
        String artifactName = parseArtifactName(artifact);
        String path = getArtifactPath(serviceName, artifactName);

        try {
            File file = new File(path);
            FileUtils.writeByteArrayToFile(file, artifact.getArtifactPayload());
        } catch (IOException e) {
            log.error("Couldn't save an artifact: " + path, e);
        }
    }
}
