/**
 * ============LICENSE_START=======================================================
 * org.onap.sdc
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom AG Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.utils.ArtifactTypeEnum;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.TestConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ExtendWith(MockitoExtension.class)
public class NotificationCallbackBuilderTest {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Mock
  DistributionClientImpl distributionClient;

  NotificationCallbackBuilder notificationCallbackBuilder;

  @BeforeEach
  void setup() {
    notificationCallbackBuilder = new NotificationCallbackBuilder(List.of(ArtifactTypeEnum.HEAT.name()), distributionClient);
  }

  @Test
	final void testBuildCallbackNotificationLogicFlagIsTrue() {
		TestConfiguration testConfiguration = new TestConfiguration();
		testConfiguration.setFilterInEmptyResources(true);
		when(distributionClient.getConfiguration()).thenReturn(testConfiguration);
		when(distributionClient.sendNotificationStatus(any())).thenReturn(new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, ""));
		NotificationDataImpl notification = gson.fromJson(getNotificationWithMultipleResources(), NotificationDataImpl.class);

		NotificationDataImpl notificationBuiltInClient = notificationCallbackBuilder.buildCallbackNotificationLogic(0, notification);
		assertEquals(2, notificationBuiltInClient.getResources().size());
	}

  @Test
	final void testBuildCallbackNotificationLogicFlagIsFalse() {
		TestConfiguration testConfiguration = new TestConfiguration();
		testConfiguration.setFilterInEmptyResources(false);
		when(distributionClient.getConfiguration()).thenReturn(testConfiguration);
		when(distributionClient.sendNotificationStatus(any())).thenReturn(new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, ""));
		NotificationDataImpl notification = gson.fromJson(getNotificationWithMultipleResources(), NotificationDataImpl.class);

		NotificationDataImpl notificationBuiltInClient = notificationCallbackBuilder.buildCallbackNotificationLogic(0, notification);
		assertEquals(1, notificationBuiltInClient.getResources().size());
	}

  private String getNotificationWithMultipleResources(){
		return "{\"distributionID\" : \"bcc7a72e-90b1-4c5f-9a37-28dc3cd86416\",\r\n" +
			"	\"serviceName\" : \"Testnotificationser1\",\r\n" +
			"	\"serviceVersion\" : \"1.0\",\r\n" +
			"	\"serviceUUID\" : \"7f7f94f4-373a-4b71-a0e3-80ae2ba4eb5d\",\r\n" +
			"	\"serviceDescription\" : \"TestNotificationVF1\",\r\n" +
			"	\"resources\" : [{\r\n" +
			"			\"resourceInstanceName\" : \"testnotificationvf11\",\r\n" +
			"			\"resourceName\" : \"TestNotificationVF1\",\r\n" +
			"			\"resourceVersion\" : \"1.0\",\r\n" +
			"			\"resoucreType\" : \"VF\",\r\n" +
			"			\"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2d\",\r\n" +
			"			\"artifacts\" : [{\r\n" +
			"					\"artifactName\" : \"sample-xml-alldata-1-1.xml\",\r\n" +
			"					\"artifactType\" : \"YANG_XML\",\r\n" +
			"					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/sample-xml-alldata-1-1.xml\",\r\n" +
			"					\"artifactChecksum\" : \"MTUxODFkMmRlOTNhNjYxMGYyYTI1ZjA5Y2QyNWQyYTk\\u003d\",\r\n" +
			"					\"artifactDescription\" : \"MyYang\",\r\n" +
			"					\"artifactTimeout\" : 0,\r\n" +
			"					\"artifactUUID\" : \"0005bc4a-2c19-452e-be6d-d574a56be4d0\",\r\n" +
			"					\"artifactVersion\" : \"1\"\r\n" +
			"				}" +
			"			]\r\n" +
			"		},\r\n" +
			"       {\r\n" +
			"			\"resourceInstanceName\" : \"testnotificationvf12\",\r\n" +
			"			\"resourceName\" : \"TestNotificationVF1\",\r\n" +
			"			\"resourceVersion\" : \"1.0\",\r\n" +
			"			\"resoucreType\" : \"VF\",\r\n" +
			"			\"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2e\",\r\n" +
			"			\"artifacts\" : [{\r\n" +
			"					\"artifactName\" : \"heat.yaml\",\r\n" +
			"					\"artifactType\" : \"HEAT\",\r\n" +
			"					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n" +
			"					\"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" +
			"					\"artifactDescription\" : \"heat\",\r\n" +
			"					\"artifactTimeout\" : 60,\r\n" +
			"					\"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" +
			"					\"artifactVersion\" : \"1\"\r\n" +
			"				}" +
			"			]\r\n" +
			"		}\r\n" +
			"	]}";
	}

}
