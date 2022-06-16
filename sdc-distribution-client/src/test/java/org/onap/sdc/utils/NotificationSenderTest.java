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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import nl.altindag.log.LogCaptor;
import org.apache.kafka.common.KafkaException;
import org.junit.Test;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;

public class NotificationSenderTest {

    private final String status = "status";

    private final DistributionClientResultImpl successResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
    private final DistributionClientResultImpl generalErrorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
    private final IConfiguration testConfig = new TestConfiguration();
    private final SdcKafkaProducer producer = mock(SdcKafkaProducer.class);
    private final List<String> emptyServers = Collections.emptyList();
    private final NotificationSender validNotificationSender = new NotificationSender(emptyServers);


    @Test
    public void whenPublisherIsValidAndNoExceptionsAreThrownShouldReturnSuccessStatus() {
        //given
        when(producer.send(anyString(), anyString(), anyString())).thenReturn(mock(Future.class));

        //when
        IDistributionClientResult result = validNotificationSender.send(producer, "mytopic", status);

        //then
        assertEquals(successResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenPublisherCouldNotSendShouldReturnGeneralErrorStatus() {
        //given
        when(producer.send(anyString(), anyString(), anyString())).thenReturn(mock(Future.class));
        doThrow(KafkaException.class)
            .when(producer)
            .flush();

        //when
        IDistributionClientResult result = validNotificationSender.send(producer, "mytopic", status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenSendingThrowsIOExceptionShouldReturnGeneralErrorStatus() {
        LogCaptor logCaptor = LogCaptor.forClass(NotificationSender.class);

        //given
        when(producer.send(anyString(), anyString(), anyString())).thenThrow(new KafkaException());

        //when
        validNotificationSender.send(producer, "mytopic", status);

        //then
        assertThat(logCaptor.getLogs()).contains("DistributionClient - sendDownloadStatus. Failed to send download status");
    }

}