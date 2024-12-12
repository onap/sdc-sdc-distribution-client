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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.onap.sdc.utils.ArtifactTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "distribution-client")
public class DistributionClientProperties {

  @Valid
  private SdcProperties sdc;
  @Valid
  private KafkaProperties kafka;

  @Data
  public static class SdcProperties {
    @NotBlank
    private String url;
    @NotBlank
    private String consumerId;
    @NotEmpty
    private List<ArtifactTypeEnum> relevantArtifactTypes;
  }

  @Data
  public static class KafkaProperties {
    @NotBlank
    private String bootstrapAddress;
    private String topic = "SDC-DISTR-NOTIF-TOPIC-AUTO";
    private String groupId;
    private String defaultDeserializerType = "org.onap.sdc.impl.NotificationDataImpl";
  }

}
