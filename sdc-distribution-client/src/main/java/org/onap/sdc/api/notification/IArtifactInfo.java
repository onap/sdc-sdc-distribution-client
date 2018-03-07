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

package org.onap.sdc.api.notification;

import java.util.List;


public interface IArtifactInfo {
	
	/**
	 * @return String of the Artifact File Name.
	 */
	String getArtifactName();
	
	/**
	 * Artifact Type.<br>
	 * Following are  valid  values :   HEAT , DG_XML. <br>
	 * List of values will be extended in post-1510 releases.
	 * 
	 * @return String of the artifact type.
	 */
	String getArtifactType();
	
	/**
	 * Relative artifact's URL. Should be used in REST GET API to download the artifact's payload.<br> 
	 * The full  artifact URL  will be  in the following format :<br>
	 * https://{serverBaseURL}/{resourcePath}<br>
	 * serverBaseURL  - Hostname ( SDC LB FQDN)  + optional port <br>
	 * resourcePath -  "artifactURL"  <br>
	 * Ex : https://sdc.com/v1/catalog/services/srv1/2.0/resources/aaa/1.0/artifacts/aaa.yml
	 * 
	 * @return String of the Relative artifact's URL.
	 */
	String getArtifactURL();
	
	/**
	 * Base-64 encoded MD5 checksum of the artifact's payload.<br>
 	 * Should be used for data integrity validation when an artifact's payload is downloaded.<br>
	 * 
	 * @return String of the checksum of the artifact's payload.
	 */
	String getArtifactChecksum();
	
	/**
	 * Installation timeout in minutes.<br>
	 * Used by the Orchestrator to determine how much time to wait for a heat (or other deployment artifact)<br>
	 * This field is only relevant for artifacts of ArtifactTypeEnum HEAT, for other artifacts it will be null.<br>
	 * deployment process to finish.<br>
	 * 
	 * @return String of the Installation timeout in minutes.
	 */
	Integer getArtifactTimeout();
	
	/**
	 * Artifact description
	 * @return
	 */
	String getArtifactDescription();
	
	/**
	 * Artifact Version
	 * @return
	 */
	String getArtifactVersion();
	
	/**
	 * Artifact Unique ID
	 * @return
	 */
	String getArtifactUUID();

	
	/**
	 * Returns the artifact it is generated from (relevant for heat_env), or null if there is no such artifact.
	 * @return
	 */
	IArtifactInfo getGeneratedArtifact(); 
	
	/**
	 * Returns the list of related artifacts (relevant for HEAT_NESTED or HEAT_ARTIFACT), or null if there is no such artifacts.
	  * @return
	 */
	List<IArtifactInfo> getRelatedArtifacts(); 
	
	
}
