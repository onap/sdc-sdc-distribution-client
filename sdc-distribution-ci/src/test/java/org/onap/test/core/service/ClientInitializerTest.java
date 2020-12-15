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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.impl.DistributionClientImpl;
import org.onap.test.core.config.DistributionClientConfig;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class ClientInitializerTest {


    public static final int SUCCESSFUL_INIT_MSG_INDEX = 0;
    public static final int SUCCESSFUL_DIST_MSG_INDEX = 3;
    @Container
    public GenericContainer mockDmaap = new GenericContainer("registry.gitlab.com/orange-opensource/lfn/onap/mock_servers/mock-dmaap:latest")
            .withNetworkMode("host");

    @Container
    public GenericContainer mockSdc = new GenericContainer("registry.gitlab.com/orange-opensource/lfn/onap/mock_servers/mock-sdc:latest")
            .dependsOn(mockDmaap)
            .withNetworkMode("host");
    @Mock
    Logger log;

    @Test
    public void shouldRegisterToDmaapAfterClientInitialization() {
        //given
        DistributionClientConfig clientConfig = new DistributionClientConfig();
        List<ArtifactsValidator> validators = new ArrayList<>();
        DistributionClientImpl client = new DistributionClientImpl();
        ClientNotifyCallback callback = new ClientNotifyCallback(validators, client);
        ClientInitializer clientInitializer = new ClientInitializer(clientConfig, callback, client);
        final ArgumentCaptor<String> exceptionCaptor = ArgumentCaptor.forClass(String.class);
        //when
        clientInitializer.log = log;
        clientInitializer.initialize();
        verify(log, Mockito.atLeastOnce()).info(exceptionCaptor.capture());
        List<String> allValues = exceptionCaptor.getAllValues();
        //then
        assertThat(allValues.get(SUCCESSFUL_INIT_MSG_INDEX)).isEqualTo("distribution client initialized successfuly");
        assertThat(allValues.get(SUCCESSFUL_DIST_MSG_INDEX)).isEqualTo("distribution client started successfuly");
    }

}