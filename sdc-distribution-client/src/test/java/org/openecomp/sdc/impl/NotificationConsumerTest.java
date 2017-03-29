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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.util.ArrayQueue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openecomp.sdc.api.consumer.INotificationCallback;
import org.openecomp.sdc.api.notification.INotificationData;
import org.openecomp.sdc.api.results.IDistributionClientResult;
import org.openecomp.sdc.utils.ArtifactTypeEnum;
import org.openecomp.sdc.utils.DistributionActionResultEnum;
import org.openecomp.sdc.utils.DistributionClientConstants;
import org.openecomp.sdc.utils.TestConfiguration;

import com.att.nsa.cambria.client.CambriaConsumer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NotificationConsumerTest {
	private CambriaConsumer cambriaConsumer = mock(CambriaConsumer.class);
	private INotificationCallback clientCallback = spy(INotificationCallback.class);
	private Queue<Iterable<String>> notificationsQueue = new ArrayQueue<>(100);
	private DistributionClientImpl distributionClient = spy(DistributionClientImpl.class);
	private List<String> artifactsTypes = Arrays.asList(ArtifactTypeEnum.HEAT.name());
	private List<Boolean> notificationStatusResults = new ArrayList<>();
	final static IDistributionClientResult DISTRIBUTION_SUCCESS_RESULT = buildSuccessResult();

	private NotificationConsumer createNotificationConsumer() {
		return new NotificationConsumer(cambriaConsumer, clientCallback, artifactsTypes, distributionClient);
	}

	@Before
	public void beforeTest() throws IOException {
		Mockito.reset(clientCallback, distributionClient);
		when(cambriaConsumer.fetch()).then(new Answer<Iterable<String>>() {
			@Override
			public Iterable<String> answer(InvocationOnMock invocation) throws Throwable {
				if (!notificationsQueue.isEmpty()) {
					return notificationsQueue.remove();
				} else {
					return new ArrayList<>();
				}
			}
		});
		when(distributionClient.sendNotificationStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ArtifactInfoImpl.class), Mockito.anyBoolean())).then(new Answer<IDistributionClientResult>() {
			@Override
			public IDistributionClientResult answer(InvocationOnMock invocation) throws Throwable {
				boolean isNotified = (boolean) invocation.getArguments()[3];
				notificationStatusResults.add(Boolean.valueOf(isNotified));
				return DISTRIBUTION_SUCCESS_RESULT;
			}
		});

	}

	private static IDistributionClientResult buildSuccessResult() {
		return new IDistributionClientResult() {

			@Override
			public String getDistributionMessageResult() {
				return "";
			}

			@Override
			public DistributionActionResultEnum getDistributionActionResult() {
				return DistributionActionResultEnum.SUCCESS;
			}
		};
	}

	@Test
	public void testNoNotifiactionsSent() throws InterruptedException {

		ScheduledExecutorService executorPool = Executors.newScheduledThreadPool(DistributionClientConstants.POOL_SIZE);
		executorPool.scheduleAtFixedRate(createNotificationConsumer(), 0, 100, TimeUnit.MILLISECONDS);

		Thread.sleep(1000);
		executorPool.shutdown();

		Mockito.verify(clientCallback, Mockito.times(0)).activateCallback(Mockito.any(INotificationData.class));

	}

	@Test
	public void testNonRelevantNotificationSent() throws InterruptedException {

		simulateNotificationFromUEB(getAsdcServiceNotificationWithoutHeatArtifact());
		Mockito.verify(clientCallback, Mockito.times(0)).activateCallback(Mockito.any(INotificationData.class));

	}

	@Test
	public void testRelevantNotificationSent() throws InterruptedException {
		simulateNotificationFromUEB(getAsdcServiceNotificationWithHeatArtifact());
		Mockito.verify(clientCallback, Mockito.times(1)).activateCallback(Mockito.any(INotificationData.class));

	}

	@Test
	public void testNonExistingArtifactsNotificationSent() throws InterruptedException {
		simulateNotificationFromUEB(getAsdcNotificationWithNonExistentArtifact());
		Mockito.verify(clientCallback, Mockito.times(1)).activateCallback(Mockito.any(INotificationData.class));

	}

	@Test
	public void testNotificationStatusSent() throws InterruptedException {
		simulateNotificationFromUEB(getAsdcServiceNotificationWithHeatArtifact());

		Mockito.verify(distributionClient, Mockito.times(3)).sendNotificationStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ArtifactInfoImpl.class), Mockito.anyBoolean());
		assertTrue(countInstances(notificationStatusResults, Boolean.TRUE) == 1);
		assertTrue(countInstances(notificationStatusResults, Boolean.FALSE) == 2);
	}

	@Test
	public void testNotificationRelatedArtifacts() throws InterruptedException {
		List<String> artifactTypesTmp = new ArrayList<>();
		for (ArtifactTypeEnum artifactTypeEnum : ArtifactTypeEnum.values()) {
			artifactTypesTmp.add(artifactTypeEnum.name());
		}
		artifactsTypes = artifactTypesTmp;
		simulateNotificationFromUEB(getAsdcServiceNotificationWithRelatedArtifacts());

		Mockito.verify(distributionClient, Mockito.times(3)).sendNotificationStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ArtifactInfoImpl.class), Mockito.anyBoolean());
		assertTrue(countInstances(notificationStatusResults, Boolean.TRUE) == 3);
		assertTrue(countInstances(notificationStatusResults, Boolean.FALSE) == 0);
	}

	@Test
	public void testNotificationStatusWithServiceArtifatcs() throws InterruptedException {
		simulateNotificationFromUEB(getNotificationWithServiceArtifatcs());
		Mockito.verify(distributionClient, Mockito.times(6)).sendNotificationStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ArtifactInfoImpl.class), Mockito.anyBoolean());
		assertTrue(countInstances(notificationStatusResults, Boolean.TRUE) == 2);
		assertTrue(countInstances(notificationStatusResults, Boolean.FALSE) == 4);

	}
	
	@Test
	public final void testBuildCallbackNotificationLogicFlagIsFalse() {
		NotificationConsumer consumer = createNotificationConsumer();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		TestConfiguration testConfiguration = new TestConfiguration();
		testConfiguration.setFilterInEmptyResources(false);
		when(distributionClient.getConfiguration()).thenReturn(testConfiguration);
		NotificationDataImpl notification = gson.fromJson(getNotificationWithMultipleResources(), NotificationDataImpl.class);
		NotificationDataImpl notificationBuiltInClient = consumer.buildCallbackNotificationLogic(0, notification);
		assertTrue(notificationBuiltInClient.getResources().size() == 1);
	}
	
	@Test
	public final void testBuildCallbackNotificationLogicFlagIsTrue() {
		NotificationConsumer consumer = createNotificationConsumer();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		TestConfiguration testConfiguration = new TestConfiguration();
		testConfiguration.setFilterInEmptyResources(true);
		when(distributionClient.getConfiguration()).thenReturn(testConfiguration);
		NotificationDataImpl notification = gson.fromJson(getNotificationWithMultipleResources(), NotificationDataImpl.class);
		NotificationDataImpl notificationBuiltInClient = consumer.buildCallbackNotificationLogic(0, notification);
		assertTrue(notificationBuiltInClient.getResources().size() == 2);
	}

	private void simulateNotificationFromUEB(final String notificationFromUEB) throws InterruptedException {
		ScheduledExecutorService executorPool = Executors.newScheduledThreadPool(DistributionClientConstants.POOL_SIZE);
		executorPool.scheduleAtFixedRate(createNotificationConsumer(), 0, 100, TimeUnit.MILLISECONDS);

		Thread.sleep(200);

		List<String> nonHeatNotification = Arrays.asList(notificationFromUEB);
		notificationsQueue.add(nonHeatNotification);
		Thread.sleep(800);
		executorPool.shutdown();
	}

	private String getAsdcServiceNotificationWithHeatArtifact() {
		return "{\"distributionID\" : \"bcc7a72e-90b1-4c5f-9a37-28dc3cd86416\",\r\n" + "	\"serviceName\" : \"Testnotificationser1\",\r\n" + "	\"serviceVersion\" : \"1.0\",\r\n"
				+ "	\"serviceUUID\" : \"7f7f94f4-373a-4b71-a0e3-80ae2ba4eb5d\",\r\n" + "	\"serviceDescription\" : \"TestNotificationVF1\",\r\n" + "	\"resources\" : [{\r\n" + "			\"resourceInstanceName\" : \"testnotificationvf11\",\r\n"
				+ "			\"resourceName\" : \"TestNotificationVF1\",\r\n" + "			\"resourceVersion\" : \"1.0\",\r\n" + "			\"resoucreType\" : \"VF\",\r\n" + "			\"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2d\",\r\n"
				+ "			\"artifacts\" : [{\r\n" + "					\"artifactName\" : \"sample-xml-alldata-1-1.xml\",\r\n" + "					\"artifactType\" : \"YANG_XML\",\r\n"
				+ "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/sample-xml-alldata-1-1.xml\",\r\n"
				+ "					\"artifactChecksum\" : \"MTUxODFkMmRlOTNhNjYxMGYyYTI1ZjA5Y2QyNWQyYTk\\u003d\",\r\n" + "					\"artifactDescription\" : \"MyYang\",\r\n" + "					\"artifactTimeout\" : 0,\r\n"
				+ "					\"artifactUUID\" : \"0005bc4a-2c19-452e-be6d-d574a56be4d0\",\r\n" + "					\"artifactVersion\" : \"1\"\r\n" + "				}, {\r\n" + "					\"artifactName\" : \"heat.yaml\",\r\n"
				+ "					\"artifactType\" : \"HEAT\",\r\n" + "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n"
				+ "					\"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" + "					\"artifactDescription\" : \"heat\",\r\n" + "					\"artifactTimeout\" : 60,\r\n"
				+ "					\"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "					\"artifactVersion\" : \"1\"\r\n" + "				}, {\r\n" + "					\"artifactName\" : \"heat.env\",\r\n"
				+ "					\"artifactType\" : \"HEAT_ENV\",\r\n" + "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.env\",\r\n"
				+ "					\"artifactChecksum\" : \"NGIzMjExZTM1NDc2NjBjOTQyMGJmMWNiMmU0NTE5NzM\\u003d\",\r\n" + "					\"artifactDescription\" : \"Auto-generated HEAT Environment deployment artifact\",\r\n"
				+ "					\"artifactTimeout\" : 0,\r\n" + "					\"artifactUUID\" : \"ce65d31c-35c0-43a9-90c7-596fc51d0c86\",\r\n" + "					\"artifactVersion\" : \"1\",\r\n"
				+ "					\"generatedFromUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\"\r\n" + "				}\r\n" + "			]\r\n" + "		}\r\n" + "	]}";
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
	

	private String getAsdcNotificationWithNonExistentArtifact() {
		return "{\"distributionID\" : \"bcc7a72e-90b1-4c5f-9a37-28dc3cd86416\",\r\n" + "	\"serviceName\" : \"Testnotificationser1\",\r\n" + "	\"serviceVersion\" : \"1.0\",\r\n"
				+ "	\"serviceUUID\" : \"7f7f94f4-373a-4b71-a0e3-80ae2ba4eb5d\",\r\n" + "	\"serviceDescription\" : \"TestNotificationVF1\",\r\n" + "	\"bugabuga\" : \"xyz\",\r\n" + "	\"resources\" : [{\r\n"
				+ "			\"resourceInstanceName\" : \"testnotificationvf11\",\r\n" + "			\"resourceName\" : \"TestNotificationVF1\",\r\n" + "			\"resourceVersion\" : \"1.0\",\r\n" + "			\"resoucreType\" : \"VF\",\r\n"
				+ "			\"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2d\",\r\n" + "			\"artifacts\" : [{\r\n" + "					\"artifactName\" : \"heat.yaml\",\r\n" + "					\"artifactType\" : \"HEAT\",\r\n"
				+ "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n"
				+ "					\"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" + "					\"artifactDescription\" : \"heat\",\r\n" + "					\"artifactTimeout\" : 60,\r\n"
				+ "					\"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "					\"artifactBuga\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "					\"artifactVersion\" : \"1\"\r\n"
				+ "				}, {\r\n" + "					\"artifactName\" : \"buga.bug\",\r\n" + "					\"artifactType\" : \"BUGA_BUGA\",\r\n"
				+ "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.env\",\r\n"
				+ "					\"artifactChecksum\" : \"NGIzMjExZTM1NDc2NjBjOTQyMGJmMWNiMmU0NTE5NzM\\u003d\",\r\n" + "					\"artifactDescription\" : \"Auto-generated HEAT Environment deployment artifact\",\r\n"
				+ "					\"artifactTimeout\" : 0,\r\n" + "					\"artifactUUID\" : \"ce65d31c-35c0-43a9-90c7-596fc51d0c86\",\r\n" + "					\"artifactVersion\" : \"1\",\r\n"
				+ "					\"generatedFromUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\"\r\n" + "				}\r\n" + "			]\r\n" + "		}\r\n" + "	]}";
	}

	private String getAsdcServiceNotificationWithRelatedArtifacts() {
		return "{\"distributionID\" : \"bcc7a72e-90b1-4c5f-9a37-28dc3cd86416\",\r\n" + "	\"serviceName\" : \"Testnotificationser1\",\r\n" + "	\"serviceVersion\" : \"1.0\",\r\n"
				+ "	\"serviceUUID\" : \"7f7f94f4-373a-4b71-a0e3-80ae2ba4eb5d\",\r\n" + "	\"serviceDescription\" : \"TestNotificationVF1\",\r\n" + "	\"resources\" : [{\r\n" + "			\"resourceInstanceName\" : \"testnotificationvf11\",\r\n"
				+ "			\"resourceName\" : \"TestNotificationVF1\",\r\n" + "			\"resourceVersion\" : \"1.0\",\r\n" + "			\"resoucreType\" : \"VF\",\r\n" + "			\"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2d\",\r\n"
				+ "			\"artifacts\" : [{\r\n" + "					\"artifactName\" : \"sample-xml-alldata-1-1.xml\",\r\n" + "					\"artifactType\" : \"YANG_XML\",\r\n"
				+ "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/sample-xml-alldata-1-1.xml\",\r\n"
				+ "					\"artifactChecksum\" : \"MTUxODFkMmRlOTNhNjYxMGYyYTI1ZjA5Y2QyNWQyYTk\\u003d\",\r\n" + "					\"artifactDescription\" : \"MyYang\",\r\n" + "					\"artifactTimeout\" : 0,\r\n"
				+ "					\"artifactUUID\" : \"0005bc4a-2c19-452e-be6d-d574a56be4d0\",\r\n" + "					\"artifactVersion\" : \"1\",\r\n" + "                   \"relatedArtifacts\" : [\r\n"
				+ "  						\"ce65d31c-35c0-43a9-90c7-596fc51d0c86\"\r\n" + "  					]" + "				}, {\r\n" + "					\"artifactName\" : \"heat.yaml\",\r\n"
				+ "					\"artifactType\" : \"HEAT\",\r\n" + "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n"
				+ "					\"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" + "					\"artifactDescription\" : \"heat\",\r\n" + "					\"artifactTimeout\" : 60,\r\n"
				+ "					\"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "					\"artifactVersion\" : \"1\", \r\n" + "					\"relatedArtifacts\" : [\r\n"
				+ "  						\"0005bc4a-2c19-452e-be6d-d574a56be4d0\", \r\n" + "  						\"ce65d31c-35c0-43a9-90c7-596fc51d0c86\"\r\n" + "  					]" + "				}, {\r\n"
				+ "					\"artifactName\" : \"heat.env\",\r\n" + "					\"artifactType\" : \"HEAT_ENV\",\r\n"
				+ "					\"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.env\",\r\n"
				+ "					\"artifactChecksum\" : \"NGIzMjExZTM1NDc2NjBjOTQyMGJmMWNiMmU0NTE5NzM\\u003d\",\r\n" + "					\"artifactDescription\" : \"Auto-generated HEAT Environment deployment artifact\",\r\n"
				+ "					\"artifactTimeout\" : 0,\r\n" + "					\"artifactUUID\" : \"ce65d31c-35c0-43a9-90c7-596fc51d0c86\",\r\n" + "					\"artifactVersion\" : \"1\",\r\n"
				+ "					\"generatedFromUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\"\r\n" + "				}\r\n" + "			]\r\n" + "		}\r\n" + "	]}";
	}

	private String getAsdcServiceNotificationWithoutHeatArtifact() {
		return "{" + "   \"distributionID\" : \"5v1234d8-5b6d-42c4-7t54-47v95n58qb7\"," + "   \"serviceName\" : \"srv1\"," + "   \"serviceVersion\": \"2.0\"," + "   \"serviceUUID\" : \"4e0697d8-5b6d-42c4-8c74-46c33d46624c\","
				+ "   \"serviceArtifacts\":[" + "                    {" + "                       \"artifactName\" : \"ddd.yml\"," + "                       \"artifactType\" : \"DG_XML\"," + "                       \"artifactTimeout\" : \"65\","
				+ "                       \"artifactDescription\" : \"description\"," + "                       \"artifactURL\" :" + "                      \"/sdc/v1/catalog/services/srv1/2.0/resources/ddd/3.0/artifacts/ddd.xml\" ,"
				+ "                       \"resourceUUID\" : \"4e5874d8-5b6d-42c4-8c74-46c33d90drw\" ," + "                       \"checksum\" : \"15e389rnrp58hsw==\"" + "                    }" + "                  ]" + "}";
	}

	private String getNotificationWithServiceArtifatcs() {
		return "{\r\n" + "  \"distributionID\" : \"bcc7a72e-90b1-4c5f-9a37-28dc3cd86416\",\r\n" + "  \"serviceName\" : \"Testnotificationser1\",\r\n" + "  \"serviceVersion\" : \"1.0\",\r\n"
				+ "  \"serviceUUID\" : \"7f7f94f4-373a-4b71-a0e3-80ae2ba4eb5d\",\r\n" + "  \"serviceDescription\" : \"TestNotificationVF1\",\r\n" + "  \"serviceArtifacts\" : [{\r\n" + "          \"artifactName\" : \"sample-xml-alldata-1-1.xml\",\r\n"
				+ "          \"artifactType\" : \"YANG_XML\",\r\n" + "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/sample-xml-alldata-1-1.xml\",\r\n"
				+ "          \"artifactChecksum\" : \"MTUxODFkMmRlOTNhNjYxMGYyYTI1ZjA5Y2QyNWQyYTk\\u003d\",\r\n" + "          \"artifactDescription\" : \"MyYang\",\r\n" + "          \"artifactTimeout\" : 0,\r\n"
				+ "          \"artifactUUID\" : \"0005bc4a-2c19-452e-be6d-d574a56be4d0\",\r\n" + "          \"artifactVersion\" : \"1\"\r\n" + "        }, {\r\n" + "          \"artifactName\" : \"heat.yaml\",\r\n"
				+ "          \"artifactType\" : \"HEAT\",\r\n" + "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n"
				+ "          \"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" + "          \"artifactDescription\" : \"heat\",\r\n" + "          \"artifactTimeout\" : 60,\r\n"
				+ "          \"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "          \"artifactVersion\" : \"1\"\r\n" + "        }, {\r\n" + "          \"artifactName\" : \"heat.env\",\r\n"
				+ "          \"artifactType\" : \"HEAT_ENV\",\r\n" + "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.env\",\r\n"
				+ "          \"artifactChecksum\" : \"NGIzMjExZTM1NDc2NjBjOTQyMGJmMWNiMmU0NTE5NzM\\u003d\",\r\n" + "          \"artifactDescription\" : \"Auto-generated HEAT Environment deployment artifact\",\r\n"
				+ "          \"artifactTimeout\" : 0,\r\n" + "          \"artifactUUID\" : \"ce65d31c-35c0-43a9-90c7-596fc51d0c86\",\r\n" + "          \"artifactVersion\" : \"1\",\r\n"
				+ "          \"generatedFromUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\"\r\n" + "        }\r\n" + "      ],\r\n" + "  \"resources\" : [{\r\n" + "      \"resourceInstanceName\" : \"testnotificationvf11\",\r\n"
				+ "      \"resourceName\" : \"TestNotificationVF1\",\r\n" + "      \"resourceVersion\" : \"1.0\",\r\n" + "      \"resoucreType\" : \"VF\",\r\n" + "      \"resourceUUID\" : \"907e1746-9f69-40f5-9f2a-313654092a2d\",\r\n"
				+ "      \"artifacts\" : [{\r\n" + "          \"artifactName\" : \"sample-xml-alldata-1-1.xml\",\r\n" + "          \"artifactType\" : \"YANG_XML\",\r\n"
				+ "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/sample-xml-alldata-1-1.xml\",\r\n"
				+ "          \"artifactChecksum\" : \"MTUxODFkMmRlOTNhNjYxMGYyYTI1ZjA5Y2QyNWQyYTk\\u003d\",\r\n" + "          \"artifactDescription\" : \"MyYang\",\r\n" + "          \"artifactTimeout\" : 0,\r\n"
				+ "          \"artifactUUID\" : \"0005bc4a-2c19-452e-be6d-d574a56be4d0\",\r\n" + "          \"artifactVersion\" : \"1\"\r\n" + "        }, {\r\n" + "          \"artifactName\" : \"heat.yaml\",\r\n"
				+ "          \"artifactType\" : \"HEAT\",\r\n" + "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.yaml\",\r\n"
				+ "          \"artifactChecksum\" : \"ODEyNjE4YTMzYzRmMTk2ODVhNTU2NTg3YWEyNmIxMTM\\u003d\",\r\n" + "          \"artifactDescription\" : \"heat\",\r\n" + "          \"artifactTimeout\" : 60,\r\n"
				+ "          \"artifactUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\",\r\n" + "          \"artifactVersion\" : \"1\"\r\n" + "        }, {\r\n" + "          \"artifactName\" : \"heat.env\",\r\n"
				+ "          \"artifactType\" : \"HEAT_ENV\",\r\n" + "          \"artifactURL\" : \"/sdc/v1/catalog/services/Testnotificationser1/1.0/resourceInstances/testnotificationvf11/artifacts/heat.env\",\r\n"
				+ "          \"artifactChecksum\" : \"NGIzMjExZTM1NDc2NjBjOTQyMGJmMWNiMmU0NTE5NzM\\u003d\",\r\n" + "          \"artifactDescription\" : \"Auto-generated HEAT Environment deployment artifact\",\r\n"
				+ "          \"artifactTimeout\" : 0,\r\n" + "          \"artifactUUID\" : \"ce65d31c-35c0-43a9-90c7-596fc51d0c86\",\r\n" + "          \"artifactVersion\" : \"1\",\r\n"
				+ "          \"generatedFromUUID\" : \"8df6123c-f368-47d3-93be-1972cefbcc35\"\r\n" + "        }\r\n" + "      ]\r\n" + "    }\r\n" + "  ]\r\n" + "}";
	}

	private <T> int countInstances(List<T> list, T element) {
		int count = 0;
		for (T curr : list) {
			if (curr.equals(element)) {
				count++;
			}
		}
		return count;
	}
}
