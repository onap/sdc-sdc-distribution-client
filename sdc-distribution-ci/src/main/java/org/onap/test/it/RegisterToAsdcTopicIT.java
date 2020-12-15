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
package org.onap.test.it;

import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.test.core.config.DistributionClientConfig;
import org.onap.test.core.service.ArtifactsValidator;
import org.onap.test.core.service.ClientInitializer;
import org.onap.test.core.service.ClientNotifyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RegisterToAsdcTopicIT {

    public static final Logger log = LoggerFactory.getLogger(RegisterToAsdcTopicIT.class.getName());


    public static void main(String[] args) {
        log.info("hello");
        DistributionClientConfig clientConfig = new DistributionClientConfig();
        List<ArtifactsValidator> validators = new ArrayList<>();
        DistributionClientImpl client = new DistributionClientImpl();
        ClientNotifyCallback callback = new ClientNotifyCallback(validators, client);
        ClientInitializer clientInitializer = new ClientInitializer(clientConfig, callback, client);
        clientInitializer.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                client.stop();
                System.out.println("Shutdown Hook is running !");
            }
        });

    }
}
