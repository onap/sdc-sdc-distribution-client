/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.onap.sdc.config.SdcWebClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@AutoConfigureWebClient
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = {SdcWebClientConfiguration.class, ArtifactTypesService.class})
public class ArtifactTypesServiceTest {

  @Value("${distribution-client.sdc.consumerId}")
  private String consumerId;

  @Autowired
  ArtifactTypesService artifactTypesService;

  @Test
  void thatArtifactTypesCanBeRetrieved() {
    stubFor(get(urlEqualTo("/sdc/v1/artifactTypes"))
        .withHeader("X-ECOMP-RequestID", matching(".+"))
        .withHeader("X-ECOMP-InstanceID", equalTo(consumerId))
        .willReturn(
          aResponse()
          .withHeader("Content-Type", "application/json")
          .withBodyFile("artifactTypes.json")));


    List<String> artifactTypesList = artifactTypesService.getArtifactTypes();
    assertNotNull(artifactTypesList);
    // assertEquals(1109582923, artifactTypesList.getChecksumMap().get("v29"));
  }
}
