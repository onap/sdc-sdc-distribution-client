/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2019 Nokia. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.hash.Hashing;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.api.asdc.RegistrationRequest;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.Pair;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fj.data.Either;

public class SdcConnectorClientTest {

    private static Gson gson = new GsonBuilder().create();
    private static final String MOCK_ENV = "MockEnv";
    private static final String MOCK_API_KEY = "MockApikey";
    private static HttpAsdcClient httpClient = mock(HttpAsdcClient.class);
    private static IConfiguration configuration = mock(IConfiguration.class);
    private static ApiCredential apiCredential = mock(ApiCredential.class);
    private static HttpAsdcResponse httpAsdcResponse = mock(HttpAsdcResponse.class);
    @SuppressWarnings("unchecked")
    private static Either<TopicRegistrationResponse, DistributionClientResultImpl> mockResponse =
            Mockito.mock(Either.class);
    private static Map<String, String> mockHeaders = new HashMap<>();
    Pair<HttpAsdcResponse, CloseableHttpResponse> mockPair = new Pair<>(httpAsdcResponse, null);
    private HttpEntity lastHttpEntity = null;

    private static SdcConnectorClient asdcClient;

    private static final String ARTIFACT_URL = "http://127.0.0.1/artifact/url";
    private static final String IT_JUST_DIDN_T_WORK = "It just didn't work";
    private static final List<String> ARTIFACT_TYPES = Arrays.asList("Service", "Resource", "VF", "VFC");
    private static final String VALID_JSON_PAYLOAD = gson.toJson(ARTIFACT_TYPES);
    private static final int PORT = 49512;
    private static final byte[] BYTES = new byte[] {0xA, 0xB, 0xC, 0xD};


    @BeforeClass
    public static void beforeClass() {
        asdcClient = Mockito.spy(new SdcConnectorClient(configuration, httpClient));
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

    @Test(expected = IllegalStateException.class)
    public void initAndCloseTest() {
        IConfiguration conf = Mockito.mock(IConfiguration.class);
        when(conf.getUser()).thenReturn("user");
        when(conf.getPassword()).thenReturn("password");
        when(conf.isUseHttpsWithSDC()).thenReturn(true);

        when(conf.activateServerTLSAuth()).thenReturn(false);
        final HttpAsdcClient httpClient = new HttpAsdcClient(conf);
        SdcConnectorClient client = new SdcConnectorClient(conf, httpClient);
        client.close();

        //check if client is really closed
        httpClient.getRequest(AsdcUrls.POST_FOR_TOPIC_REGISTRATION, new HashMap<>());
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
        verify(httpClient, times(1))
                .postRequest(Mockito.eq(AsdcUrls.POST_FOR_TOPIC_REGISTRATION), any(HttpEntity.class),
                        Mockito.eq(mockHeaders), Mockito.eq(false));
        assertNotNull(lastHttpEntity);
        RegistrationRequest actualRegRequest =
                gson.fromJson(IOUtils.toString(lastHttpEntity.getContent(), StandardCharsets.UTF_8),
                        RegistrationRequest.class);
        RegistrationRequest expectedRegRequest =
                gson.fromJson(excpectedStringBody(isConsumeProduceStatusFlag), RegistrationRequest.class);

        assertTrue(actualRegRequest.getApiPublicKey().equals(expectedRegRequest.getApiPublicKey()));
        assertTrue(actualRegRequest.getDistrEnvName().equals(expectedRegRequest.getDistrEnvName()));
        assertTrue(actualRegRequest.getIsConsumerToSdcDistrStatusTopic()
                           .equals(expectedRegRequest.getIsConsumerToSdcDistrStatusTopic()));
    }

    @Test
    public void getValidArtifactTypesListHappyScenarioTest() throws IOException {
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(VALID_JSON_PAYLOAD.getBytes()));
        when(httpClient.getRequest(eq(AsdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = asdcClient.getValidArtifactTypesList();
        assertTrue(result.isLeft());
        List<String> list = result.left().value();
        assertEquals(ARTIFACT_TYPES, list);
    }

    @Test
    public void getValidArtifactTypesListErrorResponseScenarioTest() throws IOException {
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_GATEWAY_TIMEOUT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(IT_JUST_DIDN_T_WORK.getBytes()));
        when(httpClient.getRequest(eq(AsdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = asdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.ASDC_SERVER_TIMEOUT,
                distributionClientResult.getDistributionActionResult());
    }


    @Test
    public void getValidArtifactTypesListExceptionDuringConnectionClosingTest() throws IOException {
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_GATEWAY_TIMEOUT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(VALID_JSON_PAYLOAD.getBytes()));
        when(httpClient.getRequest(eq(AsdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        doThrow(new IOException("Test exception")).when(closeableHttpResponseMock).close();

        Either<List<String>, IDistributionClientResult> result = asdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.ASDC_SERVER_TIMEOUT,
                distributionClientResult.getDistributionActionResult());
    }

    @Test
    public void getValidArtifactTypesListParsingExceptionHandlingTest() throws IOException {
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        when(httpClient.getRequest(eq(AsdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = asdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.GENERAL_ERROR,
                distributionClientResult.getDistributionActionResult());
    }

    @Test
    public void unregisterTopicsErrorDuringProcessingTest() throws IOException {
        when(configuration.getAsdcAddress()).thenReturn("127.0.0.1" + PORT);
        when(configuration.isConsumeProduceStatusTopic()).thenReturn(false);
        when(configuration.getMsgBusAddress())
                .thenReturn(Arrays.asList("http://127.0.0.1:45321/dmaap", "http://127.0.0.1:45321/dmaap"));

        String failMessage = "It just didn't work";
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(failMessage.getBytes()));
        doReturn(responsePair).when(httpClient)
                .postRequest(eq(AsdcUrls.POST_FOR_UNREGISTER), any(HttpEntity.class), any(), eq(false));

        IDistributionClientResult result = asdcClient.unregisterTopics(apiCredential);
        assertEquals(DistributionActionResultEnum.ASDC_CONNECTION_FAILED, result.getDistributionActionResult());
    }

    @Test
    public void unregisterTopicsHappyScenarioTest() throws IOException {
        when(configuration.getAsdcAddress()).thenReturn("127.0.0.1" + PORT);
        when(configuration.isConsumeProduceStatusTopic()).thenReturn(false);

        String failMessage = "";
        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_NO_CONTENT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(failMessage.getBytes()));
        doReturn(responsePair).when(httpClient)
                .postRequest(eq(AsdcUrls.POST_FOR_UNREGISTER), any(HttpEntity.class), any(), eq(false));

        IDistributionClientResult result = asdcClient.unregisterTopics(apiCredential);
        assertEquals(DistributionActionResultEnum.SUCCESS, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactHappyScenarioTest() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(asdcClient.CONTENT_DISPOSITION_HEADER, "SomeHeader");

        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);
        when(artifactInfo.getArtifactChecksum()).thenReturn(Hashing.md5().hashBytes(BYTES).toString());

        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(responseMock.getHeadersMap()).thenReturn(headers);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(BYTES));
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = asdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.SUCCESS, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactDataIntegrityProblemTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(BYTES));
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = asdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.DATA_INTEGRITY_PROBLEM, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactExceptionDuringDownloadHandlingTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = asdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.GENERAL_ERROR, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactHandleDownloadErrorTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpAsdcResponse responseMock = mock(HttpAsdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpAsdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = asdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.ASDC_SERVER_PROBLEM, result.getDistributionActionResult());
    }

    private String excpectedStringBody(boolean isConsumeProduceStatusTopic) {
        String stringBodyTemplate =
                "{\r\n" + "  \"apiPublicKey\": \"MockApikey\",\r\n" + "  \"distrEnvName\": \"MockEnv\",\r\n"
                        + "  \"isConsumerToSdcDistrStatusTopic\": %s\r\n" + "}";
        return String.format(stringBodyTemplate, isConsumeProduceStatusTopic);

    }

    static class ThrowingInputStreamForTesting extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException("Not implemented. This is expected as the implementation is for unit tests only.");
        }
    }
}
