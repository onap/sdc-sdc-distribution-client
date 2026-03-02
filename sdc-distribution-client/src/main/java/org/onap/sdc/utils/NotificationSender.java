/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSender.class);
    private final SdcKafkaProducer producer;

    public NotificationSender(SdcKafkaProducer producer) {
        this.producer = producer;
    }

    public IDistributionClientResult send(String topic, String status) {
        log.info("DistributionClient - sendStatus");
        DistributionClientResultImpl distributionResult;
        boolean sendFailed = false;
        try {
            log.debug("Publisher server list: {}", producer.getMsgBusAddresses());
            log.info("Trying to send status: {} \n to topic {}", status, producer.getTopicName());    
            Future<RecordMetadata> future = producer.send(topic, "MyPartitionKey", status);
            RecordMetadata md = future.get(10, TimeUnit.SECONDS);
            log.debug("Kafka ack received. topic={}, partition={}, offset={}, ts={}",
                  md.topic(), md.partition(), md.offset(), md.timestamp());

        } catch ( InterruptedException ie) {         
            Thread.currentThread().interrupt();
            sendFailed = true;
            log.error("DistributionClient - sendStatus interrupted while waiting for Kafka ack", ie);
        } catch (TimeoutException | ExecutionException | KafkaException e) {
            sendFailed = true;
            log.error("DistributionClient - sendStatus failed to send to Kafka", e);
       }  finally {
            
                try {
                        producer.flush();
                    } catch (KafkaException e) {
                        log.error("DistributionClient - flush encountered an error", e);
                    }
                
                if (sendFailed) {
                            distributionResult = new DistributionClientResultImpl(
                                        DistributionActionResultEnum.FAIL, "Failed to send status");
                        } else {
                            distributionResult = new DistributionClientResultImpl(
                                    DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
                        }

        }
        return distributionResult;
    }

}
