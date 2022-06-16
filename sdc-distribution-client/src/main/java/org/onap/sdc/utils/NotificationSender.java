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

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.KafkaException;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSender.class);
    private static final long SLEEP_TIME = 1;

    private final List<String> brokerServers;

    public NotificationSender(List<String> brokerServers) {
        this.brokerServers = brokerServers;
    }

    public IDistributionClientResult send(SdcKafkaProducer producer, String topic, String status) {
        log.info("DistributionClient - sendStatus");
        DistributionClientResultImpl distributionResult;
        try {
            log.debug("Trying to send status: {}", status);
            producer.send(topic, "MyPartitionKey", status);
            TimeUnit.SECONDS.sleep(SLEEP_TIME);
        } catch (KafkaException | InterruptedException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send download status", e);
        } finally {
            distributionResult = closeProducer(producer);
        }
        return distributionResult;
    }

    private DistributionClientResultImpl closeProducer(SdcKafkaProducer producer) {
        DistributionClientResultImpl distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
        try {
            producer.flush();
            distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
        } catch (KafkaException | IllegalArgumentException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send messages and close publisher.", e);
        }
        return distributionResult;
    }
}
