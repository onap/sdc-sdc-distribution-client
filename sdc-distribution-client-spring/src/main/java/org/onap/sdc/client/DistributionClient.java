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
package org.onap.sdc.client;

import java.net.URI;
import java.util.List;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.IVfModuleMetadata;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class DistributionClient implements IDistributionClient {

  private final WebClient client;

  @Override
  public IDistributionClientResult updateConfiguration(IConfiguration newConf) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateConfiguration'");
  }

  @Override
  public IConfiguration getConfiguration() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getConfiguration'");
  }

  @Override
  public IDistributionClientResult start() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'start'");
  }

  @Override
  public IDistributionClientResult stop() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'stop'");
  }

  @Override
  @SneakyThrows
  public IDistributionClientDownloadResult download(IArtifactInfo artifactInfo) {
    return client
      .get()
      .uri(artifactInfo.getArtifactURL())
      .accept(MediaType.APPLICATION_OCTET_STREAM)
      .retrieve()
      .toEntity(byte[].class)
      .map(responseEntity -> DownloadResultMapper.toDownloadResult(responseEntity, artifactInfo))
      .block();
  }

  @Override
  public IDistributionClientResult init(IConfiguration conf, INotificationCallback callback) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'init'");
  }

  @Override
  public IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback,
      IStatusCallback statusCallback) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'init'");
  }

  @Override
  public IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendDownloadStatus'");
  }

  @Override
  public IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage, String errorReason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendDownloadStatus'");
  }

  @Override
  public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendDeploymentStatus'");
  }

  @Override
  public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage, String errorReason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendDeploymentStatus'");
  }

  @Override
  public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendComponentDoneStatus'");
  }

  @Override
  public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage,
      String errorReason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendComponentDoneStatus'");
  }

  @Override
  public IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendFinalDistrStatus'");
  }

  @Override
  public IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage, String errorReason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendFinalDistrStatus'");
  }

  @Override
  public List<IVfModuleMetadata> decodeVfModuleArtifact(byte[] artifactPayload) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'decodeVfModuleArtifact'");
  }

}
