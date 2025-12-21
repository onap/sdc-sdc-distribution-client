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

package org.onap.sdc.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fj.data.Either;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.kafka.KafkaDataResponse;
import org.onap.sdc.utils.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdcConnectorClient {
    private static final Logger log = LoggerFactory.getLogger(SdcConnectorClient.class.getName());
    static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    private final IConfiguration configuration;
    private final HttpSdcClient httpClient;

    public SdcConnectorClient(IConfiguration configuration, HttpSdcClient httpClient) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(httpClient);
        this.configuration = configuration;
        this.httpClient = httpClient;
    }

    public void close() {
        httpClient.closeHttpClient();
    }

    public Either<List<String>, IDistributionClientResult> getValidArtifactTypesList() {
        Pair<HttpSdcResponse, CloseableHttpResponse> getServersResponsePair = performSdcServerRequest(SdcUrls.GET_VALID_ARTIFACT_TYPES);
        HttpSdcResponse getArtifactTypeResponse = getServersResponsePair.getFirst();

        Either<List<String>, IDistributionClientResult> response;
        if (getArtifactTypeResponse.getStatus() == HttpStatus.SC_OK) {
            response = parseGetValidArtifactTypesResponse(getArtifactTypeResponse);
        } else {
            IDistributionClientResult sdcError = handleSdcError(getArtifactTypeResponse);
            response = Either.right(sdcError);

        }
        handeSdcConnectionClose(getServersResponsePair);
        return response;

    }

    public Either<KafkaDataResponse, IDistributionClientResult> getKafkaDistData() {
        Pair<HttpSdcResponse, CloseableHttpResponse> getServersResponsePair = performSdcServerRequest(SdcUrls.GET_KAFKA_DIST_DATA);
        HttpSdcResponse getKafkaDistDataResponse = getServersResponsePair.getFirst();
        Either<KafkaDataResponse, IDistributionClientResult> response;
        if (getKafkaDistDataResponse.getStatus() == HttpStatus.SC_OK) {
            response = parseGetKafkaDistDataResponse(getKafkaDistDataResponse);
        } else {
            IDistributionClientResult sdcError = handleSdcError(getKafkaDistDataResponse);
            response = Either.right(sdcError);
        }
        handeSdcConnectionClose(getServersResponsePair);
        return response;
    }

    private void handeSdcConnectionClose(Pair<HttpSdcResponse, CloseableHttpResponse> getServersResponsePair) {
        if (getServersResponsePair.getSecond() != null) {
            try {
                getServersResponsePair.getSecond().close();

            } catch (IOException e) {
                log.error("failed to close http response");
            }

        }
    }

    private Pair<HttpSdcResponse, CloseableHttpResponse> performSdcServerRequest(String sdcUrl) {
        String requestId = generateRequestId();
        CaseInsensitiveMap<String, String> requestHeaders = addHeadersToHttpRequest(requestId);
        log.debug("about to perform get on SDC. requestId= {} url= {}", requestId, sdcUrl);
        return httpClient.getRequest(sdcUrl, requestHeaders, false);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    public DistributionClientDownloadResultImpl downloadArtifact(IArtifactInfo artifactInfo) {
        DistributionClientDownloadResultImpl response;

        String requestId = generateRequestId();
        CaseInsensitiveMap<String, String> requestHeaders = new CaseInsensitiveMap<>();
        requestHeaders.put(DistributionClientConstants.HEADER_REQUEST_ID, requestId);
        requestHeaders.put(DistributionClientConstants.HEADER_INSTANCE_ID, configuration.getConsumerID());
        requestHeaders.put(HttpHeaders.ACCEPT, ContentType.APPLICATION_OCTET_STREAM.toString());
        String requestUrl = artifactInfo.getArtifactURL();
        Pair<HttpSdcResponse, CloseableHttpResponse> downloadPair = httpClient.getRequest(requestUrl, requestHeaders, false);
        HttpSdcResponse downloadResponse = downloadPair.getFirst();

        int status = downloadResponse.getStatus();

        if (status == HttpStatus.SC_OK) {
            response = parseDownloadArtifactResponse(artifactInfo, downloadResponse);
        } else {
            response = handleSdcDownloadArtifactError(downloadResponse);
        }
        handeSdcConnectionClose(downloadPair);
        return response;
    }

    /* **************************** private methods ********************************************/

    private Either<List<String>, IDistributionClientResult> parseGetValidArtifactTypesResponse(HttpSdcResponse getArtifactTypesResponse) {
        Either<List<String>, IDistributionClientResult> result;
        try {
            String jsonMessage = IOUtils.toString(getArtifactTypesResponse.getMessage().getContent());
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            List<String> artifactTypesList = gson.fromJson(jsonMessage, listType);
            result = Either.left(artifactTypesList);

        } catch (UnsupportedOperationException | IOException e) {
            result = handleParsingError(e);
        }

        return result;
    }

    private Either<KafkaDataResponse, IDistributionClientResult> parseGetKafkaDistDataResponse(HttpSdcResponse getKafkaDistDataResponse) {
        Either<KafkaDataResponse, IDistributionClientResult> result;
        try {
            String jsonMessage = IOUtils.toString(getKafkaDistDataResponse.getMessage().getContent());
            Gson gson = new GsonBuilder().create();
            KafkaDataResponse kafkaData = gson.fromJson(jsonMessage, KafkaDataResponse.class);
            result = Either.left(kafkaData);
        } catch (UnsupportedOperationException | IOException e) {
            result = handleKafkaParsingError(e);
        }
        return result;
    }

    private Either<KafkaDataResponse, IDistributionClientResult> handleKafkaParsingError(Exception e) {
        Either<KafkaDataResponse, IDistributionClientResult> result;
        log.error("failed to parse kafka data response from SDC. error: ", e);
        IDistributionClientResult response = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to parse kafka data response from SDC");
        result = Either.right(response);
        return result;
    }

    private Either<List<String>, IDistributionClientResult> handleParsingError(Exception e) {
        Either<List<String>, IDistributionClientResult> result;
        log.error("failed to parse response from SDC. error: ", e);
        IDistributionClientResult response = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to parse response from SDC");
        result = Either.right(response);
        return result;
    }

    protected CaseInsensitiveMap<String, String> addHeadersToHttpRequest(String requestId) {
        CaseInsensitiveMap<String, String> requestHeaders = new CaseInsensitiveMap<>();
        requestHeaders.put(DistributionClientConstants.HEADER_REQUEST_ID, requestId);
        requestHeaders.put(DistributionClientConstants.HEADER_INSTANCE_ID, configuration.getConsumerID());
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        return requestHeaders;
    }

    private DistributionClientResultImpl handleSdcError(HttpSdcResponse registerResponse) {
        int status = registerResponse.getStatus();
        DistributionClientResultImpl errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to send request to SDC");
        if (status == HttpStatus.SC_UNAUTHORIZED) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_AUTHENTICATION_FAILED, "authentication to SDC failed for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_AUTHORIZATION_FAILED, "authorization failure for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_BAD_REQUEST) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.BAD_REQUEST, "SDC call failed due to missing information");
        } else if (status == HttpStatus.SC_NOT_FOUND) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_NOT_FOUND, "SDC not found");
        } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_SERVER_PROBLEM, "SDC server problem");
        } else if (status == HttpStatus.SC_BAD_GATEWAY) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_CONNECTION_FAILED, "SDC server problem");
        } else if (status == HttpStatus.SC_GATEWAY_TIMEOUT) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.SDC_SERVER_TIMEOUT, "SDC server problem");
        }
        log.error("status from SDC is {}", registerResponse);
        log.error(errorResponse.toString());
        try {
            String errorString = IOUtils.toString(registerResponse.getMessage().getContent());
            log.debug("error from SDC is: {}", errorString);
        } catch (UnsupportedOperationException | IOException e) {
            log.error("During error handling another exception occurred: ", e);
        }
        return errorResponse;

    }

    private DistributionClientDownloadResultImpl handleSdcDownloadArtifactError(HttpSdcResponse registerResponse) {
        int status = registerResponse.getStatus();
        DistributionClientDownloadResultImpl errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to send request to SDC");
        if (status == HttpStatus.SC_UNAUTHORIZED) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SDC_AUTHENTICATION_FAILED, "authentication to SDC failed for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SDC_AUTHORIZATION_FAILED, "authorization failure for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_BAD_REQUEST || status == HttpStatus.SC_NOT_FOUND) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.ARTIFACT_NOT_FOUND, "Specified artifact is  not found");
            // } else if (status == 404){
            // errorResponse = new DistributionClientDownloadResultImpl(
            // DistributionActionResultEnum.SDC_NOT_FOUND,
            // "SDC not found");
        } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SDC_SERVER_PROBLEM, "SDC server problem");
        }
        log.error("status from SDC is {}", registerResponse);
        log.error(errorResponse.toString());
        try {
            String errorString = IOUtils.toString(registerResponse.getMessage().getContent());
            log.debug("error from SDC is: {}", errorString);
        } catch (UnsupportedOperationException | IOException e) {
            log.error("During error handling another exception occurred: ", e);
        }
        return errorResponse;

    }

    private DistributionClientDownloadResultImpl parseDownloadArtifactResponse(IArtifactInfo artifactInfo, HttpSdcResponse getServersResponse) {
        HttpEntity entity = getServersResponse.getMessage();
        InputStream is;
        try {
            is = entity.getContent();
            String artifactName = "";
            if (getServersResponse.getHeadersMap().containsCaseInsensitiveKey(CONTENT_DISPOSITION_HEADER)) {
                artifactName = getServersResponse.getHeadersMap().getCaseInsensitiveKey(CONTENT_DISPOSITION_HEADER);
            }

            byte[] payload = IOUtils.toByteArray(is);
            if (artifactInfo.getArtifactChecksum() == null || artifactInfo.getArtifactChecksum().isEmpty()) {
                return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.DATA_INTEGRITY_PROBLEM, "failed to get artifact from SDC. Empty checksum");
            }

            return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "success", artifactName, payload);
        } catch (UnsupportedOperationException | IOException e) {
            log.error("failed to get artifact from response ");
            return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "UnsupportedOperationException ");
        }

    }
}
