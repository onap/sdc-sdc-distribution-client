package org.onap.sdc.client;

import org.apache.http.HttpStatus;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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
    // switch (responseEntity.getStatusCode()) {
    //   case HttpStatus.SC_UNAUTHORIZED:
    //     return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SDC_AUTHENTICATION_FAILED, "authentication to SDC failed for user " + this.configuration.getUser());
    //     break;

    //   default:
    //     break;
    // }
    new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SDC_AUTHENTICATION_FAILED, "authentication to SDC failed for user ");
  }
}
