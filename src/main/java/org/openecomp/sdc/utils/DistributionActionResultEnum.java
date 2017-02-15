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

public enum DistributionActionResultEnum {
	SUCCESS, 
	FAIL,
	GENERAL_ERROR,
	BAD_REQUEST,
	DISTRIBUTION_CLIENT_NOT_INITIALIZED,
	DISTRIBUTION_CLIENT_IS_TERMINATED,
	DISTRIBUTION_CLIENT_ALREADY_INITIALIZED,
	DISTRIBUTION_CLIENT_ALREADY_STARTED,
	
	DATA_INTEGRITY_PROBLEM,
	ARTIFACT_NOT_FOUND,
	
	CONFIGURATION_IS_MISSING,
	CONF_MISSING_USERNAME,
	CONF_MISSING_PASSWORD,
	CONF_MISSING_ASDC_FQDN,
	CONF_MISSING_ARTIFACT_TYPES,
	CONF_CONTAINS_INVALID_ARTIFACT_TYPES,
	CONF_MISSING_CONSUMER_ID,
	CONF_MISSING_ENVIRONMENT_NAME,
	CONF_MISSING_CONSUMER_GROUP, 
	CONF_INVALID_ASDC_FQDN,
	ASDC_AUTHENTICATION_FAILED, 
	ASDC_AUTHORIZATION_FAILED,
	ASDC_NOT_FOUND,
	ASDC_SERVER_PROBLEM,
	ASDC_CONNECTION_FAILED,
	ASDC_SERVER_TIMEOUT,
	
	CAMBRIA_INIT_FAILED,
	UEB_KEYS_CREATION_FAILED
}
