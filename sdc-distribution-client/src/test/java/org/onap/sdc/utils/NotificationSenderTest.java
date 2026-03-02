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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import nl.altindag.log.LogCaptor;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.Test;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;

class NotificationSenderTest {

    private final String status = "status";

    private final DistributionClientResultImpl successResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS,
        "Messages successfully sent");
    private final SdcKafkaProducer producer = mock(SdcKafkaProducer.class);
    private final NotificationSender validNotificationSender = new NotificationSender(producer);

    @Test
    void whenPublisherIsValidAndNoExceptionsAreThrownShouldReturnSuccessStatus() {
        //given       
        RecordMetadata md = mock(RecordMetadata.class);
        Future<RecordMetadata> future = CompletableFuture.completedFuture(md);
        when(producer.send(anyString(), anyString(), anyString())).thenReturn(future);

        //when
        IDistributionClientResult result = validNotificationSender.send("mytopic", status);

        //then
        assertEquals(successResponse.getDistributionActionResult(), result.getDistributionActionResult());
    }

    @Test
    void shouldReturnSuccessWhenSendSucceedsEvenIfFlushFails() throws Exception {
        //given 
        RecordMetadata md = mock(RecordMetadata.class);
        Future<RecordMetadata> future = CompletableFuture.completedFuture(md);
        when(producer.send(anyString(), anyString(), anyString())).thenReturn(future);      
        doThrow(new KafkaException("flush failed"))
            .when(producer)
            .flush();

        
       //when
        IDistributionClientResult result = validNotificationSender.send("mytopic", status);

        // then
        // As send() already succeeded and Kafka acknowledged the message, a failed flush should NOT cause a failure.
        // flush() is best-effort; failing it does not mean the notification failed.

        assertEquals(
            DistributionActionResultEnum.SUCCESS,result.getDistributionActionResult()
        );
    }

    @Test
    void whenSendingThrowsIOExceptionShouldReturnGeneralErrorStatus() {
        LogCaptor logCaptor = LogCaptor.forClass(NotificationSender.class);

        //given
        when(producer.send(anyString(), anyString(), anyString())).thenThrow(new KafkaException("send failed"));

        //when
        validNotificationSender.send("mytopic", status);

        //then

        assertTrue(
                logCaptor.getLogs().stream().anyMatch(
                        msg -> msg.contains("DistributionClient - sendStatus failed to send to Kafka")
                ),
                "Expected log message not found"
        );
    }

}
