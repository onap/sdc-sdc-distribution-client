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
package org.onap.sdc.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SdcWebClientConfiguration {

  @Value("${distribution-client.sdc.url}")
  private String sdcUrl;

  @Value("${distribution-client.sdc.consumerId}")
  private String consumerId;

  @Bean
  WebClient webClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
      .baseUrl(sdcUrl)
      .defaultHeader("X-ECOMP-RequestID", UUID.randomUUID().toString())
      .defaultHeader("X-ECOMP-InstanceID",consumerId )
      .build();
  }
}
