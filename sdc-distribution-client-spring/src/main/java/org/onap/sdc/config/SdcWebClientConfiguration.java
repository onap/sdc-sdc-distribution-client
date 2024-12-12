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
