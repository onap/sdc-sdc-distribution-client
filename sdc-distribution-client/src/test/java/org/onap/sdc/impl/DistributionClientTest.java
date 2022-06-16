/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
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

package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import fj.data.Either;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.IVfModuleMetadata;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.http.HttpSdcClient;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.utils.ArtifactTypeEnum;
import org.onap.sdc.utils.ArtifactsUtils;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.TestConfiguration;
import org.onap.sdc.utils.TestNotificationCallback;
import org.onap.sdc.utils.Wrapper;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;

class DistributionClientTest {
    DistributionClientImpl client = Mockito.spy(new DistributionClientImpl());
    IConfiguration testConfiguration = new TestConfiguration();
    SdcConnectorClient connector = Mockito.mock(SdcConnectorClient.class);
	SdcKafkaConsumer consumer = mock(SdcKafkaConsumer.class);


    @AfterEach
    public void afterTest() {
        client.stop();
    }

    @Test
    void validateConfigurationTest() {
		final Pair<DistributionActionResultEnum, Configuration> distributionActionResultEnumConfigurationPair = client.validateAndInitConfiguration(
            new Wrapper<>(), testConfiguration);
        DistributionActionResultEnum validationResult = distributionActionResultEnumConfigurationPair.getFirst();
        Configuration configuration = distributionActionResultEnumConfigurationPair.getSecond();
        assertEquals(DistributionActionResultEnum.SUCCESS, validationResult);
        assertEquals(testConfiguration.getPollingInterval(), configuration.getPollingInterval());
        assertEquals(testConfiguration.getPollingTimeout(), configuration.getPollingTimeout());
    }

    @Test
    void validateConfigurationToDefaultTest() {
        TestConfiguration userConfig = new TestConfiguration();
        userConfig.setPollingInterval(1);
        userConfig.setPollingTimeout(2);
		final Pair<DistributionActionResultEnum, Configuration> distributionActionResultEnumConfigurationPair = client.validateAndInitConfiguration(
            new Wrapper<>(), userConfig);
        DistributionActionResultEnum validationResult = distributionActionResultEnumConfigurationPair.getFirst();
        Configuration configuration = distributionActionResultEnumConfigurationPair.getSecond();
        assertEquals(DistributionActionResultEnum.SUCCESS, validationResult);
        assertEquals(15, configuration.getPollingInterval());
        assertEquals(15, configuration.getPollingTimeout());
    }

    @Test
    void validateConfigurationPasswordTest() {
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        TestConfiguration testPassword = new TestConfiguration();
        testPassword.setPassword(null);
        DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(errorWrapper, testPassword).getFirst();
        assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult);

        testPassword.setPassword("");
        validationResult = client.validateAndInitConfiguration(errorWrapper, testPassword).getFirst();
        assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult);

    }

    @Test
    void validateConfigurationUserTest() {
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        TestConfiguration testUser = new TestConfiguration();
        testUser.setUser(null);
        DistributionActionResultEnum validationResult = client.validateAndInitConfiguration(errorWrapper, testUser).getFirst();
        assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult);

        testUser.setUser("");
        validationResult = client.validateAndInitConfiguration(errorWrapper, testUser).getFirst();
        assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult);

    }

    @Test
	public void initWithMocksBadConfigurationTest() {
		reconfigureSdcConnector(connector, client);
        // no password
        TestConfiguration testPassword = new TestConfiguration();
        testPassword.setPassword(null);
        IDistributionClientResult validationResult = client.init(testPassword, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult.getDistributionActionResult());

        testPassword.setPassword("");
        validationResult = client.init(testPassword, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_PASSWORD, validationResult.getDistributionActionResult());

        // no username
        TestConfiguration testUser = new TestConfiguration();
        testUser.setUser(null);
        validationResult = client.init(testUser, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult.getDistributionActionResult());

        testUser.setUser("");
        validationResult = client.init(testUser, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_USERNAME, validationResult.getDistributionActionResult());

		// no SDC server fqdn
        TestConfiguration testServerFqdn = new TestConfiguration();
		testServerFqdn.setSdcAddress(null);
        validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.CONF_MISSING_SDC_FQDN, validationResult.getDistributionActionResult());

		testServerFqdn.setSdcAddress("");
        validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.CONF_MISSING_SDC_FQDN, validationResult.getDistributionActionResult());

		testServerFqdn.setSdcAddress("this##is##bad##fqdn");
        validationResult = client.init(testServerFqdn, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.CONF_INVALID_SDC_FQDN, validationResult.getDistributionActionResult());

        // no consumerId
        TestConfiguration testConsumerId = new TestConfiguration();
        testConsumerId.setComsumerID(null);
        validationResult = client.init(testConsumerId, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID, validationResult.getDistributionActionResult());

        testConsumerId.setComsumerID("");
        validationResult = client.init(testConsumerId, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID, validationResult.getDistributionActionResult());

        // no environmentName
        TestConfiguration testEnv = new TestConfiguration();
        testEnv.setEnvironmentName(null);
        validationResult = client.init(testEnv, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME, validationResult.getDistributionActionResult());

        testEnv.setEnvironmentName("");
        validationResult = client.init(testEnv, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME, validationResult.getDistributionActionResult());

    }

	private void reconfigureSdcConnector(SdcConnectorClient connector, DistributionClientImpl client) {
		doReturn(connector).when(client).createSdcConnector(any());
    }

    @Test
	public void initFailedConnectSdcTest() {

		TestConfiguration badSdcConfig = new TestConfiguration();
		if (badSdcConfig.isUseHttpsWithSDC() == null) {
            System.out.println("null for HTTPS then TRUE");
        } else {
			System.out.println("isUseHttpsWithSDC set to " + badSdcConfig.isUseHttpsWithSDC());
        }
		badSdcConfig.setSdcAddress("badhost:8080");

		IDistributionClientResult init = client.init(badSdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SDC_CONNECTION_FAILED, init.getDistributionActionResult());

		badSdcConfig = new TestConfiguration();
		badSdcConfig.setSdcAddress("localhost:8181");

		init = client.init(badSdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SDC_CONNECTION_FAILED, init.getDistributionActionResult());

    }

    @Test
	public void initFailedConnectSdcInHttpTest() {

		TestConfiguration badSdcConfig = new TestConfiguration();
		badSdcConfig.setSdcAddress("badhost:8080");
		badSdcConfig.setUseHttpsWithSDC(false);

		IDistributionClientResult init = client.init(badSdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SDC_CONNECTION_FAILED, init.getDistributionActionResult());

		badSdcConfig = new TestConfiguration();
		badSdcConfig.setSdcAddress("localhost:8181");
		badSdcConfig.setUseHttpsWithSDC(false);

		init = client.init(badSdcConfig, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.SDC_CONNECTION_FAILED, init.getDistributionActionResult());

    }

    @Test
	public void getConfigurationTest() {
        // connectorMock
        mockArtifactTypeList();
		mockKafkaData();
		reconfigureSdcConnector(connector, client);
		TestConfiguration badSdcConfig = new TestConfiguration();
		badSdcConfig.setPollingInterval(-5);

		IDistributionClientResult init = client.init(badSdcConfig, new TestNotificationCallback());
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
	public void initWithMocksTest() {
        mockArtifactTypeList();
		mockKafkaData();
		reconfigureSdcConnector(connector, client);
        IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
        assertEquals(DistributionActionResultEnum.SUCCESS, initResponse.getDistributionActionResult());
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

	private void mockKafkaData() {
		List<String> kafkaData = new ArrayList<>(
			Arrays.asList("onap-strimzi-kafka-bootstrap:9092", "SDC-DISTR-NOTIF-TOPIC-AUTO","SDC-DISTR-STATUS-TOPIC-AUTO"));
		final Either<List<String>, IDistributionClientResult> eitherArtifactTypes = Either.left(kafkaData);
		Mockito.when(connector.getKafkaDistData()).thenReturn(eitherArtifactTypes);
    }

    @Test
	public void testAlreadyInitTest() {
		initWithMocksTest();
        IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		assertEquals(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_INITIALIZED, initResponse.getDistributionActionResult());
    }

    @Test
	public void testStartWithoutInit() {
		IDistributionClientResult result = client.start();
		assertTrue(result.getDistributionActionResult() == DistributionActionResultEnum.DISTRIBUTION_CLIENT_NOT_INITIALIZED);
    }

	@org.junit.jupiter.api.Test
	@SetEnvironmentVariable(key = "JAASCONFIG", value = "org.apache.kafka.common.security.scram.ScramLoginModule required username=admin password=admin-secret;")
	public void testStartWithInitMessagingClientFailed() {
        mockArtifactTypeList();
		mockKafkaData();
		reconfigureSdcConnector(connector, client);
        IDistributionClientResult initResponse = client.init(testConfiguration, new TestNotificationCallback());
		Assertions.assertEquals(DistributionActionResultEnum.SUCCESS, initResponse.getDistributionActionResult());
        IDistributionClientResult result = client.start();
		Assertions.assertEquals(DistributionActionResultEnum.MESSAGING_CLIENT_INIT_FAILED, result.getDistributionActionResult());
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
		assertEquals(DistributionActionResultEnum.SUCCESS, keysResult.getDistributionActionResult());
		assertFalse(client.credential.getApiKey().isEmpty());
		assertFalse(client.credential.getApiSecret().isEmpty());

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
    void testDecodeVfModuleArtifact() throws IOException {
        String vfModuleContent = getVFModuleExample();
        List<IVfModuleMetadata> decodeVfModuleArtifact = client.decodeVfModuleArtifact(vfModuleContent.getBytes());
        assertEquals(1, decodeVfModuleArtifact.size());
        IVfModuleMetadata iVfModuleMetadata = decodeVfModuleArtifact.get(0);
        assertEquals(11, iVfModuleMetadata.getArtifacts().size());
        assertEquals("Vccfdb..base_vDB_11032016..module-0", iVfModuleMetadata.getVfModuleModelName());
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


    public void downloadArtifactTestCI() {
		SdcConnectorClient connector = new SdcConnectorClient(testConfiguration, new HttpSdcClient(testConfiguration));
        IArtifactInfo artifactInfo = initArtifactInfo();
        connector.downloadArtifact(artifactInfo);

    }
    // ########### TESTS TO ADD TO CI END ###########

}
