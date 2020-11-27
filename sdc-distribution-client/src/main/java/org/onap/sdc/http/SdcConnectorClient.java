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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.api.asdc.RegistrationRequest;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.impl.DistributionClientDownloadResultImpl;
import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fj.data.Either;

public class SdcConnectorClient {
    private final static Logger log = LoggerFactory.getLogger(SdcConnectorClient.class.getName());
    static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    private final IConfiguration configuration;
    private final HttpAsdcClient httpClient;

    public SdcConnectorClient(IConfiguration configuration, HttpAsdcClient httpClient) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(httpClient);
        this.configuration = configuration;
        this.httpClient = httpClient;
    }

    public void close() {
        httpClient.closeHttpClient();
    }

    public Either<List<String>, IDistributionClientResult> getValidArtifactTypesList() {
        Pair<HttpAsdcResponse, CloseableHttpResponse> getServersResponsePair = performAsdcServerRequest(AsdcUrls.GET_VALID_ARTIFACT_TYPES);
        HttpAsdcResponse getArtifactTypeResponse = getServersResponsePair.getFirst();

        Either<List<String>, IDistributionClientResult> response;
        if (getArtifactTypeResponse.getStatus() == HttpStatus.SC_OK) {
            response = parseGetValidArtifactTypesResponse(getArtifactTypeResponse);
        } else {
            IDistributionClientResult asdcError = handleAsdcError(getArtifactTypeResponse);
            response = Either.right(asdcError);

        }
        handeAsdcConnectionClose(getServersResponsePair);
        return response;

    }

    private void handeAsdcConnectionClose(Pair<HttpAsdcResponse, CloseableHttpResponse> getServersResponsePair) {
        if (getServersResponsePair.getSecond() != null) {
            try {
                getServersResponsePair.getSecond().close();

            } catch (IOException e) {
                log.error("failed to close http response");
            }

        }
    }

    private Pair<HttpAsdcResponse, CloseableHttpResponse> performAsdcServerRequest(final String url) {
        String requestId = generateRequestId();
        Map<String, String> requestHeaders = addHeadersToHttpRequest(requestId);
        log.debug("about to perform getServerList. requestId= {} url= {}", requestId, url);
        return httpClient.getRequest(url, requestHeaders, false);
    }

    public Either<TopicRegistrationResponse, DistributionClientResultImpl> registerAsdcTopics(ApiCredential credential) {

        Either<TopicRegistrationResponse, DistributionClientResultImpl> response;

        String requestId = generateRequestId();
        Map<String, String> requestHeaders = addHeadersToHttpRequest(requestId);

        RegistrationRequest registrationRequest = new RegistrationRequest(credential.getApiKey(), configuration.getEnvironmentName(), configuration.isConsumeProduceStatusTopic(), configuration.getMsgBusAddress());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(registrationRequest);
        StringEntity body = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);

        log.debug("about to perform registerAsdcTopics. requestId= " + requestId + " url= " + AsdcUrls.POST_FOR_TOPIC_REGISTRATION);
        Pair<HttpAsdcResponse, CloseableHttpResponse> registerResponsePair = httpClient.postRequest(AsdcUrls.POST_FOR_TOPIC_REGISTRATION, body, requestHeaders, false);
        HttpAsdcResponse registerResponse = registerResponsePair.getFirst();
        int status = registerResponse.getStatus();

        if (status == HttpStatus.SC_OK) {
            response = parseRegistrationResponse(registerResponse);

        } else {
            DistributionClientResultImpl asdcError = handleAsdcError(registerResponse);
            return Either.right(asdcError);
        }
        handeAsdcConnectionClose(registerResponsePair);

        log.debug("registerAsdcTopics response= " + status + ". requestId= " + requestId + " url= " + AsdcUrls.POST_FOR_TOPIC_REGISTRATION);
        return response;

    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    public IDistributionClientResult unregisterTopics(ApiCredential credential) {

        DistributionClientResultImpl response;

        String requestId = generateRequestId();
        Map<String, String> requestHeaders = addHeadersToHttpRequest(requestId);

        RegistrationRequest registrationRequest = new RegistrationRequest(credential.getApiKey(), configuration.getEnvironmentName(), configuration.isConsumeProduceStatusTopic(), configuration.getMsgBusAddress());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(registrationRequest);
        StringEntity body = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);

        log.debug("about to perform unregisterTopics. requestId= " + requestId + " url= " + AsdcUrls.POST_FOR_UNREGISTER);
        Pair<HttpAsdcResponse, CloseableHttpResponse> unRegisterResponsePair = httpClient.postRequest(AsdcUrls.POST_FOR_UNREGISTER, body, requestHeaders, false);
        HttpAsdcResponse unRegisterResponse = unRegisterResponsePair.getFirst();
        int status = unRegisterResponse.getStatus();
        if (status == HttpStatus.SC_NO_CONTENT || status == HttpStatus.SC_OK) {
            response = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "unregistration successful");

        } else {
            response = handleAsdcError(unRegisterResponse);
        }

        handeAsdcConnectionClose(unRegisterResponsePair);

        log.debug("unregisterTopics response = " + status + ". requestId= " + requestId + " url= " + AsdcUrls.POST_FOR_UNREGISTER);

        return response;

    }

    public DistributionClientDownloadResultImpl downloadArtifact(IArtifactInfo artifactInfo) {
        DistributionClientDownloadResultImpl response;

        String requestId = generateRequestId();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(DistributionClientConstants.HEADER_REQUEST_ID, requestId);
        requestHeaders.put(DistributionClientConstants.HEADER_INSTANCE_ID, configuration.getConsumerID());
        requestHeaders.put(HttpHeaders.ACCEPT, ContentType.APPLICATION_OCTET_STREAM.toString());
        String requestUrl = artifactInfo.getArtifactURL();
        Pair<HttpAsdcResponse, CloseableHttpResponse> downloadPair = httpClient.getRequest(requestUrl, requestHeaders, false);
        HttpAsdcResponse downloadResponse = downloadPair.getFirst();

        int status = downloadResponse.getStatus();
        if (status == HttpStatus.SC_OK) {

            response = parseDownloadArtifactResponse(artifactInfo, downloadResponse);
        } else {
            response = handleAsdcDownloadArtifactError(downloadResponse);

        }
        handeAsdcConnectionClose(downloadPair);
        return response;
    }

    /* **************************** private methods ********************************************/

    private Either<List<String>, IDistributionClientResult> parseGetValidArtifactTypesResponse(HttpAsdcResponse getArtifactTypesResponse) {
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

    private Either<List<String>, IDistributionClientResult> handleParsingError(Exception e) {
        Either<List<String>, IDistributionClientResult> result;
        log.error("failed to parse response from ASDC. error: " + e.getMessage());
        IDistributionClientResult response = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to parse response from ASDC");
        result = Either.right(response);
        return result;
    }

    Either<TopicRegistrationResponse, DistributionClientResultImpl> parseRegistrationResponse(HttpAsdcResponse registerResponse) {

        String jsonMessage;
        try {
            jsonMessage = IOUtils.toString(registerResponse.getMessage().getContent());

            Gson gson = new GsonBuilder().create();
            TopicRegistrationResponse registrationResponse = gson.fromJson(jsonMessage, TopicRegistrationResponse.class);

            if (registrationResponse.getDistrNotificationTopicName() == null) {
                DistributionClientResultImpl response = new DistributionClientResultImpl(DistributionActionResultEnum.FAIL, "failed to receive notification topic from ASDC");
                return Either.right(response);
            }

            if (registrationResponse.getDistrStatusTopicName() == null) {
                DistributionClientResultImpl response = new DistributionClientResultImpl(DistributionActionResultEnum.FAIL, "failed to receive status topic from ASDC");
                return Either.right(response);
            }
            return Either.left(registrationResponse);

        } catch (UnsupportedOperationException | IOException e) {
            log.error("failed to pars response from ASDC. error: " + e.getMessage());
            DistributionClientResultImpl response = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to parse response from ASDC");
            return Either.right(response);
        }
    }

    protected Map<String, String> addHeadersToHttpRequest(String requestId) {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(DistributionClientConstants.HEADER_REQUEST_ID, requestId);
        requestHeaders.put(DistributionClientConstants.HEADER_INSTANCE_ID, configuration.getConsumerID());
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        return requestHeaders;
    }

    private DistributionClientResultImpl handleAsdcError(HttpAsdcResponse registerResponse) {
        int status = registerResponse.getStatus();
        DistributionClientResultImpl errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to send request to ASDC");
        if (status == HttpStatus.SC_UNAUTHORIZED) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_AUTHENTICATION_FAILED, "authentication to ASDC failed for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_AUTHORIZATION_FAILED, "authorization failure for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_BAD_REQUEST) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.BAD_REQUEST, "ASDC call failed due to missing information");
        } else if (status == HttpStatus.SC_NOT_FOUND) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_NOT_FOUND, "ASDC not found");
        } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_SERVER_PROBLEM, "ASDC server problem");
        } else if (status == HttpStatus.SC_BAD_GATEWAY) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_CONNECTION_FAILED, "ASDC server problem");
        } else if (status == HttpStatus.SC_GATEWAY_TIMEOUT) {
            errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.ASDC_SERVER_TIMEOUT, "ASDC server problem");
        }
        log.error("status from ASDC is " + registerResponse);
        log.error(errorResponse.toString());
        try {
            String errorString = IOUtils.toString(registerResponse.getMessage().getContent());
            log.debug("error from ASDC is: " + errorString);
        } catch (UnsupportedOperationException | IOException e) {
        }
        return errorResponse;

    }

    private DistributionClientDownloadResultImpl handleAsdcDownloadArtifactError(HttpAsdcResponse registerResponse) {
        int status = registerResponse.getStatus();
        DistributionClientDownloadResultImpl errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "failed to send request to ASDC");
        if (status == HttpStatus.SC_UNAUTHORIZED) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.ASDC_AUTHENTICATION_FAILED, "authentication to ASDC failed for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.ASDC_AUTHORIZATION_FAILED, "authorization failure for user " + configuration.getUser());
        } else if (status == HttpStatus.SC_BAD_REQUEST || status == HttpStatus.SC_NOT_FOUND) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.ARTIFACT_NOT_FOUND, "Specified artifact is  not found");
            // } else if (status == 404){
            // errorResponse = new DistributionClientDownloadResultImpl(
            // DistributionActionResultEnum.ASDC_NOT_FOUND,
            // "ASDC not found");
        } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            errorResponse = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.ASDC_SERVER_PROBLEM, "ASDC server problem");
        }
        log.error("status from ASDC is " + registerResponse);
        log.error(errorResponse.toString());
        try {
            String errorString = IOUtils.toString(registerResponse.getMessage().getContent());
            log.debug("error from ASDC is: " + errorString);
        } catch (UnsupportedOperationException | IOException e) {
        }
        return errorResponse;

    }

    private DistributionClientDownloadResultImpl parseDownloadArtifactResponse(IArtifactInfo artifactInfo, HttpAsdcResponse getServersResponse) {
        HttpEntity entity = getServersResponse.getMessage();
        InputStream is;
        try {
            is = entity.getContent();
            String artifactName = "";
            if (getServersResponse.getHeadersMap().containsKey(CONTENT_DISPOSITION_HEADER)) {
                artifactName = getServersResponse.getHeadersMap().get(CONTENT_DISPOSITION_HEADER);
            }

            byte[] payload = IOUtils.toByteArray(is);
            if (artifactInfo.getArtifactChecksum() == null || artifactInfo.getArtifactChecksum().isEmpty()) {
                return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.DATA_INTEGRITY_PROBLEM, "failed to get artifact from ASDC. Empty checksum");
            }

            return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "success", artifactName, payload);
        } catch (UnsupportedOperationException | IOException e) {
            log.error("failed to get artifact from response ");
            return new DistributionClientDownloadResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "UnsupportedOperationException ");
        }

    }
}
