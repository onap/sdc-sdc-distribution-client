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

package org.openecomp.sdc.api.consumer;

import java.util.List;

public interface IConfiguration {
	/**ASDC Distribution Engine address. 
	 * Value can be either hostname (with or without port), IP:port or FQDN (Fully Qualified Domain Name). */
	String getAsdcAddress(); 
	/** User Name for ASDC distribution consumer authentication. */
	String getUser(); 
	/** User Password for ASDC distribution consumer authentication. */
	String getPassword(); 
	/** Distribution Client Polling Interval towards UEB in seconds. 
	 * Can Be reconfigured in runtime */
	int getPollingInterval(); 
	/** Distribution Client Timeout in seconds waiting to UEB server response in each fetch interval. 
	 * Can Be reconfigured in runtime */
	int getPollingTimeout(); 
	/** List of artifact types.<br>
	 * If the service contains any of the artifacts in the list, the callback will be activated. 
	 * Can Be reconfigured in runtime */
	List<String> getRelevantArtifactTypes();
	/** Returns the consumer group defined for this ECOMP component, if no consumer group is defined return null. */
	String getConsumerGroup();
	/** Returns the environment name (testing, production etc...)
	 * Can Be reconfigured in runtime */
	String getEnvironmentName();
	/**Unique ID of ECOMP component instance (e.x INSTAR name)*/
	String getConsumerID();
	/**Return full path to Client's Key Store that contains either CA certificate or the ASDC's public key (e.g /etc/keystore/asdc-client.jks)
	 * file will be deployed with asdc-distribution jar */
	String getKeyStorePath();
	
	/**return client's Key Store password */
	String getKeyStorePassword();
	
	/**
	 * Sets whether ASDC server TLS authentication is activated.
	 * If set to false, Key Store path and password are not needed to be set. 
	 * @return
	 */
	boolean activateServerTLSAuth();
}


