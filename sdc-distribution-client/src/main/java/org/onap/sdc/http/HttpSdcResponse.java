/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import org.apache.http.HttpEntity;

import org.onap.sdc.utils.CaseInsensitiveMap;

public class HttpSdcResponse {

    private int status;
    private HttpEntity message;
    private CaseInsensitiveMap<String, String> headersMap;

    public HttpSdcResponse(int status, HttpEntity message) {
        super();
        this.status = status;
        this.message = message;
    }

    public HttpSdcResponse(int status, HttpEntity message, CaseInsensitiveMap<String, String> headersMap) {
        super();
        this.status = status;
        this.message = message;
        this.headersMap = headersMap;
    }

    public CaseInsensitiveMap<String, String> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(CaseInsensitiveMap<String, String> headersMap) {
        this.headersMap = headersMap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HttpEntity getMessage() {
        return message;
    }

    public void setMessage(HttpEntity message) {
        this.message = message;
    }


}
