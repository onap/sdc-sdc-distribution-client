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
package org.onap.sdc.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpSdcClientResponseTest {
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {HttpStatus.SC_INTERNAL_SERVER_ERROR, "failed to send request"},
            {HttpStatus.SC_BAD_GATEWAY, "failed to connect"},
            {HttpStatus.SC_BAD_GATEWAY, "failed to send request "}
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldCreateHttpResponse(int httpStatusCode, String httpMessage) throws IOException {
        // when
        final HttpSdcResponse response = HttpSdcClient.createHttpResponse(httpStatusCode, httpMessage);

        // then
        assertEquals(httpStatusCode, response.getStatus());
        assertEquals(httpMessage, getResponseMessage(response));
    }

    private String getResponseMessage(HttpSdcResponse response) throws IOException {
        return IOUtils.toString(response.getMessage().getContent(), StandardCharsets.UTF_8);
    }
  }
