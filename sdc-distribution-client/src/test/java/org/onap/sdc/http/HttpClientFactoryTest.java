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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.TestConfiguration;

class HttpClientFactoryTest {

    @Test
    void shouldReturnSSLConnection() {
        TestConfiguration config = spy(new TestConfiguration());
        HttpClientFactory httpClientFactory = new HttpClientFactory(config);
        when(config.activateServerTLSAuth()).thenReturn(true);
        when(config.getKeyStorePath()).thenReturn("src/test/resources/sdc-user-keystore.jks");
        when(config.getKeyStorePassword()).thenReturn("zreRDCnNLsZ7");
        when(config.getTrustStorePath()).thenReturn("src/test/resources/sdc-user-truststore.jks");
        when(config.getTrustStorePassword()).thenReturn("changeit");
        Pair<String, CloseableHttpClient> client = httpClientFactory.createInstance();
        SSLConnectionSocketFactory sslsf = spy(SSLConnectionSocketFactory.getSocketFactory());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        CloseableHttpClient expectedHttpClient = HttpClientBuilder.create().
            setDefaultCredentialsProvider(credsProvider).
            setSSLSocketFactory(sslsf).
            build();
        Pair<String, CloseableHttpClient> expectedClient = new Pair<>("https://", expectedHttpClient);
        assertNotNull(client);
        assertEquals(expectedClient.getFirst(), client.getFirst());
    }

    @Test
    void shouldReturnSSLConnectionWithHttp() {
        TestConfiguration config = spy(new TestConfiguration());
        HttpClientFactory httpClientFactory = new HttpClientFactory(config);
        when(config.activateServerTLSAuth()).thenReturn(false);
        when(config.isUseHttpsWithSDC()).thenReturn(false);
        Pair<String, CloseableHttpClient> client = httpClientFactory.createInstance();
        SSLConnectionSocketFactory sslsf = spy(SSLConnectionSocketFactory.getSocketFactory());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        CloseableHttpClient expectedHttpClient = HttpClientBuilder.create().
            setDefaultCredentialsProvider(credsProvider).
            setSSLSocketFactory(sslsf).
            build();
        Pair<String, CloseableHttpClient> expectedClient = new Pair<>("http://", expectedHttpClient);
        assertNotNull(client);
        assertEquals(expectedClient.getFirst(), client.getFirst());
    }

    @Test
    void shouldReturnSSLConnectionError() throws HttpSdcClientException{
        TestConfiguration config = spy(new TestConfiguration());
        HttpClientFactory httpClientFactory = new HttpClientFactory(config);
        when(config.activateServerTLSAuth()).thenReturn(true);
        when(config.getKeyStorePath()).thenReturn("src/test/resources/dummy.jks");
        when(config.getKeyStorePassword()).thenReturn("Aa123456");
        assertThrows(HttpSdcClientException.class, () -> httpClientFactory.createInstance());
    }

}
