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

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaPublisher;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSender.class);
    private static final long PUBLISHER_CLOSING_TIMEOUT = 10L;
    private static final long SLEEP_TIME = 1;

    private final List<String> brokerServers;

    public NotificationSender(List<String> brokerServers) {
        this.brokerServers = brokerServers;
    }

    public IDistributionClientResult send(CambriaBatchingPublisher publisher, String status) {
        log.info("DistributionClient - sendStatus");
        DistributionClientResultImpl distributionResult;
        try {
            log.debug("Publisher server list: {}", brokerServers);
            log.debug("Trying to send status: {}", status);
            publisher.send("MyPartitionKey", status);
            TimeUnit.SECONDS.sleep(SLEEP_TIME);
        } catch (IOException | InterruptedException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send download status", e);
        } finally {
            distributionResult = closePublisher(publisher);
        }
        return distributionResult;
    }

    private DistributionClientResultImpl closePublisher(CambriaBatchingPublisher publisher) {
        DistributionClientResultImpl distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
        try {
            List<CambriaPublisher.message> notSentMessages = publisher.close(PUBLISHER_CLOSING_TIMEOUT, TimeUnit.SECONDS);
            if (notSentMessages.isEmpty()) {
                distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
            } else {
                log.debug("DistributionClient - sendDownloadStatus. {} messages were not sent", notSentMessages.size());
            }
        } catch (IOException | InterruptedException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send messages and close publisher.", e);
        }
        return distributionResult;
    }
}
