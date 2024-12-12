package org.onap.sdc.client;

import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadResultMapper {
  public static DistributionClientDownloadResultImpl toDownloadResult(ResponseEntity<byte[]> responseEntity, IArtifactInfo artifactInfo) {
    if(responseEntity.getStatusCode().is2xxSuccessful()) {
      return successResult(responseEntity, artifactInfo);
    } else {
      return failureResult(responseEntity);
    }
  }

  private static DistributionClientDownloadResultImpl successResult(ResponseEntity<byte[]> responseEntity, IArtifactInfo artifactInfo) {
    String artifactName = getArtifactName(responseEntity);

    return artifactInfo.getArtifactChecksum() != null && !artifactInfo.getArtifactChecksum().isEmpty()
      ? new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "success", artifactName, responseEntity.getBody())
      : new DistributionClientDownloadResultImpl(DistributionActionResultEnum.DATA_INTEGRITY_PROBLEM, "failed to get artifact from SDC. Empty checksum");
  }

  private static String getArtifactName(ResponseEntity<byte[]> responseEntity) {
    String artifactName = responseEntity.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION)
      ? responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
      : "";
    return artifactName;
  }

  private static DistributionClientDownloadResultImpl failureResult(ResponseEntity<byte[]> responseEntity) {
    log.error("Error from SDC is: {}", responseEntity.getBody());
    switch (responseEntity.getStatusCode()) {
      case UNAUTHORIZED:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.SDC_AUTHENTICATION_FAILED,
          "Authentication to SDC failed");
      case FORBIDDEN:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.SDC_AUTHORIZATION_FAILED,
        "Authentication to SDC failed");
      case BAD_REQUEST:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.BAD_REQUEST,
          "Bad request");
      case NOT_FOUND:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.ARTIFACT_NOT_FOUND,
          "Specified artifact could not be found");
      case INTERNAL_SERVER_ERROR:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.SDC_SERVER_PROBLEM,
          "SDC server problem");
      default:
        return new DistributionClientDownloadResultImpl(
          DistributionActionResultEnum.GENERAL_ERROR,
          "Failed to send request to SDC");
    }
  }
}
