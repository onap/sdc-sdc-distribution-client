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

import java.util.ArrayList;
import java.util.List;
import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.test.core.config.DistributionClientConfig;
import org.onap.test.core.service.ArtifactsValidator;
import org.onap.test.core.service.ClientInitializer;
import org.onap.test.core.service.ClientNotifyCallback;

public class RegisterToSdcTopicIT {


    public static void main(String[] args) {
        DistributionClientConfig clientConfig = new DistributionClientConfig();
        List<ArtifactsValidator> validators = new ArrayList<>();
        DistributionClientImpl client = new DistributionClientImpl();
        ClientNotifyCallback callback = new ClientNotifyCallback(validators, client);
        ClientInitializer clientInitializer = new ClientInitializer(clientConfig, callback, client);
        clientInitializer.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.stop();
            System.out.println("Shutdown Hook is running !");
        }));

    }
}
