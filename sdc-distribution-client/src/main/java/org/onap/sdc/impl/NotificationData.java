/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2024 Deutsche Telekom Intellectual Property. All rights reserved.
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

import java.util.List;
import java.util.function.Supplier;

import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;

import lombok.Data;

@Data
public class NotificationData implements INotificationData {
  String distributionID;
  String serviceName;
  String serviceVersion;
  String serviceUUID;
  String serviceDescription;
  String serviceInvariantUUID;
  String workloadContext;
  String artifactMetadataByUUID;
  List<IResourceInstance> resources;
  List<IArtifactInfo> serviceArtifacts;

  @Override
  public IArtifactInfo getArtifactMetadataByUUID(String artifactUUID) {
    Supplier<IArtifactInfo> artifactInfoFromResources = () ->
      resources.stream()
        .flatMap(resourceInstance -> resourceInstance.getArtifacts().stream())
        .filter(artifactInfo -> artifactInfo.getArtifactUUID().equals(artifactUUID))
        .findAny()
        .orElse(null);

    return serviceArtifacts.stream()
      .filter(artifactInfo -> artifactInfo.getArtifactUUID().equals(artifactUUID))
      .findAny()
      .orElseGet(artifactInfoFromResources);
  }
}
