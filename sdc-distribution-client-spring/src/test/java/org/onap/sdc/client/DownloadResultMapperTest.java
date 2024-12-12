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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.model.ArtifactInfoImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith(MockitoExtension.class)
public class DownloadResultMapperTest {

  @Mock ResponseEntity<byte[]> responseEntity;
  @Mock ArtifactInfoImpl artifactInfo;

  @Test
  public void testSuccessResult() {
    when(artifactInfo.getArtifactChecksum()).thenReturn("checksum");
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "foo");
    when(responseEntity.getHeaders()).thenReturn(headers);
    when(responseEntity.getBody()).thenReturn("bar".getBytes());

    var downloadResult = DownloadResultMapper.toDownloadResult(responseEntity, artifactInfo);

    assertEquals(DistributionActionResultEnum.SUCCESS, downloadResult.getDistributionActionResult());
    assertEquals("foo", downloadResult.getArtifactName());
    assertArrayEquals("bar".getBytes(), downloadResult.getArtifactPayload());
  }

  @Test
  public void testSuccessResultWithoutContentDisposition() {
    when(artifactInfo.getArtifactChecksum()).thenReturn("checksum");
    HttpHeaders headers = new HttpHeaders();
    when(responseEntity.getHeaders()).thenReturn(headers);
    when(responseEntity.getBody()).thenReturn("bar".getBytes());

    var downloadResult = DownloadResultMapper.toDownloadResult(responseEntity, artifactInfo);

    assertEquals("", downloadResult.getArtifactName());
  }

  @ParameterizedTest
  @CsvSource({
      "UNAUTHORIZED, SDC_AUTHENTICATION_FAILED",
      "FORBIDDEN, SDC_AUTHORIZATION_FAILED",
      "BAD_REQUEST, BAD_REQUEST",
      "NOT_FOUND, ARTIFACT_NOT_FOUND",
      "INTERNAL_SERVER_ERROR, SDC_SERVER_PROBLEM",
      "BAD_GATEWAY, GENERAL_ERROR",
      "GATEWAY_TIMEOUT, GENERAL_ERROR",
  })
  public void testFailureResult(HttpStatus status, DistributionActionResultEnum expectedStatus) {
    var exception = Mockito.mock(WebClientResponseException.class);
    when(exception.getStatusCode()).thenReturn(status);

    var downloadResult = DownloadResultMapper.toDownloadResult(exception);
    assertEquals(expectedStatus, downloadResult.getDistributionActionResult());
  }
}
