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
import fj.data.Either;
import org.junit.Test;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NotificationSenderTest {

    private final String status = "status";
    private final CambriaPublisher.message message = new CambriaPublisher.message("sample-partition", "sample-message");
    private final List<CambriaPublisher.message> notEmptySendingFailedMessages = Collections.singletonList(message);
    private final DistributionClientResultImpl successResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
    private final DistributionClientResultImpl generalErrorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");

    private final CambriaBatchingPublisher publisher = mock(CambriaBatchingPublisher.class);
    private final List<String> emptyServers = Collections.emptyList();
    private final NotificationSender validNotificationSender = new NotificationSender(emptyServers);;


    @Test
    public void whenPublisherIsValidAndNoExceptionsAreThrownShouldReturnSuccessStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenReturn(0);
        when(publisher.close(anyLong(), any())).thenReturn(Collections.emptyList());

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(successResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenPublisherCouldNotSendShouldReturnGeneralErrorStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenReturn(0);
        when(publisher.close(anyLong(), any())).thenReturn(notEmptySendingFailedMessages);

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenSendingThrowsIOExceptionShouldReturnGeneralErrorStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenThrow(new IOException());
        when(publisher.close(anyLong(), any())).thenReturn(notEmptySendingFailedMessages);

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenSendingThrowsInterruptedExceptionShouldReturnGeneralErrorStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenAnswer(invocationOnMock -> {throw new InterruptedException();});
        when(publisher.close(anyLong(), any())).thenReturn(notEmptySendingFailedMessages);

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenClosingThrowsIOExceptionShouldReturnGeneralErrorStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenReturn(0);
        when(publisher.close(anyLong(), any())).thenThrow(new IOException());

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    public void whenClosingThrowsInterruptedExceptionShouldReturnGeneralErrorStatus() throws IOException, InterruptedException {
        //given
        when(publisher.send(anyString(), anyString())).thenReturn(0);
        when(publisher.close(anyLong(), any())).thenAnswer(invocationOnMock -> {throw new InterruptedException();});

        //when
        IDistributionClientResult result = validNotificationSender.send(publisher, status);

        //then
        assertEquals(generalErrorResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }
}