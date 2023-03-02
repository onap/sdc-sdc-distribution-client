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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Test;
import org.onap.sdc.utils.CaseInsensitiveMap;

class HttpRequestFactoryTest {

    private static final String URL = "https://127.0.0.1:8080/target";
    private static final String EXPECTED_AUTHORIZATION_VALUE = "Basic dXNlcjpwYXNzd29yZA==";
    private static final String HEADER_KEY_1 = "key1";
    private static final String HEADER_KEY_2 = "key2";
    private static final String HEADER_VALUE_1 = "value1";
    private static final String HEADER_VALUE_2 = "value2";
    private static final CaseInsensitiveMap<String, String> HEADERS = new CaseInsensitiveMap<String, String>() {
        {
            put(HEADER_KEY_1, HEADER_VALUE_1);
            put(HEADER_KEY_2, HEADER_VALUE_2);
        }
    };

    private HttpRequestFactory testObj = new HttpRequestFactory("user", "password");

    @Test
    void shouldCreateValidGetHttpRequest() throws URISyntaxException {
        // when
        final HttpGet httpGetRequest = testObj.createHttpGetRequest(URL, HEADERS);

        // then
        assertThat(httpGetRequest.getURI()).isEqualTo(new URI(URL));
        assertThat(httpGetRequest.getFirstHeader(HEADER_KEY_1).getValue())
            .isEqualTo(HEADER_VALUE_1);
        assertThat(httpGetRequest.getFirstHeader(HEADER_KEY_2).getValue())
            .isEqualTo(HEADER_VALUE_2);
        assertThat(httpGetRequest.getFirstHeader(HttpRequestFactory.AUTHORIZATION).getValue())
            .isEqualTo(EXPECTED_AUTHORIZATION_VALUE);
    }

    @Test
    void shouldCreateValidPostHttpRequest() throws URISyntaxException {
        // given
        final HttpEntity httpEntity = mock(HttpEntity.class);

        // when
        final HttpPost httpPostRequest = testObj.createHttpPostRequest(URL, HEADERS, httpEntity);

        // then
        assertThat(httpPostRequest.getURI()).isEqualTo(new URI(URL));
        assertThat(httpPostRequest.getEntity()).isEqualTo(httpEntity);
        assertThat(httpPostRequest.getFirstHeader(HEADER_KEY_1).getValue())
            .isEqualTo(HEADER_VALUE_1);
        assertThat(httpPostRequest.getFirstHeader(HEADER_KEY_2).getValue())
            .isEqualTo(HEADER_VALUE_2);
        assertThat(httpPostRequest.getFirstHeader(HttpRequestFactory.AUTHORIZATION).getValue())
            .isEqualTo(EXPECTED_AUTHORIZATION_VALUE);
    }
}
