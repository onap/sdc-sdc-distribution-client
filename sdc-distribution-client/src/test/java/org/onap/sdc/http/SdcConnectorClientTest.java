/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.sdc.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fj.data.Either;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.Pair;

public class SdcConnectorClientTest {

    private static final Gson gson = new GsonBuilder().create();
    private static final HttpSdcClient httpClient = mock(HttpSdcClient.class);
    private static final IConfiguration configuration = mock(IConfiguration.class);
    private static final HttpSdcResponse httpSdcResponse = mock(HttpSdcResponse.class);
    private static final Map<String, String> mockHeaders = new HashMap<>();
    private static SdcConnectorClient sdcClient;

    private static final String ARTIFACT_URL = "http://127.0.0.1/artifact/url";
    private static final String IT_JUST_DIDN_T_WORK = "It just didn't work";
    private static final List<String> ARTIFACT_TYPES = Arrays.asList("Service", "Resource", "VF", "VFC");
    private static final List<String> KAFKA_DATA = Arrays.asList("onap-strimzi-kafka-bootstrap:9092", "SDC-DISTR-NOTIF-TOPIC-AUTO","SDC-DISTR-STATUS-TOPIC-AUTO");
    private static final String VALID_JSON_PAYLOAD = gson.toJson(ARTIFACT_TYPES);
    private static final String VALID_KAFKA_JSON_PAYLOAD = gson.toJson(KAFKA_DATA);
    private static final int PORT = 49512;
    private static final byte[] BYTES = new byte[] {0xA, 0xB, 0xC, 0xD};


    @BeforeClass
    public static void beforeClass() {
        sdcClient = Mockito.spy(new SdcConnectorClient(configuration, httpClient));
        when(httpSdcResponse.getStatus()).thenReturn(HttpStatus.SC_OK);

        doReturn(mockHeaders).when(sdcClient).addHeadersToHttpRequest(Mockito.anyString());
    }

    @Test
    public void initAndCloseTest() {
        IConfiguration conf = Mockito.mock(IConfiguration.class);
        when(conf.getUser()).thenReturn("user");
        when(conf.getPassword()).thenReturn("password");
        when(conf.isUseHttpsWithSDC()).thenReturn(true);

        when(conf.activateServerTLSAuth()).thenReturn(false);
        final HttpSdcClient httpClient = new HttpSdcClient(conf);
        SdcConnectorClient client = new SdcConnectorClient(conf, httpClient);
        client.close();
    }

    @Test
    public void getValidKafkaDataHappyScenarioTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair =
            new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(VALID_KAFKA_JSON_PAYLOAD.getBytes()));
        when(httpClient.getRequest(eq(SdcUrls.GET_KAFKA_DIST_DATA), Matchers.any(), eq(false)))
            .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = sdcClient.getKafkaDistData();
        assertTrue(result.isLeft());
        List<String> list = result.left().value();
        assertEquals(KAFKA_DATA, list);
    }

    @Test
    public void getValidKafkaDataErrorResponseScenarioTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_GATEWAY_TIMEOUT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(IT_JUST_DIDN_T_WORK.getBytes()));
        when(httpClient.getRequest(eq(SdcUrls.GET_KAFKA_DIST_DATA), Matchers.any(), eq(false)))
            .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = sdcClient.getKafkaDistData();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.SDC_SERVER_TIMEOUT,
            distributionClientResult.getDistributionActionResult());
    }

    @Test
    public void getValidArtifactTypesListHappyScenarioTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(VALID_JSON_PAYLOAD.getBytes()));
        when(httpClient.getRequest(eq(SdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = sdcClient.getValidArtifactTypesList();
        assertTrue(result.isLeft());
        List<String> list = result.left().value();
        assertEquals(ARTIFACT_TYPES, list);
    }

    @Test
    public void getValidArtifactTypesListErrorResponseScenarioTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_GATEWAY_TIMEOUT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(IT_JUST_DIDN_T_WORK.getBytes()));
        when(httpClient.getRequest(eq(SdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = sdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.SDC_SERVER_TIMEOUT,
                distributionClientResult.getDistributionActionResult());
    }


    @Test
    public void getValidArtifactTypesListExceptionDuringConnectionClosingTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_GATEWAY_TIMEOUT);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(VALID_JSON_PAYLOAD.getBytes()));
        when(httpClient.getRequest(eq(SdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        doThrow(new IOException("Test exception")).when(closeableHttpResponseMock).close();

        Either<List<String>, IDistributionClientResult> result = sdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.SDC_SERVER_TIMEOUT,
                distributionClientResult.getDistributionActionResult());
    }

    @Test
    public void getValidArtifactTypesListParsingExceptionHandlingTest() throws IOException {
        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        CloseableHttpResponse closeableHttpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair =
                new Pair<>(responseMock, closeableHttpResponseMock);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        when(httpClient.getRequest(eq(SdcUrls.GET_VALID_ARTIFACT_TYPES), Matchers.any(), eq(false)))
                .thenReturn(responsePair);

        Either<List<String>, IDistributionClientResult> result = sdcClient.getValidArtifactTypesList();
        assertTrue(result.isRight());
        IDistributionClientResult distributionClientResult = result.right().value();
        assertEquals(DistributionActionResultEnum.GENERAL_ERROR,
                distributionClientResult.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactHappyScenarioTest() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(SdcConnectorClient.CONTENT_DISPOSITION_HEADER, "SomeHeader");

        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);
        when(artifactInfo.getArtifactChecksum()).thenReturn(Hashing.md5().hashBytes(BYTES).toString());

        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(responseMock.getHeadersMap()).thenReturn(headers);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(BYTES));
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = sdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.SUCCESS, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactDataIntegrityProblemTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ByteArrayInputStream(BYTES));
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = sdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.DATA_INTEGRITY_PROBLEM, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactExceptionDuringDownloadHandlingTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = sdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.GENERAL_ERROR, result.getDistributionActionResult());
    }

    @Test
    public void downloadArtifactHandleDownloadErrorTest() throws IOException {
        IArtifactInfo artifactInfo = mock(IArtifactInfo.class);
        when(artifactInfo.getArtifactURL()).thenReturn(ARTIFACT_URL);

        HttpSdcResponse responseMock = mock(HttpSdcResponse.class);
        HttpEntity messageMock = mock(HttpEntity.class);
        Pair<HttpSdcResponse, CloseableHttpResponse> responsePair = new Pair<>(responseMock, null);

        when(responseMock.getStatus()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(responseMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getContent()).thenReturn(new ThrowingInputStreamForTesting());
        doReturn(responsePair).when(httpClient).getRequest(eq(ARTIFACT_URL), any(), eq(false));

        IDistributionClientResult result = sdcClient.downloadArtifact(artifactInfo);
        assertEquals(DistributionActionResultEnum.SDC_SERVER_PROBLEM, result.getDistributionActionResult());
    }

    static class ThrowingInputStreamForTesting extends InputStream {

        @Override
        public int read() throws IOException {
            throw new IOException("Not implemented. This is expected as the implementation is for unit tests only.");
        }
    }
}
