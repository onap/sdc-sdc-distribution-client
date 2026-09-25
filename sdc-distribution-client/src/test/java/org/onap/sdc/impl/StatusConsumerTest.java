/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2026 Deutsche Telekom. All rights reserved.
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

package org.onap.sdc.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.jupiter.api.Test;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IStatusData;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;

class StatusConsumerTest {

    private final SdcKafkaConsumer kafkaConsumer = mock(SdcKafkaConsumer.class);
    private final IStatusCallback callback = mock(IStatusCallback.class);
    private final StatusConsumer statusConsumer = new StatusConsumer(kafkaConsumer, callback);

    @Test
    void pollOnceHandsStatusToCallback() {
        when(kafkaConsumer.poll()).thenReturn(List.of("{\"distributionID\":\"d1\",\"status\":\"DEPLOY_OK\"}"));

        assertThat(statusConsumer.pollOnce()).isTrue();
        verify(callback).activateCallback(any(IStatusData.class));
    }

    @Test
    void pollOnceReportsFailedPoll() {
        when(kafkaConsumer.poll()).thenThrow(new KafkaException("broker unavailable"));

        assertThat(statusConsumer.pollOnce()).isFalse();
    }

    @Test
    void pollOnceTreatsWakeupAsSuccess() {
        when(kafkaConsumer.poll()).thenThrow(new WakeupException());

        assertThat(statusConsumer.pollOnce()).isTrue();
    }
}
