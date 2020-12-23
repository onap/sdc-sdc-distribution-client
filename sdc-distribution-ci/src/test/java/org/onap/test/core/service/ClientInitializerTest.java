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

import org.junit.jupiter.api.BeforeEach;
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

    private static final int SUCCESSFUL_STOP_MSG_INDEX = 2;
    private static final int SUCCESSFUL_UNREGISTER_MSG_INDEX = 3;
    private static final int SUCCESSFUL_INIT_MSG_INDEX = 0;
    private static final int SUCCESSFUL_DIST_MSG_INDEX = 3;
    private ClientInitializer clientInitializer;

    @Container
    public GenericContainer mockDmaap = new GenericContainer("registry.gitlab.com/orange-opensource/lfn/onap/mock_servers/mock-dmaap:latest")
            .withNetworkMode("host");

    @Container
    public GenericContainer mockSdc = new GenericContainer("registry.gitlab.com/orange-opensource/lfn/onap/mock_servers/mock-sdc:latest")
            .withNetworkMode("host");
    @Mock
    Logger log;

    @Mock
    Logger distClientLog;

    @BeforeEach
    public void initializeClient() {
        DistributionClientConfig clientConfig = new DistributionClientConfig();
        List<ArtifactsValidator> validators = new ArrayList<>();
        DistributionClientImpl client = new DistributionClientImpl(distClientLog);
        ClientNotifyCallback callback = new ClientNotifyCallback(validators, client);
        clientInitializer = new ClientInitializer(clientConfig, callback, client);
    }

    @Test
    public void shouldRegisterToDmaapAfterClientInitialization() {
        //given
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

    @Test
    public void shouldUnregisterAndStopClient() {
        //given
        final ArgumentCaptor<String> exceptionCaptor = ArgumentCaptor.forClass(String.class);
        //when
        clientInitializer.initialize();
        clientInitializer.stop();
        verify(distClientLog, Mockito.atLeastOnce()).info(exceptionCaptor.capture());
        List<String> allValues = exceptionCaptor.getAllValues();
        //then
        assertThat(allValues.get(SUCCESSFUL_STOP_MSG_INDEX)).isEqualTo("stop DistributionClient");
        assertThat(allValues.get(SUCCESSFUL_UNREGISTER_MSG_INDEX)).isEqualTo("client unregistered from topics successfully");
    }
}