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
import org.openecomp.sdc.api.notification.INotificationData;

public interface IConfiguration {
	/**
	 * SDC Distribution Engine address. Value can be either hostname (with or
	 * without port), IP:port or FQDN (Fully Qualified Domain Name). * @return
	 * SDC Distribution Engine address.
	 */
	String getAsdcAddress();

	/**SDC Distribution Addresses from ONAP Component
	 * Values need to be set from impl
	 */
	List<String> getMsgBusAddress();

	/**
	 * User Name for SDC distribution consumer authentication.
	 * 
	 * @return User Name.
	 */
	String getUser();

	/**
	 * User Password for SDC distribution consumer authentication.
	 * 
	 * @return User Password.
	 */
	String getPassword();

	/**
	 * Distribution Client Polling Interval towards UEB in seconds. Can Be
	 * reconfigured in runtime.
	 * 
	 * @return Distribution Client Polling Interval.
	 */
	int getPollingInterval();

	/**
	 * Distribution Client Timeout in seconds waiting to UEB server response in
	 * each fetch interval. Can Be reconfigured in runtime.
	 * 
	 * @return Distribution Client Timeout in seconds.
	 */
	int getPollingTimeout();

	/**
	 * List of artifact types.<br>
	 * If the service contains any of the artifacts in the list, the callback
	 * will be activated. Can Be reconfigured in runtime.
	 * 
	 * @return List of artifact types.
	 */
	List<String> getRelevantArtifactTypes();

	/**
	 * Returns the consumer group defined for this ECOMP component, if no
	 * consumer group is defined return null.
	 * 
	 * @return Consumer group.
	 */
	String getConsumerGroup();

	/**
	 * Returns the environment name (testing, production etc...). Can Be
	 * reconfigured in runtime.
	 * 
	 * @return
	 */
	String getEnvironmentName();

	/**
	 * Unique ID of ECOMP component instance (e.x INSTAR name).
	 * 
	 * @return
	 */
	String getConsumerID();

	/**
	 * Return full path to Client's Key Store that contains either CA
	 * certificate or the ASDC's public key (e.g /etc/keystore/asdc-client.jks)
	 * file will be deployed with sdc-distribution jar.
	 * 
	 * @return
	 */
	String getKeyStorePath();

	/**
	 * @return Returns client's Key Store password
	 */
	String getKeyStorePassword();

	/**
	 * Sets whether SDC server TLS authentication is activated. If set to false,
	 * Key Store path and password are not needed to be set.
	 * 
	 * @return
	 */
	boolean activateServerTLSAuth();

	/**
	 * If set to true the method {@link INotificationData#getResources()} will
	 * return all found resources.<br>
	 * That means that metadata of resources that do not contain relevant
	 * artifacts types (artifacts that are defined in
	 * {@link #getRelevantArtifactTypes()} will be returned.<br>
	 * Setting the method to false will activate the legacy behavior, in which
	 * empty resources are not part of the notification.<br>
	 * 
	 * @return
	 */
	boolean isFilterInEmptyResources();

	/**
	 * By default, Distribution Client will use HTTPS (TLS 1.2) when connecting
	 * to DMAAP. This param can be null, then default (HTTPS) behavior will be
	 * applied. If set to false, distribution client will use HTTP when
	 * connecting to DMAAP.
	 * 
	 * @return
	 */
	Boolean isUseHttpsWithDmaap();

	/**
	 * By default, (false value) Distribution Client will trigger the regular registration
	 * towards SDC (register component as consumer to the SDC-DISTR-NOTIF-TOPIC-[ENV] topic and register component as producer to the SDC-DISTR-STATUS-TOPIC-[ENV]).<br>
	 * If set to true, distribution client trigger Register to SDC indicating
	 * that this component request to be consumer and producer of the
	 * SDC-DISTR-STATUS-TOPIC-[ENV] topic.<br>
	 * @return
	 */
	default boolean isConsumeProduceStatusTopic() {
		return false;
	}
}
