/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpAsdcClient implements IHttpAsdcClient {

    private static final Logger log = LoggerFactory.getLogger(HttpAsdcClient.class.getName());
    private static final boolean ALWAYS_CLOSE_THE_REQUEST_CONNECTION = true;
    private final CloseableHttpClient httpClient;
    private final String httpSchema;
    private final String serverFqdn;
    private final HttpRequestFactory httpRequestFactory;

    @Deprecated
    public HttpAsdcClient(IConfiguration configuration) {
        this(configuration.getAsdcAddress(),
                new HttpClientFactory(configuration),
                new HttpRequestFactory(configuration.getUser(), configuration.getPassword())
        );
    }

    public HttpAsdcClient(String asdcAddress, HttpClientFactory httpClientFactory, HttpRequestFactory httpRequestFactory) {
        this.serverFqdn = asdcAddress;
        this.httpRequestFactory = httpRequestFactory;

        Pair<String, CloseableHttpClient> httpClientPair = httpClientFactory.createInstance();
        this.httpSchema = httpClientPair.getFirst();
        this.httpClient = httpClientPair.getSecond();
    }

    public HttpAsdcResponse postRequest(String requestUrl, HttpEntity entity, Map<String, String> headersMap) {
        return postRequest(requestUrl, entity, headersMap, ALWAYS_CLOSE_THE_REQUEST_CONNECTION).getFirst();
    }

    public Pair<HttpAsdcResponse, CloseableHttpResponse> postRequest(String requestUrl, HttpEntity entity, Map<String, String> headersMap, boolean closeTheRequest) {
        Pair<HttpAsdcResponse, CloseableHttpResponse> ret;
        final String url = resolveUrl(requestUrl);
        log.debug("url to send {}", url);
        HttpPost httpPost = httpRequestFactory.createHttpPostRequest(url, headersMap, entity);

        CloseableHttpResponse httpResponse = null;
        HttpAsdcResponse response = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            response = new HttpAsdcResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getEntity());
        } catch (IOException e) {
            log.error("failed to send request to url: {}", requestUrl);
            response = createHttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "failed to send request");
        } finally {
            ret = finalizeHttpRequest(closeTheRequest, httpResponse, response);
        }

        return ret;
    }

    public HttpAsdcResponse getRequest(String requestUrl, Map<String, String> headersMap) {
        return getRequest(requestUrl, headersMap, ALWAYS_CLOSE_THE_REQUEST_CONNECTION).getFirst();
    }

    public Pair<HttpAsdcResponse, CloseableHttpResponse> getRequest(String requestUrl, Map<String, String> headersMap, boolean closeTheRequest) {
        Pair<HttpAsdcResponse, CloseableHttpResponse> ret;

        final String url = resolveUrl(requestUrl);
        log.debug("url to send {}", url);
        HttpGet httpGet = httpRequestFactory.createHttpGetRequest(url, headersMap);

        CloseableHttpResponse httpResponse = null;
        HttpAsdcResponse response = null;
        try {
            httpResponse = httpClient.execute(httpGet);

            log.debug("GET Response Status {}", httpResponse.getStatusLine().getStatusCode());
            Header[] headersRes = httpResponse.getAllHeaders();
            Map<String, String> headersResMap = new HashMap<>();
            for (Header header : headersRes) {
                headersResMap.put(header.getName(), header.getValue());
            }
            response = new HttpAsdcResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getEntity(), headersResMap);

        } catch (UnknownHostException | ConnectException e) {
            log.error("failed to connect to url: {}", requestUrl, e);
            response = createHttpResponse(HttpStatus.SC_BAD_GATEWAY, "failed to connect");
        } catch (IOException e) {
            log.error("failed to send request to url: {} error {}", requestUrl, e.getMessage());
            response = createHttpResponse(HttpStatus.SC_BAD_GATEWAY, "failed to send request " + e.getMessage());
        } finally {
            ret = finalizeHttpRequest(closeTheRequest, httpResponse, response);
        }

        return ret;
    }

    String getHttpSchema(){
        return this.httpSchema;
    }

    private String resolveUrl(String requestUrl) {
        return this.httpSchema + serverFqdn + requestUrl;
    }

    private Pair<HttpAsdcResponse, CloseableHttpResponse> finalizeHttpRequest(boolean closeTheRequest, CloseableHttpResponse httpResponse, HttpAsdcResponse response) {
        Pair<HttpAsdcResponse, CloseableHttpResponse> ret;
        if (closeTheRequest) {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("failed to close http response");
                }
            }
            ret = new Pair<>(response, null);
        } else {
            ret = new Pair<>(response, httpResponse);
        }

        return ret;
    }

    static HttpAsdcResponse createHttpResponse(int httpStatusCode, String httpMessage) {
        return new HttpAsdcResponse(httpStatusCode, new StringEntity(httpMessage, StandardCharsets.UTF_8));
    }

    public void closeHttpClient() {
        try {
            httpClient.close();
        } catch (IOException e) {
            log.error("failed to close http client");
        }
    }
}
