/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights reserved.
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

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StatusDataImplTest {

    private static final String COMPONENT_NAME = "componentName";
    private static final String ERROR_REASON = "errorReason";

    @Test
    public void shouldHaveValidGettersAndSetters() {
        assertThat(StatusDataImpl.class, hasValidGettersAndSettersExcluding(COMPONENT_NAME, ERROR_REASON));
    }

    @Test
    public void shouldHaveValidToString() {
        assertThat(StatusDataImpl.class, hasValidBeanToStringExcluding(COMPONENT_NAME, ERROR_REASON));
    }
}