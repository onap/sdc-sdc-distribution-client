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

package org.openecomp.sdc.http;

import java.util.Map;

import org.apache.http.HttpEntity;

public class HttpAsdcResponse {

	int status;
	HttpEntity message;
	Map<String, String> headersMap;
	
	public HttpAsdcResponse(int status, HttpEntity message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	public HttpAsdcResponse(int status, HttpEntity message, Map<String, String> headersMap) {
		super();
		this.status = status;
		this.message = message;
		this.headersMap = headersMap;
	}

	public Map<String, String> getHeadersMap() {
		return headersMap;
	}

	public void setHeadersMap(Map<String, String> headersMap) {
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
