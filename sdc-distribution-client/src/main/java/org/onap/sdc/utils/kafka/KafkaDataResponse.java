package org.onap.sdc.utils.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaDataResponse {

    private String kafkaBootStrapServer;
    private String distrNotificationTopicName;
    private String distrStatusTopicName;
}