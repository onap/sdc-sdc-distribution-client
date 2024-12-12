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
package org.onap.sdc.client;

import java.util.List;
import java.util.stream.Collectors;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.IVfModuleMetadata;
import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.config.DistributionClientProperties;
import org.onap.sdc.producer.NotificationPublisher;
import org.onap.sdc.utils.ArtifactTypeEnum;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DistributionClient implements IDistributionClient {

  private final WebClient client;
  private final DistributionClientProperties clientProperties;
  private final NotificationPublisher notificationPublisher;

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
      .onErrorResume(throwable -> Mono.just(DownloadResultMapper.toDownloadResult(throwable)))
      .block();
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

  @Override
  public IDistributionClientResult init(IConfiguration conf, INotificationCallback callback) {
    throw new UnsupportedOperationException("Unimplemented method 'init'");
  }

  @Override
  public IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback,
      IStatusCallback statusCallback) {
    throw new UnsupportedOperationException("Unimplemented method 'init'");
  }

  @Override
  public IConfiguration getConfiguration() {
    return new IConfiguration() {

      @Override
      public String getSdcAddress() {
        return clientProperties.getSdc().getUrl();
      }

      @Override
      public List<String> getRelevantArtifactTypes() {
        return clientProperties.getSdc().getRelevantArtifactTypes().stream()
          .map(ArtifactTypeEnum::name)
          .collect(Collectors.toList());
      }

      @Override
      public String getConsumerGroup() {
        return clientProperties.getKafka().getGroupId();
      }

      @Override
      public String getConsumerID() {
        return clientProperties.getSdc().getConsumerId();
      }

      @Override
      public boolean isFilterInEmptyResources() {
        // TODO: understand what this exactly does and
        // define a (better named!) property in DistributionClientProperties
        return true;
      }

      public String getUser() { throw new UnsupportedOperationException("Unimplemented method 'getUser'"); }

      public String getPassword() { throw new UnsupportedOperationException("Unimplemented method 'getPassword'"); }

      public int getPollingInterval() { throw new UnsupportedOperationException("Unimplemented method 'getPollingInterval'"); }

      public int getPollingTimeout() { throw new UnsupportedOperationException("Unimplemented method 'getPollingTimeout'"); }

      public String getEnvironmentName() { throw new UnsupportedOperationException("Unimplemented method 'getEnvironmentName'"); }

      public String getKeyStorePath() { throw new UnsupportedOperationException("Unimplemented method 'getKeyStorePath'"); }

      public String getKeyStorePassword() { throw new UnsupportedOperationException("Unimplemented method 'getKeyStorePassword'"); }

      public boolean activateServerTLSAuth() { throw new UnsupportedOperationException("Unimplemented method 'activateServerTLSAuth'"); }

      public String getHttpProxyHost() { throw new UnsupportedOperationException("Unimplemented method 'getHttpProxyHost'"); }

      public int getHttpProxyPort() { throw new UnsupportedOperationException("Unimplemented method 'getHttpProxyPort'"); }

      public String getHttpsProxyHost() { throw new UnsupportedOperationException("Unimplemented method 'getHttpsProxyHost'"); }

      public int getHttpsProxyPort() { throw new UnsupportedOperationException("Unimplemented method 'getHttpsProxyPort'"); }

    };
  }

  @Override
  public IDistributionClientResult updateConfiguration(IConfiguration newConf) {
    throw new UnsupportedOperationException("Unimplemented method 'updateConfiguration'");
  }

  @Override
  public IDistributionClientResult start() {
    throw new UnsupportedOperationException("Unimplemented method 'start'");
  }

  @Override
  public IDistributionClientResult stop() {
    throw new UnsupportedOperationException("Unimplemented method 'stop'");
  }

  @Override
  public IDistributionClientResult sendNotificationStatus(StatusMessage statusMessage) {
    return notificationPublisher.publishStatusMessage(clientProperties.getKafka().getTopics().getNotification(), statusMessage);
  }

}
