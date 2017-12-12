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

package org.openecomp.test;

import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.consumer.IComponentDoneStatusMessage;
import org.openecomp.sdc.api.consumer.IStatusCallback;
import org.openecomp.sdc.api.notification.INotificationData;
import org.openecomp.sdc.api.notification.IStatusData;
import org.openecomp.sdc.api.results.IDistributionClientResult;
import org.openecomp.sdc.impl.DistributionClientFactory;
import org.openecomp.sdc.utils.DistributionStatusEnum;

public class ClientTest {
	public static void main(String[] args) throws Exception {

		msoWdListner();
		clientSender();

	}

	private static void clientSender() {
		IDistributionClient client = DistributionClientFactory.createDistributionClient();
		IDistributionClientResult result = client.init(new SimpleConfiguration(), new SimpleCallback(client));
		System.err.println("Init Status: " + result.toString());
		
		IDistributionClientResult start = client.start();

		System.err.println("Start Status: " + start.toString());
		for( int i = 0; i < 2; i++ ){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.sendComponentDoneStatus(new IComponentDoneStatusMessage() {
				
				@Override
				public long getTimestamp() {
					return System.currentTimeMillis();
				}
				
				@Override
				public DistributionStatusEnum getStatus() {
					return DistributionStatusEnum.COMPONENT_DONE_OK;
				}
				
				@Override
				public String getDistributionID() {
					// TODO Auto-generated method stub
					return "";
				}
				
				@Override
				public String getConsumerID() {
					return client.getConfiguration().getConsumerID();
				}
				
				@Override
				public String getComponentName() {
					return "MSO";
				}
			});
		}
		
	}

	private static void msoWdListner() {
		IDistributionClient client = DistributionClientFactory.createDistributionClient();
		IDistributionClientResult result = client.init(new SimpleConfiguration() {
			@Override
			public boolean isConsumeProduceStatusTopic() {
				return true;
			}
		}, new SimpleCallback(client) {
			@Override
			public void activateCallback(INotificationData data) {
				System.err.println("Monitor Recieved Notification: " + data.toString());

			}
		}, new IStatusCallback() {

			@Override
			public void activateCallback(IStatusData data) {
				System.err.println("Monitor Recieved Status: " + data.toString());

			}
		});
		System.err.println("Init Status: " + result.toString());
		IDistributionClientResult start = client.start();

		System.err.println("Start Status: " + start.toString());
	}

}
