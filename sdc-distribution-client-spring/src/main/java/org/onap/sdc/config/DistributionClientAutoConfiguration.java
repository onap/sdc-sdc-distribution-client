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

package org.onap.sdc.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.client.DistributionClient;
import org.onap.sdc.consumer.NotificationListener;
import org.onap.sdc.impl.NotificationCallbackBuilder;
import org.onap.sdc.producer.NotificationPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@EnableKafka
// @EnableWebFlux
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(DistributionClientProperties.class)
public class DistributionClientAutoConfiguration {

  private final DistributionClientProperties clientProperties;

  @Bean
  // @ConditionalOnBean(IDistributionClient.class)
  WebClient distributionClientWebClient(WebClient.Builder webClientBuilder) {
    ExchangeFilterFunction xRequestIdHeaderFilter = (clientRequest, nextFilter) -> {
        ClientRequest filteredRequest = ClientRequest.from(clientRequest)
          .header("X-ECOMP-RequestID", UUID.randomUUID().toString())
          .build();
        return nextFilter.exchange(filteredRequest);
    };
    return webClientBuilder
      .baseUrl(clientProperties.getSdc().getUrl())
      .defaultHeader("X-ECOMP-InstanceID",clientProperties.getSdc().getConsumerId() )
      .filter(xRequestIdHeaderFilter)
      .build();
  }

  @Bean
  NotificationCallbackBuilder distributionClientNotificationCallbackBuilder(IDistributionClient distributionClient) {
    List<String> artifactTypes = clientProperties.getSdc().getRelevantArtifactTypes().stream()
      .map(artifactTypeEnum -> artifactTypeEnum.name())
      .collect(Collectors.toList());
    return new NotificationCallbackBuilder(artifactTypes, distributionClient);
  }

  @Bean
  @ConditionalOnBean(INotificationCallback.class)
  NotificationListener distributionClientNotificationListener(INotificationCallback callback, NotificationCallbackBuilder callbackBuilder) {
    return new NotificationListener(callback, callbackBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerFactory.class)
  @ConditionalOnBean(NotificationListener.class)
  ConsumerFactory<String, String> distributionClientConsumerFactory() {
      Map<String, Object> props = new HashMap<>();
      props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        clientProperties.getKafka().getBootstrapAddress());
      // use the consumerId as a default for the groupId
      String groupId = clientProperties.getKafka().getGroupId() != null
        ? clientProperties.getKafka().getGroupId()
        : clientProperties.getSdc().getConsumerId();
      props.put(
        ConsumerConfig.GROUP_ID_CONFIG,
        groupId);
      props.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
      props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
      props.put(
        JsonDeserializer.VALUE_DEFAULT_TYPE,
        clientProperties.getKafka().getDefaultDeserializerType()
      );
      return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  @ConditionalOnMissingBean(KafkaListenerContainerFactory.class)
  @ConditionalOnBean(NotificationListener.class)
  ConcurrentKafkaListenerContainerFactory<String, String>
  distributionClientKafkaListenerContainerFactory() {

      ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
      factory.setConsumerFactory(distributionClientConsumerFactory());
      return factory;
  }

  @Bean
  @ConditionalOnBean(NotificationPublisher.class)
  ProducerFactory<String, String> distributionClientProducerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, clientProperties.getKafka().getBootstrapAddress());
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

      return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  @ConditionalOnBean(NotificationPublisher.class)
  KafkaTemplate<String, String> distributionClientKafkaTemplate() {
      return new KafkaTemplate<>(distributionClientProducerFactory());
  }

  @Bean
  @ConditionalOnMissingBean(NotificationPublisher.class)
  NotificationPublisher distributionClientNotificationPublisher() {
    return new NotificationPublisher();
  }

  @Bean
  @ConditionalOnMissingBean(IDistributionClient.class)
  IDistributionClient distributionClient(WebClient webClient, NotificationPublisher notificationPublisher) {
    return new DistributionClient(webClient, clientProperties, notificationPublisher);
  }

}
