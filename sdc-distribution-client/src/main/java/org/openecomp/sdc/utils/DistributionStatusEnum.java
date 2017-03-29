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

public enum DistributionStatusEnum {
	/**Can be sent  when ECOMP component  successfully  downloaded the specific artifact*/
	DOWNLOAD_OK, 
	
	/**Can be sent when ECOMP component failed to download  the specific artifact (corrupted file)*/
	DOWNLOAD_ERROR, 
	
	/**Can be sent only  if  the  repeated  distribution notification  event is  sent when  the ECOMP component  already  downloaded  the  artifact  , but  still  not  stored it in the  local  repository .*/
	ALREADY_DOWNLOADED, 
	
	/**Can be sent  when ECOMP component  successfully  deployed the specific artifact in the  local repository*/
	DEPLOY_OK, 
	
	/**Can be sent when ECOMP component failed  to  store  the downloaded  artifact  in the local  repository*/
	DEPLOY_ERROR, 
	
	/**Sent  when  the  repeated  distribution notification  event is sent for already  stored  in the  local  repository  service artifact  ( artifact's version and  checksum match the one stored  in the local repository)*/
	ALREADY_DEPLOYED
}
