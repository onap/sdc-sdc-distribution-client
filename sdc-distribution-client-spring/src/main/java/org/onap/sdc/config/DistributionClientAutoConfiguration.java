package org.onap.sdc.config;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.client.DistributionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DistributionClientProperties.class)
public class DistributionClientAutoConfiguration {

  private final DistributionClientProperties clientProperties;

  @Bean
  WebClient webClient(WebClient.Builder webClientBuilder) {
    ExchangeFilterFunction xRequestIdHeaderFilter = (clientRequest, nextFilter) -> {
        ClientRequest filteredRequest = ClientRequest.from(clientRequest)
          .header("X-ECOMP-RequestID", UUID.randomUUID().toString())
          .build();
        return nextFilter.exchange(filteredRequest);
    };
    return webClientBuilder
      .baseUrl(clientProperties.getSdcProperties().getUrl())
      .defaultHeader("X-ECOMP-InstanceID",clientProperties.getSdcProperties().getConsumerId() )
      .filter(xRequestIdHeaderFilter)
      .build();
  }

  @Bean
  @ConditionalOnMissingBean(IDistributionClient.class)
  IDistributionClient distributionClient(WebClient webClient) {
    return new DistributionClient(webClient);
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerFactory.class)
  ConsumerFactory<String, String> consumerFactory() {
      Map<String, Object> props = new HashMap<>();
      props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        clientProperties.getKafkaProperties().getBootstrapAddress());
      // props.put(
      //   ConsumerConfig.GROUP_ID_CONFIG,
      //   groupId);
      props.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
      props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
      return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerFactory.class)
  ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

      ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
      factory.setConsumerFactory(consumerFactory());
      return factory;
  }

}
