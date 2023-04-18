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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.TestConfiguration;
import org.onap.sdc.utils.CaseInsensitiveMap;

@ExtendWith(MockitoExtension.class)
class HttpSdcClientTest {
    private static final String URL = "http://127.0.0.1:8080/target";
    private static final int HTTP_OK = 200;
    private static final String K_1 = "k1";
    private static final String V_1 = "v1";
    private static final String K_2 = "k2";
    private static final String V_2 = "v2";
    private static final Header[] HEADERS = new Header[]{new BasicHeader(K_1, V_1), new BasicHeader(K_2, V_2)};
    private static final CaseInsensitiveMap<String, String> HEADERS_MAP = new CaseInsensitiveMap<String, String>() {{
        put("key1", "key2");
    }};

    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private HttpEntity httpEntity;

    @Test
    void shouldCreateInitializedHttpClient() {
        // given
        TestConfiguration configuration = new TestConfiguration();
        configuration.setUseHttpsWithSDC(true);

        // when
        final HttpRequestFactory httpRequestFactory = new HttpRequestFactory(
            configuration.getUser(),
            configuration.getPassword());
        final HttpSdcClient httpSdcClient = new HttpSdcClient(
                configuration.getSdcAddress(),
            new HttpClientFactory(configuration),
            httpRequestFactory);

        // then
        assertNotNull(httpSdcClient);
        assertEquals(HttpClientFactory.HTTPS, httpSdcClient.getHttpSchema());
    }

    @Test
    void shouldCreateInitializedHttpsClient() {
        // given
        TestConfiguration configuration = new TestConfiguration();
        configuration.setUseHttpsWithSDC(true);

        // when
        final HttpRequestFactory httpRequestFactory = new HttpRequestFactory(
            configuration.getUser(),
            configuration.getPassword());
        final HttpSdcClient httpSdcClient = new HttpSdcClient(
                configuration.getSdcAddress(),
            new HttpClientFactory(configuration),
            httpRequestFactory);

        // then
        assertNotNull(httpSdcClient);
        assertEquals(HttpClientFactory.HTTPS, httpSdcClient.getHttpSchema());
    }

    @Test
    void shouldSendGetRequestWithoutAnyError() throws IOException {
        // given
        TestConfiguration configuration = givenHttpConfiguration();
        final HttpSdcClient httpSdcClient = createTestObj(HttpClientFactory.HTTP, configuration, httpClient);
        CloseableHttpResponse httpResponse = givenHttpResponse(true);

        // when
        final HttpSdcResponse response = httpSdcClient.getRequest(URL, HEADERS_MAP);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HTTP_OK);
        assertThat(response.getHeadersMap()).containsAllEntriesOf(new HashMap<String, String>() {{
            put(K_1, V_1);
            put(K_2, V_2);
        }});
        assertThat(response.getMessage()).isEqualTo(httpEntity);
        verify(httpResponse).close();

    }

    @Test
    void shouldSendPostRequestWithoutAnyError() throws IOException {
        // given
        TestConfiguration configuration = givenHttpConfiguration();
        final HttpSdcClient httpSdcClient = createTestObj(HttpClientFactory.HTTP, configuration, httpClient);
        CloseableHttpResponse httpResponse = givenHttpResponse(false);

        // when
        final HttpSdcResponse response = httpSdcClient.postRequest(URL,httpEntity, HEADERS_MAP);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HTTP_OK);
        assertThat(response.getMessage()).isEqualTo(httpEntity);
        verify(httpResponse).close();

    }

    private HttpSdcClient createTestObj(String httpProtocol, TestConfiguration configuration, CloseableHttpClient httpClient) {
        final HttpRequestFactory httpRequestFactory = new HttpRequestFactory(
            configuration.getUser(),
            configuration.getPassword());
        HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
        when(httpClientFactory.createInstance()).thenReturn(new Pair<>(httpProtocol, httpClient));
        final HttpSdcClient httpSdcClient = new HttpSdcClient(
                configuration.getSdcAddress(),
            httpClientFactory,
            httpRequestFactory);
        return httpSdcClient;
    }

    private CloseableHttpResponse givenHttpResponse(HttpEntity httpEntity, Header[] headers, boolean includeGetAllHeaders) {
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HTTP_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        if (includeGetAllHeaders) {
            when(httpResponse.getAllHeaders()).thenReturn(headers);
        }
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        return httpResponse;
    }

    private TestConfiguration givenHttpConfiguration() {
        TestConfiguration configuration = new TestConfiguration();
        configuration.setUseHttpsWithSDC(false);
        return configuration;
    }

    private CloseableHttpResponse givenHttpResponse(boolean includeGetAllHeaders) throws IOException {
        CloseableHttpResponse httpResponse = givenHttpResponse(httpEntity, HEADERS, includeGetAllHeaders);
        when(httpClient.execute(any())).thenReturn(httpResponse);
        return httpResponse;
    }
}
