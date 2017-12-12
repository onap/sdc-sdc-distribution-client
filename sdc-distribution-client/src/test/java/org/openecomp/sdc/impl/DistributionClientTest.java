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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.consumer.IConfiguration;
import org.openecomp.sdc.api.notification.IArtifactInfo;
import org.openecomp.sdc.api.notification.IVfModuleMetadata;
import org.openecomp.sdc.api.results.IDistributionClientResult;
import org.openecomp.sdc.http.AsdcConnectorClient;
import org.openecomp.sdc.http.TopicRegistrationResponse;
import org.openecomp.sdc.utils.ArtifactTypeEnum;
import org.openecomp.sdc.utils.ArtifactsUtils;
import org.openecomp.sdc.utils.DistributionActionResultEnum;
import org.openecomp.sdc.utils.TestConfiguration;
import org.openecomp.sdc.utils.TestNotificationCallback;
import org.openecomp.sdc.utils.Wrapper;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.cambria.client.CambriaClient.CambriaApiException;
import com.att.nsa.cambria.client.CambriaIdentityManager;

import fj.data.Either;

public class DistributionClientTest {

	static CambriaIdentityManager cc;
	DistributionClientImpl client = Mockito.spy(new DistributionClientImpl());
	IConfiguration testConfiguration = new TestConfiguration();
	AsdcConnectorClient connector = Mockito.mock(AsdcConnectorClient.class);


	@After
	public void afterTest() {
		client.stop();
	}

	@Test
	public void validateConfigurationTest() {
		DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(new Wrapper<IDistributionClientResult>(), testConfiguration);
		Assert.assertEquals(DistributionActionResultEnum.SUCCESS, validationResult);
		Assert.assertEquals(testConfiguration.getPollingInterval(), client.configuration.getPollingInterval());
		Assert.assertEquals(testConfiguration.getPollingTimeout(), client.configuration.getPollingTimeout());
	}

	@Test
	public void validateConfigurationToDefaultTest() {
		TestConfiguration userConfig = new TestConfiguration();
		userConfig.setPollingInterval(1);
		userConfig.setPollingTimeout(2);
		DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(new Wrapper<IDistributionClientResult>(), userConfig);
		Assert.assertEquals(DistributionActionResultEnum.SUCCESS, validationResult);
		Assert.assertEquals(15, client.configuration.getPollingInterval());
		Assert.assertEquals(15, client.configuration.getPollingTimeout());
	}

	@Test
	public void validateConfigurationFqdnTest() {

		String[] validFqdns = { "myHostname", "myHostname:80", "myHostname:8080", "1.1.1.1", "1.1.1.1:8080", "ueb01hydc.it.open.com", "ueb01hydc.it.open.com:8080", "ueb01hydc.it", "my-good.and-simple.fqdn" };

		String[] invalidFqdns = { "myHostname:808080", /* 70 letters */"abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij", "not**good", "very#not#good#" };

		boolean validationResult = true;

		for (int i = 0; i < validFqdns.length; i++) {
			validationResult = client.isValidFqdn(validFqdns[i]);
			assertEquals("assertion failed for FQDN " + validFqdns[i] + " expected to be valid, actual invalid", true, validationResult);
		}

		for (int i = 0; i < invalidFqdns.length; i++) {
			validationResult = client.isValidFqdn(invalidFqdns[i]);
			assertEquals("assertion failed for FQDN " + invalidFqdns[i] + " expected to be invalid, actual valid", false, validationResult);
		}

	}

	@Test
	public void validateConfigurationPasswordTest() {
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		TestConfiguration testPassword = new TestConfiguration();
		testPassword.setPassword(null);
		DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(errorWrapper, testPassword);
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult);

		testPassword.setPassword("");
		validationResult = client.validateAndInitConfiguration(errorWrapper, testPassword);
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult);

	}

	@Test
	public void validateConfigurationUserTest() {
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		TestConfiguration testUser = new TestConfiguration();
		testUser.setUser(null);
		DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(errorWrapper, testUser);
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult);

		testUser.setUser("");
		validationResult = client.validateAndInitConfiguration(errorWrapper, testUser);
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult);

	}

	@Test
	public void initWithMocksBadConfigurationTest() throws HttpException, CambriaApiException, IOException {


		TopicRegistrationResponse topics = new TopicRegistrationResponse();
		topics.setDistrNotificationTopicName("notificationTopic");
		topics.setDistrStatusTopicName("statusTopic");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.left(topics);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		// no password
		TestConfiguration testPassword = new TestConfiguration();
		testPassword.setPassword(null);
		IDistributionClientResult validationResult = client.init(testPassword, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult.getDistributionActionResult());

		testPassword.setPassword("");
		validationResult = client.init(testPassword, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult.getDistributionActionResult());

		// no username
		TestConfiguration testUser = new TestConfiguration();
		testUser.setUser(null);
		validationResult = client.init(testUser, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult.getDistributionActionResult());

		testUser.setUser("");
		validationResult = client.init(testUser, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult.getDistributionActionResult());

		// no ASDC server fqdn
		TestConfiguration testServerFqdn = new TestConfiguration();
		testServerFqdn.setAsdcAddress(null);
		validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_ASDC_FQDN, validationResult.getDistributionActionResult());

		testServerFqdn.setAsdcAddress("");
		validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_ASDC_FQDN, validationResult.getDistributionActionResult());

		testServerFqdn.setAsdcAddress("this##is##bad##fqdn");
		validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_INVALID_ASDC_FQDN, validationResult.getDistributionActionResult());

		// no consumerId
		TestConfiguration testConsumerId = new TestConfiguration();
		testConsumerId.setComsumerID(null);
		validationResult = client.init(testConsumerId, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID, validationResult.getDistributionActionResult());

		testConsumerId.setComsumerID("");
		validationResult = client.init(testConsumerId, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID, validationResult.getDistributionActionResult());

		// no environmentName
		TestConfiguration testEnv = new TestConfiguration();
		testEnv.setEnvironmentName(null);
		validationResult = client.init(testEnv, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME, validationResult.getDistributionActionResult());

		testEnv.setEnvironmentName("");
		validationResult = client.init(testEnv, new TestNotificationCallback());
		Assert.assertEquals(DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME, validationResult.getDistributionActionResult());

		Mockito.verify(client, Mockito.times(0)).getUEBServerList();
		Mockito.verify(cambriaMock, Mockito.times(0)).createApiKey(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(connector, Mockito.times(0)).registerAsdcTopics(Mockito.any(ApiCredential.class));
	}

	@Test
	public void initFailedConnectAsdcTest() throws HttpException, CambriaApiException, IOException {
		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		TestConfiguration badAsdcConfig = new TestConfiguration();
		badAsdcConfig.setAsdcAddress("badhost:8080");

		IDistributionClientResult init = client.init(badAsdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.ASDC_CONNECTION_FAILED, init.getDistributionActionResult());

		badAsdcConfig = new TestConfiguration();
		badAsdcConfig.setAsdcAddress("localhost:8181");

		init = client.init(badAsdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.ASDC_CONNECTION_FAILED, init.getDistributionActionResult());

	}

	@Test
	public void getConfigurationTest() throws HttpException, CambriaApiException, IOException {
		// connectorMock
		mockArtifactTypeList();
		TopicRegistrationResponse topics = new TopicRegistrationResponse();
		topics.setDistrNotificationTopicName("notificationTopic");
		topics.setDistrStatusTopicName("statusTopic");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.left(topics);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);
		IDistributionClientResult success = initSuccesResult();
		Mockito.when(connector.unregisterTopics(Mockito.any(ApiCredential.class))).thenReturn(success);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		TestConfiguration badAsdcConfig = new TestConfiguration();
		badAsdcConfig.setPollingInterval(-5);

		IDistributionClientResult init = client.init(badAsdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SUCCESS, init.getDistributionActionResult());

		String confString = client.getConfiguration().toString();
		System.out.println(confString);

	}

	private IDistributionClientResult initSuccesResult() {
		return new IDistributionClientResult() {

			@Override
			public String getDistributionMessageResult() {
				return "success";
			}

			@Override
			public DistributionActionResultEnum getDistributionActionResult() {
				return DistributionActionResultEnum.SUCCESS;
			}
		};
	}

	@Test
	public void initWithMocksTest() throws HttpException, CambriaApiException, IOException {

		mockArtifactTypeList();

		TopicRegistrationResponse topics = new TopicRegistrationResponse();
		topics.setDistrNotificationTopicName("notificationTopic");
		topics.setDistrStatusTopicName("statusTopic");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.left(topics);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);
		IDistributionClientResult success = initSuccesResult();
		Mockito.when(connector.unregisterTopics(Mockito.any(ApiCredential.class))).thenReturn(success);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SUCCESS, initResponse.getDistributionActionResult());
		Mockito.verify(client, Mockito.times(1)).getUEBServerList();
		Mockito.verify(cambriaMock, Mockito.times(1)).createApiKey(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(connector, Mockito.times(1)).registerAsdcTopics(Mockito.any(ApiCredential.class));
		System.out.println(initResponse);
	}

	private void mockArtifactTypeList() {
		List<String> artifactTypes = new ArrayList<>();
		for (ArtifactTypeEnum artifactType : ArtifactTypeEnum.values()) {
			artifactTypes.add(artifactType.name());
		}

		final Either<List<String>, IDistributionClientResult> eitherArtifactTypes = Either.left(artifactTypes);
		Mockito.when(connector.getValidArtifactTypesList()).thenReturn(eitherArtifactTypes);
	}

	@Test
	public void testAlreadyInitTest() throws HttpException, CambriaApiException, IOException {
		initWithMocksTest();
		IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_INITIALIZED, initResponse.getDistributionActionResult());
	}

	@Test
	public void initGetServerFailedTest() throws HttpException, CambriaApiException, IOException {

		// connectorMock
		IDistributionClientResult getServersResult = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_SERVER_PROBLEM, "problem");
		Either<List<String>, IDistributionClientResult> serversResult = Either.right(getServersResult);
		doReturn(serversResult).when(client).getUEBServerList();

		TopicRegistrationResponse topics = new TopicRegistrationResponse();
		topics.setDistrNotificationTopicName("notificationTopic");
		topics.setDistrStatusTopicName("statusTopic");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.left(topics);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.ASDC_SERVER_PROBLEM, initResponse.getDistributionActionResult());

		Mockito.verify(client, Mockito.times(1)).getUEBServerList();
		Mockito.verify(cambriaMock, Mockito.times(0)).createApiKey(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(connector, Mockito.times(0)).registerAsdcTopics(Mockito.any(ApiCredential.class));

		System.out.println(initResponse);
	}

	@Test
	public void initCreateKeysFailedTest() throws HttpException, CambriaApiException, IOException {

		// connectorMock
		mockArtifactTypeList();

		TopicRegistrationResponse topics = new TopicRegistrationResponse();
		topics.setDistrNotificationTopicName("notificationTopic");
		topics.setDistrStatusTopicName("statusTopic");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.left(topics);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new CambriaApiException("failure"));
		client.cambriaIdentityManager = cambriaMock;

		IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.UEB_KEYS_CREATION_FAILED, initResponse.getDistributionActionResult());

		Mockito.verify(client, Mockito.times(1)).getUEBServerList();
		Mockito.verify(cambriaMock, Mockito.times(1)).createApiKey(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(connector, Mockito.times(0)).registerAsdcTopics(Mockito.any(ApiCredential.class));
		System.out.println(initResponse);
	}

	@Test
	public void initRegistrationFailedTest() throws HttpException, CambriaApiException, IOException {

		// connectorMock
		mockArtifactTypeList();
		DistributionClientResultImpl failureResult = new DistributionClientResultImpl(DistributionActionResultEnum.BAD_REQUEST, "Bad Request");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsResult = Either.right(failureResult);
		Mockito.when(connector.registerAsdcTopics(Mockito.any(ApiCredential.class))).thenReturn(topicsResult);

		client.asdcConnector = connector;

		// cambriaMock

		CambriaIdentityManager cambriaMock = Mockito.mock(CambriaIdentityManager.class);
		Mockito.when(cambriaMock.createApiKey(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new ApiCredential("public", "secret"));
		client.cambriaIdentityManager = cambriaMock;

		IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.BAD_REQUEST, initResponse.getDistributionActionResult());
		Mockito.verify(client, Mockito.times(1)).getUEBServerList();
		Mockito.verify(cambriaMock, Mockito.times(1)).createApiKey(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(connector, Mockito.times(1)).registerAsdcTopics(Mockito.any(ApiCredential.class));
		System.out.println(initResponse);
	}

	@Test
	public void testStartWithoutInit() {
		IDistributionClientResult result = client.start();
		assertTrue(result.getDistributionActionResult() == DistributionActionResultEnum.DISTRIBUTION_CLIENT_NOT_INITIALIZED);
	}

	private IArtifactInfo initArtifactInfo() {
		ArtifactInfoImpl artifactInfo = new ArtifactInfoImpl();
		artifactInfo.setArtifactURL("/sdc/v1/services/serviceName/0.1/artifacts/aaa.hh");
		artifactInfo.setArtifactChecksum(ArtifactsUtils.getValidChecksum());
		return artifactInfo;
	}

	// ########### TESTS TO ADD TO CI START ###########
	/*public void createKeysTestCI() throws MalformedURLException, GeneralSecurityException {
		validateConfigurationTest();
		CambriaIdentityManager trueCambria = new CambriaClientBuilders.IdentityManagerBuilder().usingHttps().usingHosts(serverList).build();
		client.cambriaIdentityManager = trueCambria;
		DistributionClientResultImpl keysResult = client.createUebKeys();
		Assert.assertEquals(DistributionActionResultEnum.SUCCESS, keysResult.getDistributionActionResult());
		Assert.assertFalse(client.credential.getApiKey().isEmpty());
		Assert.assertFalse(client.credential.getApiSecret().isEmpty());

		System.out.println(keysResult);
		System.out.println("keys: public=" + client.credential.getApiKey() + " | secret=" + client.credential.getApiSecret());
	}
*/
	public void initTestCI() {
		IDistributionClient distributionClient = DistributionClientFactory.createDistributionClient();
		IDistributionClientResult init = distributionClient.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SUCCESS, init.getDistributionActionResult());

	}
	
	@Test
	public void testDecodeVfModuleArtifact() throws IOException{
		String vfModuleContent = getVFModuleExample();
		List<IVfModuleMetadata> decodeVfModuleArtifact = client.decodeVfModuleArtifact(vfModuleContent.getBytes());
		assertTrue(decodeVfModuleArtifact.size() == 1);
		IVfModuleMetadata iVfModuleMetadata = decodeVfModuleArtifact.get(0);
		assertTrue(iVfModuleMetadata.getArtifacts().size() == 11);
		assertEquals(iVfModuleMetadata.getVfModuleModelName(), "Vccfdb..base_vDB_11032016..module-0");
	}
	
    private String getVFModuleExample() {
		return "[\r\n" + 
				"  {\r\n" + 
				"    \"vfModuleModelName\": \"Vccfdb..base_vDB_11032016..module-0\",\r\n" + 
				"    \"vfModuleModelInvariantUUID\": \"89bcc10e-84f9-475a-b7e3-bdac6cd2b31a\",\r\n" + 
				"    \"vfModuleModelVersion\": \"1\",\r\n" + 
				"    \"vfModuleModelUUID\": \"f7e1c7aa-cc7b-4dfc-b761-237e8063bd96\",\r\n" + 
				"    \"GuguBubu\": true,\r\n" + 
				"    \"isBase\": true,\r\n" + 
				"    \"artifacts\": [\r\n" + 
				"      \"68733000-7656-487c-aecb-040af96df5a5\",\r\n" + 
				"      \"d3519bb4-be98-4c04-8815-4557379fdff3\",\r\n" + 
				"      \"b445d84b-de23-4f0c-a0aa-8d794d85bebe\",\r\n" + 
				"      \"52a6656a-63f4-4ae8-80f4-40febcaa15d6\",\r\n" + 
				"      \"fdcf20b5-1bac-4da7-9e77-b0b565115027\",\r\n" + 
				"      \"d3fcfd98-941c-4627-8b94-386dd3eab1ab\",\r\n" + 
				"      \"bdd6c2b6-793b-49d7-8590-51e7d6998f69\",\r\n" + 
				"      \"554a62b0-3a56-4c29-bc5e-23badf6da67f\",\r\n" + 
				"      \"4b922d87-f2c9-44da-b933-57a91294fb42\",\r\n" + 
				"      \"ad5cceda-0fa4-415e-b319-96f080e4b5c7\",\r\n" + 
				"      \"8f4312f4-7be5-4d64-a3f5-564be7a0f01e\"\r\n" + 
				"    ]\r\n" + 
				"  }\r\n" + 
				"]";
	}


	// @Test
	public void registerProducerCI() {

		/*try {
			CambriaTopicManager topicManager = new CambriaClientBuilders.TopicManagerBuilder().usingHttps().usingHosts(serverList).authenticatedBy("sSJc5qiBnKy2qrlc", "4ZRPzNJfEUK0sSNBvccd2m7X").build();
			topicManager.allowProducer("ASDC-DISTR-STATUS-TOPIC-TESTER", "1FSVAA3bRjhSKNAI");
		} catch (HttpException | IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		// publish
		// StringBuilder sb = new StringBuilder();
		// for (String s : serverList)
		// {
		// sb.append(s);
		// sb.append(",");
		// }
		// CambriaBatchingPublisher pub = CambriaClientFactory.createSimplePublisher(sb.toString(), "ASDC-DISTR-STATUS-TOPIC-TESTER");
		// pub.setApiCredentials("yPMwjhmOgHUyJEeW", "3RYpgvBsjpA8Y2CHdA1PM8xK" );
		//
		//
		// try {
		// pub.send("MyPartitionKey", "{\"artifactURL\":\"artifactURL_Val\", \"consumerID\" : \"123\", \"distributionID\" : \"AAA\", \"status\" : \"DOWNLOAD_OK\", \"timestamp\" : 1000}");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// finally{
		//
		//
		// try {
		// List<message> stuck = pub.close(15L, TimeUnit.SECONDS);
		// assertTrue(stuck.isEmpty());
		// } catch (IOException | InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

	}

	/*public void connectorGetServersTestCI() {
		AsdcConnectorClient connector = new AsdcConnectorClient();
		connector.init(testConfiguration);

		Either<List<String>, IDistributionClientResult> serverListFromAsdc = connector.getServerList();
		assertTrue(serverListFromAsdc.isLeft());
		assertEquals(serverList, serverListFromAsdc.left().value());
	}*/

	public void connectorRegisterCI() {
		AsdcConnectorClient connector = new AsdcConnectorClient();
		connector.init(testConfiguration);

		ApiCredential creds = new ApiCredential("publicKey", "secretKey");
		Either<TopicRegistrationResponse, DistributionClientResultImpl> topicsFromAsdc = connector.registerAsdcTopics(creds);
		assertTrue(topicsFromAsdc.isLeft());

	}

	public void downloadArtifactTestCI() {
		AsdcConnectorClient connector = new AsdcConnectorClient();
		connector.init(testConfiguration);
		IArtifactInfo artifactInfo = initArtifactInfo();
		connector.dowloadArtifact(artifactInfo);

	}
	// ########### TESTS TO ADD TO CI END ###########

}
