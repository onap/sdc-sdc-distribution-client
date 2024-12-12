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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.onap.sdc.config.DistributionClientAutoConfiguration;
import org.onap.sdc.model.ArtifactInfoImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@EnableAutoConfiguration
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 0)
@SpringBootTest(
  classes = {DistributionClientAutoConfiguration.class, DistributionClient.class},
  properties = {
    "distribution-client.sdc.url=http://localhost:${wiremock.server.port}",
    "distribution-client.sdc.consumerId=someConsumer",
})
public class DistributionClientTest {

  @Autowired
  DistributionClient client;

  @Test
  void thatArtifactsCanBeDownloaded() throws IOException {
    stubFor(get(urlEqualTo("/sdc/v1/catalog/services/DemovlbCds/1.0/artifacts/service-TestSvc-csar.csar"))
      .withHeader("Accept", equalTo(MediaType.APPLICATION_OCTET_STREAM_VALUE))
      .withHeader("X-ECOMP-RequestID", matching(".+"))
      .withHeader("X-ECOMP-InstanceID", matching(".+"))
      .willReturn(aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .withHeader("Content-Disposition", "csar")
        .withBodyFile("service-TestSvc-csar.csar")));

    ArtifactInfoImpl artifactInfo = new ArtifactInfoImpl();
    artifactInfo.setArtifactName("service-TestSvc-csar.csar");
    artifactInfo.setArtifactVersion("1.0");
    artifactInfo.setArtifactURL("/sdc/v1/catalog/services/DemovlbCds/1.0/artifacts/service-TestSvc-csar.csar");
    artifactInfo.setArtifactType("TOSCA_CSAR");
    artifactInfo.setArtifactChecksum("ZmI5NzQ1MWViZGFkMjRjZWEwNTQzY2U0OWQwYjlmYjQ=");
    artifactInfo.setArtifactUUID("f6f907f1-3f45-4fb4-8cbe-15a4c6ee16db");

    var downloadResult = client.download(artifactInfo);

    Path path = Paths.get("src/test/resources/__files/service-TestSvc-csar.csar");
    byte[] expected = Files.readAllBytes(path);
    assertEquals("csar", downloadResult.getArtifactFilename());
    assertArrayEquals(expected, downloadResult.getArtifactPayload());
  }

  @Test
  void thatArtifactsCanNotBeDownloaded() {
    stubFor(
      get(urlEqualTo("/sdc/v1/catalog/services/DemovlbCds/1.0/artifacts/service-TestSvc-csar.csar"))
        .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));
    ArtifactInfoImpl artifactInfo = new ArtifactInfoImpl();
    artifactInfo.setArtifactURL("/sdc/v1/catalog/services/DemovlbCds/1.0/artifacts/service-TestSvc-csar.csar");

    var downloadResult = client.download(artifactInfo);
    assertEquals(DistributionActionResultEnum.ARTIFACT_NOT_FOUND, downloadResult.getDistributionActionResult());
  }
}
