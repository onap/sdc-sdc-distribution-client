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

import static java.util.Objects.isNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.IDistributionStatusMessageJsonBuilder;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.http.HttpAsdcClient;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.http.TopicRegistrationResponse;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.utils.GeneralUtils;
import org.onap.sdc.utils.NotificationSender;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.Wrapper;
import org.onap.sdc.api.notification.IVfModuleMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClient.CambriaApiException;
import com.att.nsa.cambria.client.CambriaClientBuilders.AbstractAuthenticatedManagerBuilder;
import com.att.nsa.cambria.client.CambriaClientBuilders.ConsumerBuilder;
import com.att.nsa.cambria.client.CambriaClientBuilders.IdentityManagerBuilder;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaIdentityManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fj.data.Either;

public class DistributionClientImpl implements IDistributionClient {

    public static final int POLLING_TIMEOUT_MULTIPLIER = 1000;
    public static final int TERMINATION_TIMEOUT = 60;
    private static Logger log = LoggerFactory.getLogger(DistributionClientImpl.class.getName());

    private SdcConnectorClient asdcConnector;
    private ScheduledExecutorService executorPool = null;
    protected CambriaIdentityManager cambriaIdentityManager = null;
    private List<String> brokerServers;
    protected ApiCredential credential;
    protected Configuration configuration;
    private INotificationCallback callback;
    private IStatusCallback statusCallback;
    private String notificationTopic;
    private String statusTopic;
    private boolean isConsumerGroupGenerated = false;
    private NotificationSender notificationSender;

    private boolean isInitialized;
    private boolean isStarted;
    private boolean isTerminated;

    @Override
    public IConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    /* see javadoc */
    public synchronized IDistributionClientResult updateConfiguration(IConfiguration conf) {

        log.info("update DistributionClient configuration");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);

        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }

        IDistributionClientResult updateResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "configuration updated successfuly");

        boolean needToUpdateCambriaConsumer = false;

        if (conf.getRelevantArtifactTypes() != null && !conf.getRelevantArtifactTypes().isEmpty()) {
            configuration.setRelevantArtifactTypes(conf.getRelevantArtifactTypes());
            needToUpdateCambriaConsumer = true;
        }
        if (isPollingIntervalValid(conf.getPollingInterval())) {
            configuration.setPollingInterval(conf.getPollingInterval());
            needToUpdateCambriaConsumer = true;
        }
        if (isPollingTimeoutValid(conf.getPollingTimeout())) {
            configuration.setPollingTimeout(conf.getPollingTimeout());
            needToUpdateCambriaConsumer = true;
        }
        if (conf.getConsumerGroup() != null) {
            configuration.setConsumerGroup(conf.getConsumerGroup());
            isConsumerGroupGenerated = false;
            needToUpdateCambriaConsumer = true;
        } else if (!isConsumerGroupGenerated) {
            String generatedConsumerGroup = UUID.randomUUID().toString();
            configuration.setConsumerGroup(generatedConsumerGroup);
            isConsumerGroupGenerated = true;
        }

        if (needToUpdateCambriaConsumer) {
            updateResult = restartConsumer();
        }

        return updateResult;
    }

    @Override
    /**
     * Start polling the Notification topic
     */
    public synchronized IDistributionClientResult start() {

        log.info("start DistributionClient");
        IDistributionClientResult startResult;
        CambriaConsumer cambriaNotificationConsumer = null;
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (errorWrapper.isEmpty()) {
            validateNotStarted(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            try {
                cambriaNotificationConsumer = new ConsumerBuilder().authenticatedBy(credential.getApiKey(), credential.getApiSecret()).knownAs(configuration.getConsumerGroup(), configuration.getConsumerID()).onTopic(notificationTopic).usingHttps(configuration.isUseHttpsWithDmaap()).usingHosts(brokerServers)
                        .withSocketTimeout(configuration.getPollingTimeout() * POLLING_TIMEOUT_MULTIPLIER).build();

            } catch (MalformedURLException | GeneralSecurityException e) {
                handleCambriaInitFailure(errorWrapper, e);
            }
        }
        if (errorWrapper.isEmpty()) {

            List<String> relevantArtifactTypes = configuration.getRelevantArtifactTypes();
            // Remove nulls from list - workaround for how configuration is built
            relevantArtifactTypes.removeAll(Collections.singleton(null));

            NotificationConsumer consumer = new NotificationConsumer(cambriaNotificationConsumer, callback, relevantArtifactTypes, this);
            executorPool = Executors.newScheduledThreadPool(DistributionClientConstants.POOL_SIZE);
            executorPool.scheduleAtFixedRate(consumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);

            handleStatusConsumer(errorWrapper, executorPool);
        }
        if (!errorWrapper.isEmpty()) {
            startResult = errorWrapper.getInnerElement();
        } else {
            startResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "distribution client started successfuly");
            isStarted = true;
        }
        return startResult;
    }

    private void handleStatusConsumer(Wrapper<IDistributionClientResult> errorWrapper, ScheduledExecutorService executorPool) {
        if (configuration.isConsumeProduceStatusTopic()) {
            CambriaConsumer cambriaStatusConsumer;
            try {
                cambriaStatusConsumer = new ConsumerBuilder().authenticatedBy(credential.getApiKey(), credential.getApiSecret()).knownAs(configuration.getConsumerGroup(), configuration.getConsumerID()).onTopic(statusTopic).usingHttps(configuration.isUseHttpsWithDmaap()).usingHosts(brokerServers)
                        .withSocketTimeout(configuration.getPollingTimeout() * POLLING_TIMEOUT_MULTIPLIER).build();
                StatusConsumer statusConsumer = new StatusConsumer(cambriaStatusConsumer, statusCallback);
                executorPool.scheduleAtFixedRate(statusConsumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);
            } catch (MalformedURLException | GeneralSecurityException e) {
                handleCambriaInitFailure(errorWrapper, e);
            }
        }
    }

    @Override
    /* see javadoc */
    public synchronized IDistributionClientResult stop() {

        log.info("stop DistributionClient");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        // 1. stop polling notification topic
        shutdownExecutor();

        // 2. send to ASDC unregister to topic
        IDistributionClientResult unregisterResult = asdcConnector.unregisterTopics(credential);
        if (unregisterResult.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
            log.info("client failed to unregister from topics");
        } else {
            log.info("client unregistered from topics successfully");
        }
        asdcConnector.close();

        try {
            cambriaIdentityManager.deleteCurrentApiKey();
        } catch (HttpException | IOException e) {
            log.debug("failed to delete cambria keys", e);
        }
        cambriaIdentityManager.close();

        isInitialized = false;
        isTerminated = true;

        DistributionClientResultImpl stopResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "distribution client stopped successfuly");
        return stopResult;
    }

    @Override
    public IDistributionClientDownloadResult download(IArtifactInfo artifactInfo) {
        log.info("DistributionClient - download");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            IDistributionClientResult result = errorWrapper.getInnerElement();
            IDistributionClientDownloadResult downloadResult = new DistributionClientDownloadResultImpl(result.getDistributionActionResult(), result.getDistributionMessageResult());
            return downloadResult;
        }
        return asdcConnector.downloadArtifact(artifactInfo);
    }

    @Override
    public synchronized IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback,
                                                       IStatusCallback statusCallback) {
        IDistributionClientResult initResult;
        if (!conf.isConsumeProduceStatusTopic()) {
            initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG, "configuration is invalid: isConsumeProduceStatusTopic() should be set to 'true'");

        } else if (isNull(statusCallback)) {
            initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG, "configuration is invalid: statusCallback is not defined");
        } else {
            this.statusCallback = statusCallback;
            initResult = init(conf, notificationCallback);
        }
        return initResult;
    }

    @Override
    /*
     * see javadoc
     */
    public synchronized IDistributionClientResult init(IConfiguration conf, INotificationCallback callback) {

        log.info("DistributionClient - init");

        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateNotInitilized(errorWrapper);
        if (errorWrapper.isEmpty()) {
            validateNotTerminated(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            this.configuration = validateAndInitConfiguration(errorWrapper, conf).getSecond();
            this.asdcConnector = createAsdcConnector(this.configuration);
        }
        // 1. get ueb server list from configuration
        if (errorWrapper.isEmpty()) {
            List<String> servers = initUebServerList(errorWrapper);
            if (servers != null) {
                this.brokerServers = servers;
            }
        }
        // 2.validate artifact types against asdc server
        if (errorWrapper.isEmpty()) {
            validateArtifactTypesWithAsdcServer(conf, errorWrapper);
        }
        // 3. create keys
        if (errorWrapper.isEmpty()) {
            this.callback = callback;
            ApiCredential apiCredential = createUebKeys(errorWrapper);
            if (apiCredential != null) {
                this.credential = apiCredential;
            }
        }
        // 4. register for topics
        if (errorWrapper.isEmpty()) {
            TopicRegistrationResponse topics = registerForTopics(errorWrapper, this.credential);
            if (topics != null) {
                this.notificationTopic = topics.getDistrNotificationTopicName();
                this.statusTopic = topics.getDistrStatusTopicName();
                this.notificationSender = createNotificationSender();
            }
        }

        IDistributionClientResult result;
        if (errorWrapper.isEmpty()) {
            isInitialized = true;
            result = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "distribution client initialized successfuly");
        } else {
            result = errorWrapper.getInnerElement();
        }

        return result;
    }

    SdcConnectorClient createAsdcConnector(Configuration configuration) {
        return new SdcConnectorClient(configuration, new HttpAsdcClient(configuration));
    }

    private NotificationSender createNotificationSender() {
        Either<CambriaBatchingPublisher, IDistributionClientResult> cambriaPublisher = getCambriaPublisher(statusTopic, configuration, brokerServers, credential);
        return new NotificationSender(cambriaPublisher, brokerServers);
    }

    private TopicRegistrationResponse registerForTopics(Wrapper<IDistributionClientResult> errorWrapper, ApiCredential credential) {
        Either<TopicRegistrationResponse, DistributionClientResultImpl> registerAsdcTopics = asdcConnector.registerAsdcTopics(credential);
        if (registerAsdcTopics.isRight()) {

            try {
                cambriaIdentityManager.deleteCurrentApiKey();
            } catch (HttpException | IOException e) {
                log.debug("failed to delete cambria keys", e);
            }
            errorWrapper.setInnerElement(registerAsdcTopics.right().value());
        } else {
            return registerAsdcTopics.left().value();
        }
        return null;
    }

    private ApiCredential createUebKeys(Wrapper<IDistributionClientResult> errorWrapper) {
        ApiCredential apiCredential = null;

        initCambriaClient(errorWrapper);
        if (errorWrapper.isEmpty()) {
            log.debug("create keys");
            Pair<DistributionClientResultImpl, ApiCredential> uebKeys = createUebKeys();
            DistributionClientResultImpl createKeysResponse = uebKeys.getFirst();
            apiCredential = uebKeys.getSecond();
            if (createKeysResponse.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
                errorWrapper.setInnerElement(createKeysResponse);
            }
        }
        return apiCredential;
    }

    private void validateArtifactTypesWithAsdcServer(IConfiguration conf, Wrapper<IDistributionClientResult> errorWrapper) {
        Either<List<String>, IDistributionClientResult> eitherValidArtifactTypesList = asdcConnector.getValidArtifactTypesList();
        if (eitherValidArtifactTypesList.isRight()) {
            DistributionActionResultEnum errorType = eitherValidArtifactTypesList.right().value().getDistributionActionResult();
            // Support the case of a new client and older ASDC Server which does not have the API
            if (errorType != DistributionActionResultEnum.ASDC_NOT_FOUND) {
                errorWrapper.setInnerElement(eitherValidArtifactTypesList.right().value());
            }
        } else {
            final List<String> artifactTypesFromAsdc = eitherValidArtifactTypesList.left().value();
            boolean isArtifactTypesValid = artifactTypesFromAsdc.containsAll(conf.getRelevantArtifactTypes());
            if (!isArtifactTypesValid) {
                List<String> invalidArtifactTypes = new ArrayList<>();
                invalidArtifactTypes.addAll(conf.getRelevantArtifactTypes());
                invalidArtifactTypes.removeAll(artifactTypesFromAsdc);
                DistributionClientResultImpl errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_CONTAINS_INVALID_ARTIFACT_TYPES,
                        "configuration contains invalid artifact types:" + invalidArtifactTypes + " valid types are:" + artifactTypesFromAsdc);
                errorWrapper.setInnerElement(errorResponse);
            } else {
                log.debug("Artifact types: {} were validated with ASDC server", conf.getRelevantArtifactTypes());
            }
        }
    }

    private List<String> initUebServerList(Wrapper<IDistributionClientResult> errorWrapper) {
        List<String> brokerServers = null;
        log.debug("get ueb cluster server list from component(configuration file)");

        Either<List<String>, IDistributionClientResult> serverListResponse = getUEBServerList();
        if (serverListResponse.isRight()) {
            errorWrapper.setInnerElement(serverListResponse.right().value());
        } else {
            brokerServers = serverListResponse.left().value();
        }

        return brokerServers;
    }

    private void validateNotInitilized(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isInitialized) {
            log.warn("distribution client already initialized");
            DistributionClientResultImpl alreadyInitResponse = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_INITIALIZED, "distribution client already initialized");
            errorWrapper.setInnerElement(alreadyInitResponse);
        }
    }

    @Override
    public IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage) {
        log.info("DistributionClient - sendDownloadStatus");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
    }

    private IDistributionClientResult sendStatus(IDistributionStatusMessageJsonBuilder builder) {
        return notificationSender.send(builder.build());
    }

    private Either<CambriaBatchingPublisher, IDistributionClientResult> getCambriaPublisher(String statusTopic, Configuration configuration, List<String> brokerServers, ApiCredential credential) {
        CambriaBatchingPublisher cambriaPublisher = null;
        try {
            cambriaPublisher = new PublisherBuilder().onTopic(statusTopic).usingHttps(configuration.isUseHttpsWithDmaap())
                    .usingHosts(brokerServers).build();
            cambriaPublisher.setApiCredentials(credential.getApiKey(), credential.getApiSecret());
        } catch (MalformedURLException | GeneralSecurityException e) {
            Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
            handleCambriaInitFailure(errorWrapper, e);
            return Either.right(errorWrapper.getInnerElement());
        }
        return Either.left(cambriaPublisher);
    }

    @Override
    public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage) {
        log.info("DistributionClient - sendDeploymentStatus");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
    }

    IDistributionClientResult sendNotificationStatus(long currentTimeMillis, String distributionId, ArtifactInfoImpl artifactInfo, boolean isNotified) {
        log.info("DistributionClient - sendNotificationStatus");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        return sendStatus(DistributionStatusMessageJsonBuilderFactory.prepareBuilderForNotificationStatus(getConfiguration().getConsumerID(), currentTimeMillis, distributionId, artifactInfo, isNotified));
    }

    /* *************************** Private Methods *************************************************** */

    protected Pair<DistributionClientResultImpl, ApiCredential> createUebKeys() {
        DistributionClientResultImpl response = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "keys created successfuly");
        ApiCredential credential = null;
        try {
            String description = String.format(DistributionClientConstants.CLIENT_DESCRIPTION, configuration.getConsumerID());
            credential = cambriaIdentityManager.createApiKey(DistributionClientConstants.EMAIL, description);
            cambriaIdentityManager.setApiCredentials(credential.getApiKey(), credential.getApiSecret());

        } catch (HttpException | CambriaApiException | IOException e) {
            response = new DistributionClientResultImpl(DistributionActionResultEnum.UEB_KEYS_CREATION_FAILED, "failed to create keys: " + e.getMessage());
            log.error(response.toString());
        }
        return new Pair<>(response, credential);
    }

    private IDistributionClientResult restartConsumer() {
        shutdownExecutor();
        return start();
    }

    protected Pair<DistributionActionResultEnum, Configuration> validateAndInitConfiguration(Wrapper<IDistributionClientResult> errorWrapper, IConfiguration conf) {
        DistributionActionResultEnum result = DistributionActionResultEnum.SUCCESS;
        Configuration configuration = null;
        if (conf == null) {
            result = DistributionActionResultEnum.CONFIGURATION_IS_MISSING;
        } else if (conf.getConsumerID() == null || conf.getConsumerID().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID;
        } else if (conf.getUser() == null || conf.getUser().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_USERNAME;
        } else if (conf.getPassword() == null || conf.getPassword().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_PASSWORD;
        } else if (conf.getMsgBusAddress() == null || conf.getMsgBusAddress().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_MSG_BUS_ADDRESS;
        } else if (conf.getAsdcAddress() == null || conf.getAsdcAddress().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_ASDC_FQDN;
        } else if (!isValidFqdn(conf.getAsdcAddress())) {
            result = DistributionActionResultEnum.CONF_INVALID_ASDC_FQDN;
        } else if (!isValidFqdns(conf.getMsgBusAddress())) {
            result = DistributionActionResultEnum.CONF_INVALID_MSG_BUS_ADDRESS;
        } else if (conf.getEnvironmentName() == null || conf.getEnvironmentName().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME;
        } else if (conf.getRelevantArtifactTypes() == null || conf.getRelevantArtifactTypes().isEmpty()) {
            result = DistributionActionResultEnum.CONF_MISSING_ARTIFACT_TYPES;
        } else if (conf.isConsumeProduceStatusTopic() && Objects.isNull(statusCallback)) {
            result = DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG;
        } else { // DistributionActionResultEnum.SUCCESS
            configuration = createConfiguration(conf);
        }

        if (result != DistributionActionResultEnum.SUCCESS) {

            DistributionClientResultImpl initResult = new DistributionClientResultImpl(result, "configuration is invalid: " + result.name());

            log.error(initResult.toString());
            errorWrapper.setInnerElement(initResult);
        }
        return new Pair<>(result, configuration);
    }

    private Configuration createConfiguration(IConfiguration conf) {
        Configuration configuration = new Configuration(conf);
        if (!isPollingIntervalValid(conf.getPollingInterval())) {
            configuration.setPollingInterval(DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
        }
        if (!isPollingTimeoutValid(conf.getPollingTimeout())) {
            configuration.setPollingTimeout(DistributionClientConstants.POLLING_TIMEOUT_SEC);
        }
        if (conf.getConsumerGroup() == null) {
            String generatedConsumerGroup = UUID.randomUUID().toString();
            configuration.setConsumerGroup(generatedConsumerGroup);
            isConsumerGroupGenerated = true;
        }

        //Default use HTTPS with SDC
        if (conf.isUseHttpsWithSDC() == null) {
            configuration.setUseHttpsWithSDC(true);
        }

        //Default use HTTPS with DMAAP
        if (conf.isUseHttpsWithDmaap() == null) {
            configuration.setUseHttpsWithDmaap(true);
        }

        return configuration;
    }

    protected boolean isValidFqdn(String fqdn) {
        try {
            Matcher matcher = DistributionClientConstants.FQDN_PATTERN.matcher(fqdn);
            return matcher.matches();
        } catch (Exception e) {
        }
        return false;
    }

    protected boolean isValidFqdns(List<String> fqdns) {
        if (fqdns != null && !fqdns.isEmpty()) {
            for (String fqdn : fqdns) {
                if (isValidFqdn(fqdn)) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void shutdownExecutor() {
        if (executorPool == null) {
            return;
        }

        executorPool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executorPool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                executorPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorPool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        } finally {
            isStarted = false;
        }
    }

    private void validateRunReady(Wrapper<IDistributionClientResult> errorWrapper) {
        if (errorWrapper.isEmpty()) {
            validateInitilized(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            validateNotTerminated(errorWrapper);
        }

    }

    private void validateInitilized(Wrapper<IDistributionClientResult> errorWrapper) {
        if (!isInitialized) {
            log.debug("client was not initialized");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_NOT_INITIALIZED, "distribution client was not initialized");
            errorWrapper.setInnerElement(result);
        }
    }

    private void validateNotStarted(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isStarted) {
            log.debug("client already started");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_STARTED, "distribution client already started");
            errorWrapper.setInnerElement(result);
        }
    }

    private void validateNotTerminated(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isTerminated) {
            log.debug("client was terminated");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_IS_TERMINATED, "distribution client was terminated");
            errorWrapper.setInnerElement(result);
        }
    }

    private boolean isPollingTimeoutValid(int timeout) {
        boolean isValid = (timeout >= DistributionClientConstants.POLLING_TIMEOUT_SEC);
        if (!isValid) {
            log.warn("polling interval is out of range. value should be greater than or equals to " + DistributionClientConstants.POLLING_TIMEOUT_SEC);
            log.warn("setting polling interval to default: " + DistributionClientConstants.POLLING_TIMEOUT_SEC);
        }
        return isValid;
    }

    private boolean isPollingIntervalValid(int pollingInt) {
        boolean isValid = (pollingInt >= DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
        if (!isValid) {
            log.warn("polling interval is out of range. value should be greater than or equals to " + DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
            log.warn("setting polling interval to default: " + DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
        }
        return isValid;
    }

    private synchronized void initCambriaClient(Wrapper<IDistributionClientResult> errorWrapper) {
        if (cambriaIdentityManager == null) {
            try {
                AbstractAuthenticatedManagerBuilder<CambriaIdentityManager> managerBuilder = new IdentityManagerBuilder().usingHosts(brokerServers);
                if (configuration.isUseHttpsWithDmaap()) {
                    managerBuilder = managerBuilder.usingHttps();
                }
                cambriaIdentityManager = managerBuilder.build();
            } catch (MalformedURLException | GeneralSecurityException e) {
                handleCambriaInitFailure(errorWrapper, e);
            }
        }
    }

    private void handleCambriaInitFailure(Wrapper<IDistributionClientResult> errorWrapper, Exception e) {
        final String errorMessage = "Failed initilizing cambria component:" + e.getMessage();
        IDistributionClientResult errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.CAMBRIA_INIT_FAILED, errorMessage);
        errorWrapper.setInnerElement(errorResponse);
        log.error(errorMessage);
        log.debug(errorMessage, e);
    }

    @Override
    public IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage, String errorReason) {
        log.info("DistributionClient - sendDownloadStatus with errorReason");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));

    }

    @Override
    public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage, String errorReason) {
        log.info("DistributionClient - sendDeploymentStatus with errorReason");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));

    }

    private IDistributionClientResult sendErrorStatus(IDistributionStatusMessageJsonBuilder errorReasonBuilder) {
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        return sendStatus(errorReasonBuilder);
    }

    @Override
    public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage) {
        log.info("DistributionClient - sendComponentDone status");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));

    }

    @Override
    public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage,
                                                             String errorReason) {
        log.info("DistributionClient - sendComponentDone status with errorReason");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));
    }


    @Override
    public List<IVfModuleMetadata> decodeVfModuleArtifact(byte[] artifactPayload) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String vfModuleJsonString = new String(artifactPayload, StandardCharsets.UTF_8);
        final Type type = new TypeToken<List<VfModuleMetadata>>() {
        }.getType();
        List<IVfModuleMetadata> vfModules = gson.fromJson(vfModuleJsonString, type);
        return vfModules;
    }


    public IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage) {
        log.info("DistributionClient - sendFinalDistributionStatus status");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));

    }


    @Override
    public IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage,
                                                          String errorReason) {
        log.info("DistributionClient - sendFinalDistributionStatus status with errorReason");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));


    }

    public Either<List<String>, IDistributionClientResult> getUEBServerList() {
        List<String> msgBusAddresses = configuration.getMsgBusAddress();
        if (msgBusAddresses.isEmpty()) {
            return Either.right(new DistributionClientResultImpl(DistributionActionResultEnum.CONF_MISSING_MSG_BUS_ADDRESS, "Message bus address was not found in the config file"));
        } else {
            return GeneralUtils.convertToValidHostName(msgBusAddresses);
        }
    }


}
