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

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class PollingLoopTest {

    private static final Duration LONG_BACKOFF = Duration.ofMinutes(1);

    private final AtomicInteger polls = new AtomicInteger();
    private final AtomicBoolean wokenUp = new AtomicBoolean();
    private PollingLoop loop;
    private Thread thread;

    @AfterEach
    void stopLoop() throws InterruptedException {
        if (loop != null) {
            loop.stop();
            thread.join(1000);
        }
    }

    @Test
    void pollsAgainWithoutWaitingAfterSuccessfulPoll() throws InterruptedException {
        CountDownLatch fivePolls = new CountDownLatch(5);
        start(() -> {
            polls.incrementAndGet();
            fivePolls.countDown();
            return true;
        });

        assertThat(fivePolls.await(2, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void waitsForBackoffAfterFailedPoll() throws InterruptedException {
        start(() -> {
            polls.incrementAndGet();
            return false;
        });

        Thread.sleep(300);

        assertThat(polls.get()).isEqualTo(1);
    }

    @Test
    void stopDuringBackoffEndsLoopPromptly() throws InterruptedException {
        start(() -> false);
        Thread.sleep(100);

        loop.stop();
        thread.join(1000);

        assertThat(thread.isAlive()).isFalse();
    }

    @Test
    void stopWakesUpConsumerAndEndsLoop() throws InterruptedException {
        start(() -> true);

        loop.stop();
        thread.join(1000);

        assertThat(wokenUp.get()).isTrue();
        assertThat(thread.isAlive()).isFalse();
    }

    private void start(java.util.function.BooleanSupplier pollOnce) {
        loop = new PollingLoop(pollOnce, () -> wokenUp.set(true), LONG_BACKOFF);
        thread = new Thread(loop);
        thread.start();
    }
}
