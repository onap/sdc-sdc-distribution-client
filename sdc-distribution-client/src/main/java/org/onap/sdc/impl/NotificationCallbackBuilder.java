package org.onap.sdc.impl;

import java.util.ArrayList;
import java.util.List;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.ArtifactTypeEnum;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotificationCallbackBuilder {

  private final List<String> artifactsTypes;
  private final IDistributionClient distributionClient;

  public NotificationDataImpl buildCallbackNotificationLogic(long currentTimeMillis,
      final NotificationDataImpl notificationFromMessageBus) {
    List<IResourceInstance> relevantResourceInstances = buildResourceInstancesLogic(notificationFromMessageBus,
        currentTimeMillis);
    List<ArtifactInfoImpl> relevantServiceArtifacts = handleRelevantArtifacts(notificationFromMessageBus,
        currentTimeMillis, notificationFromMessageBus.getServiceArtifactsImpl());
    notificationFromMessageBus.setResources(relevantResourceInstances);
    notificationFromMessageBus.setServiceArtifacts(relevantServiceArtifacts);
    return notificationFromMessageBus;
  }

  private List<IResourceInstance> buildResourceInstancesLogic(NotificationDataImpl notificationFromMessageBus,
      long currentTimeMillis) {

    List<IResourceInstance> relevantResourceInstances = new ArrayList<>();

    for (JsonContainerResourceInstance resourceInstance : notificationFromMessageBus.getResourcesImpl()) {
      final List<ArtifactInfoImpl> artifactsImplList = resourceInstance.getArtifactsImpl();
      List<ArtifactInfoImpl> foundRelevantArtifacts = handleRelevantArtifacts(notificationFromMessageBus,
          currentTimeMillis, artifactsImplList);
      if (!foundRelevantArtifacts.isEmpty() || distributionClient.getConfiguration().isFilterInEmptyResources()) {
        resourceInstance.setArtifacts(foundRelevantArtifacts);
        relevantResourceInstances.add(resourceInstance);
      }
    }
    return relevantResourceInstances;

  }

  private List<ArtifactInfoImpl> handleRelevantArtifacts(NotificationDataImpl notificationFromMessageBus,
      long currentTimeMillis, final List<ArtifactInfoImpl> artifactsImplList) {
    List<ArtifactInfoImpl> relevantArtifacts = new ArrayList<>();
    if (artifactsImplList != null) {
      for (ArtifactInfoImpl artifactInfo : artifactsImplList) {
        handleRelevantArtifact(notificationFromMessageBus, currentTimeMillis, artifactsImplList, relevantArtifacts,
            artifactInfo);
      }
    }
    return relevantArtifacts;
  }

  private void handleRelevantArtifact(NotificationDataImpl notificationFromMessageBus, long currentTimeMillis,
      final List<ArtifactInfoImpl> artifactsImplList, List<ArtifactInfoImpl> relevantArtifacts,
      ArtifactInfoImpl artifactInfo) {
    boolean isArtifactRelevant = artifactsTypes.contains(artifactInfo.getArtifactType());
    String artifactType = artifactInfo.getArtifactType();
    if (artifactInfo.getGeneratedFromUUID() != null && !artifactInfo.getGeneratedFromUUID().isEmpty()) {
      IArtifactInfo generatedFromArtInfo = findGeneratedFromArtifact(artifactInfo.getGeneratedFromUUID(),
          artifactsImplList);
      if (generatedFromArtInfo != null) {
        isArtifactRelevant = isArtifactRelevant && artifactsTypes.contains(generatedFromArtInfo.getArtifactType());
      } else {
        isArtifactRelevant = false;
      }
    }
    if (isArtifactRelevant) {
      setRelatedArtifacts(artifactInfo, notificationFromMessageBus);
      if (artifactType.equals(ArtifactTypeEnum.HEAT.name()) || artifactType.equals(ArtifactTypeEnum.HEAT_VOL.name())
          || artifactType.equals(ArtifactTypeEnum.HEAT_NET.name())) {
        setGeneratedArtifact(artifactsImplList, artifactInfo);
      }
      relevantArtifacts.add(artifactInfo);

    }
    DistributionStatusEnum distributionStatus = isArtifactRelevant
      ?  DistributionStatusEnum.NOTIFIED
      : DistributionStatusEnum.NOT_NOTIFIED;
    StatusMessage status = StatusMessage.builder()
      .distributionID(notificationFromMessageBus.getDistributionID())
      .artifactURL(artifactInfo.getArtifactURL())
      .consumerID(distributionClient.getConfiguration().getConsumerID())
      .timestamp(currentTimeMillis)
      .status(distributionStatus)
      .build();
    IDistributionClientResult notificationStatus = distributionClient.sendNotificationStatus(status);
    if (notificationStatus.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
      log.error("Error failed to send notification status to MessageBus failed status:{}, error message:{}",
          notificationStatus.getDistributionActionResult().name(), notificationStatus.getDistributionMessageResult());
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
      if (generatedArtifactInfo.getArtifactType().equals(ArtifactTypeEnum.HEAT_ENV.name())
          && artifactUUID.equals(generatedArtifactInfo.getGeneratedFromUUID())) {
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
