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

package org.openecomp.sdc.utils;

import java.util.regex.Pattern;

/**
 * Constants Used By Distribution Client
 * @author mshitrit
 *
 */
public final class DistributionClientConstants {
	public static final String CLIENT_DESCRIPTION = "ASDC Distribution Client Key for %s";
	public static final Pattern FQDN_PATTERN = Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*(:[0-9]{2,4})*$", Pattern.CASE_INSENSITIVE);
	public static final String EMAIL = "";
	public static final int MIN_POLLING_INTERVAL_SEC = 15;
	public static final int POOL_SIZE = 10;
	public static final int POLLING_TIMEOUT_SEC = 15;
	
	public static final String HEADER_INSTANCE_ID = "X-ECOMP-InstanceID";
	public static final String HEADER_REQUEST_ID = "X-ECOMP-RequestID";
	public static final String APPLICATION_JSON = "application/json";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	
	private DistributionClientConstants(){ throw new UnsupportedOperationException();}
}
