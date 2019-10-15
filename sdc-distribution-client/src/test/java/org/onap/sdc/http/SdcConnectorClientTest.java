/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.sdc.http;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.api.asdc.RegistrationRequest;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.utils.Pair;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fj.data.Either;

public class SdcConnectorClientTest {
	private Gson gson = new GsonBuilder().create();
	private static final String MOCK_ENV = "MockEnv";
	private static final String MOCK_API_KEY = "MockApikey";
	private static HttpAsdcClient httpClient = Mockito.mock(HttpAsdcClient.class);
	private static IConfiguration configuration = Mockito.mock(IConfiguration.class);
	private static ApiCredential apiCredential = Mockito.mock(ApiCredential.class);
	private static HttpAsdcResponse httpAsdcResponse = Mockito.mock(HttpAsdcResponse.class);
	@SuppressWarnings("unchecked")
	private static Either<TopicRegistrationResponse, DistributionClientResultImpl> mockResponse = Mockito
			.mock(Either.class);
	private static Map<String, String> mockHeaders = new HashMap<>();
	Pair<HttpAsdcResponse, CloseableHttpResponse> mockPair = new Pair<>(httpAsdcResponse, null);
	private HttpEntity lastHttpEntity = null;

	private static SdcConnectorClient asdcClient = Mockito.spy(new SdcConnectorClient());

	@BeforeClass
	public static void beforeClass() {
		asdcClient.setConfiguration(configuration);
		asdcClient.setHttpClient(httpClient);
		when(apiCredential.getApiKey()).thenReturn(MOCK_API_KEY);
		when(httpAsdcResponse.getStatus()).thenReturn(HttpStatus.SC_OK);

		doReturn(mockHeaders).when(asdcClient).addHeadersToHttpRequest(Mockito.anyString());
		doReturn(mockResponse).when(asdcClient).parseRegistrationResponse(httpAsdcResponse);
	}

	@Before
	public void beforeMethod() {
		Mockito.reset(configuration, httpClient);
		lastHttpEntity = null;
		when(configuration.getEnvironmentName()).thenReturn(MOCK_ENV);


		doAnswer(new Answer<Pair<HttpAsdcResponse, CloseableHttpResponse>>() {
			@Override
			public Pair<HttpAsdcResponse, CloseableHttpResponse> answer(InvocationOnMock invocation) throws Throwable {
				lastHttpEntity = invocation.getArgumentAt(1, HttpEntity.class);
				return mockPair;
			}
		}).when(httpClient).postRequest(Mockito.eq(AsdcUrls.POST_FOR_TOPIC_REGISTRATION), Mockito.any(HttpEntity.class),
				Mockito.eq(mockHeaders), Mockito.eq(false));
	}

	@Test
	public void testConsumeProduceStatusTopicFalse() throws UnsupportedOperationException, IOException {

		testConsumeProduceStatusTopic(false);

	}

	@Test
	public void testConsumeProduceStatusTopicTrue() throws UnsupportedOperationException, IOException {
		
		testConsumeProduceStatusTopic(true);
		
	}
	
	private void testConsumeProduceStatusTopic(final boolean isConsumeProduceStatusFlag) throws IOException {
		when(configuration.isConsumeProduceStatusTopic()).thenReturn(isConsumeProduceStatusFlag);
		asdcClient.registerAsdcTopics(apiCredential);
		verify(httpClient, times(1)).postRequest(Mockito.eq(AsdcUrls.POST_FOR_TOPIC_REGISTRATION),
				Mockito.any(HttpEntity.class), Mockito.eq(mockHeaders), Mockito.eq(false));
		assertNotNull(lastHttpEntity);
		RegistrationRequest actualRegRequest = gson.fromJson(IOUtils.toString(lastHttpEntity.getContent(), StandardCharsets.UTF_8), RegistrationRequest.class);
		RegistrationRequest expectedRegRequest = gson.fromJson(excpectedStringBody(isConsumeProduceStatusFlag), RegistrationRequest.class);

		assertTrue(actualRegRequest.getApiPublicKey().equals(expectedRegRequest.getApiPublicKey()));
		assertTrue(actualRegRequest.getDistrEnvName().equals(expectedRegRequest.getDistrEnvName()));
		assertTrue(actualRegRequest.getIsConsumerToSdcDistrStatusTopic()
				.equals(expectedRegRequest.getIsConsumerToSdcDistrStatusTopic()));
	}
	
	

	private String excpectedStringBody(boolean isConsumeProduceStatusTopic) {
		String stringBodyTemplate = "{\r\n" + "  \"apiPublicKey\": \"MockApikey\",\r\n"
				+ "  \"distrEnvName\": \"MockEnv\",\r\n" + "  \"isConsumerToSdcDistrStatusTopic\": %s\r\n" + "}";
		return String.format(stringBodyTemplate, isConsumeProduceStatusTopic);

	}
}
