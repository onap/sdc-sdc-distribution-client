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

package org.openecomp.sdc.impl;

import org.openecomp.sdc.api.consumer.IStatusCallback;
import org.openecomp.sdc.api.notification.IStatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.cambria.client.CambriaConsumer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class StatusConsumer implements Runnable {

	private static Logger log = LoggerFactory.getLogger(StatusConsumer.class.getName());

	private CambriaConsumer cambriaConsumer;
	private IStatusCallback clientCallback;

	public StatusConsumer(CambriaConsumer cambriaConsumer, IStatusCallback clientCallback) {
		this.cambriaConsumer = cambriaConsumer;
		this.clientCallback = clientCallback;
	}

	@Override
	public void run() {

		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			for (String statusMsg : cambriaConsumer.fetch()) {
				log.debug("received message from topic");
				log.debug("recieved notification from broker: {}", statusMsg);
				IStatusData statusData = gson.fromJson(statusMsg, StatusDataImpl.class);
				clientCallback.activateCallback(statusData);
				
				
			}

		} catch (Exception e) {
			log.error("Error exception occured when fetching with Cambria Client:{}", e.getMessage());
			log.debug("Error exception occured when fetching with Cambria Client:{}", e.getMessage(), e);
		}
	}



}
