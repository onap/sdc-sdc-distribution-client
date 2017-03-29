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
import org.openecomp.sdc.api.notification.INotificationData;

public class NotificationCallback extends SimpleCallback{
	INotificationData latestCallbackData;
	public INotificationData getData() {
		return latestCallbackData;
	}
	public NotificationCallback(IDistributionClient client) {
		super(client);
	}
	
	public void activateCallback(INotificationData data) {
		this.latestCallbackData = data;
		super.activateCallback(data);
	}
}
