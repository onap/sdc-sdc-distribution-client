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
package org.onap.test.core.service;

import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.test.core.config.DistributionClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInitializer {

    Logger log = LoggerFactory.getLogger(ClientInitializer.class);
    public static final String SEPARATOR = "========================================";
    private final DistributionClientConfig clientConfig;
    private final ClientNotifyCallback callback;
    private final DistributionClientImpl client;


    public ClientInitializer(DistributionClientConfig clientConfig, ClientNotifyCallback callback, DistributionClientImpl client) {
        this.clientConfig = clientConfig;
        this.callback = callback;
        this.client = client;
    }

    public void initialize() {
        IDistributionClientResult initResult = client.init(clientConfig, callback);
        log.info(initResult.getDistributionMessageResult());
        log.info(SEPARATOR);
        log.info(SEPARATOR);
        IDistributionClientResult startResult = client.start();
        log.info(startResult.getDistributionMessageResult());
        log.info(SEPARATOR);
    }

    public void stop(){
        IDistributionClientResult stopResult = client.stop();
        log.info(SEPARATOR);
        log.info(stopResult.getDistributionMessageResult());
        log.info(SEPARATOR);

    }

}
