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


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.onap.sdc.utils.CaseInsensitiveMap;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequestFactory {

    public static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_AUTH_FORMAT = "%s:%s";
    private final String authHeaderValue;

    public HttpRequestFactory(String user, String password) {
        this.authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(createAuthHeaderData(user, password));
    }

    public HttpGet createHttpGetRequest(String url, CaseInsensitiveMap<String, String> headersMap) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeaders(createHttpRequestHeaders(headersMap, authHeaderValue));

        return httpGet;
    }

    public HttpPost createHttpPostRequest(String url, CaseInsensitiveMap<String, String> headersMap, HttpEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(createHttpRequestHeaders(headersMap, authHeaderValue));
        httpPost.setEntity(entity);

        return httpPost;
    }

    private Header[] createHttpRequestHeaders(CaseInsensitiveMap<String, String> headersMap, String authorizationValue) {
        final List<Header> headers = headersMap.entrySet().stream()
                .map(it -> new BasicHeader(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
        headers.add(new BasicHeader(AUTHORIZATION, authorizationValue));
        return convertToArray(headers);
    }

    private Header[] convertToArray(List<Header> headers) {
        return headers.toArray(new Header[0]);
    }

    private byte[] createAuthHeaderData(String user, String password) {
        return (String.format(BASIC_AUTH_FORMAT, user, password)).getBytes();
    }
}
