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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.impl.ArtifactInfoImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@ExtendWith(MockitoExtension.class)
public class DownloadResultMapperTest {

  @Mock ResponseEntity<byte[]> responseEntity;
  @Mock ArtifactInfoImpl artifactInfo;

  @Test
  public void testSuccessResult() {
    when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
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
    when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(artifactInfo.getArtifactChecksum()).thenReturn("checksum");
    HttpHeaders headers = new HttpHeaders();
    when(responseEntity.getHeaders()).thenReturn(headers);
    when(responseEntity.getBody()).thenReturn("bar".getBytes());

    var downloadResult = DownloadResultMapper.toDownloadResult(responseEntity, artifactInfo);

    assertEquals("", downloadResult.getArtifactName());
  }
}
