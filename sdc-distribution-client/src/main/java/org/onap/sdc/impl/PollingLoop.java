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

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * Polls back to back until stopped, so that a message is handled as soon as the broker delivers it.
 * Only a failed poll waits for {@code backoffAfterFailure} before the next attempt.
 */
class PollingLoop implements Runnable {

    private final BooleanSupplier pollOnce;
    private final Runnable wakeup;
    private final Duration backoffAfterFailure;
    private final CountDownLatch stopped = new CountDownLatch(1);

    /**
     * @param pollOnce            performs one poll and returns whether it succeeded
     * @param wakeup              aborts a poll that is currently blocking
     * @param backoffAfterFailure delay before polling again after a failed poll
     */
    PollingLoop(BooleanSupplier pollOnce, Runnable wakeup, Duration backoffAfterFailure) {
        this.pollOnce = pollOnce;
        this.wakeup = wakeup;
        this.backoffAfterFailure = backoffAfterFailure;
    }

    @Override
    public void run() {
        try {
            while (!isStopped()) {
                if (!pollOnce.getAsBoolean()) {
                    stopped.await(backoffAfterFailure.toMillis(), TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void stop() {
        stopped.countDown();
        wakeup.run();
    }

    private boolean isStopped() {
        return stopped.getCount() == 0 || Thread.currentThread().isInterrupted();
    }
}
