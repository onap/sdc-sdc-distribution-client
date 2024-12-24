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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fj.data.Either;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpHost;
import org.apache.kafka.common.KafkaException;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.IDistributionStatusMessageJsonBuilder;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.IVfModuleMetadata;
import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.http.HttpClientFactory;
import org.onap.sdc.http.HttpRequestFactory;
import org.onap.sdc.http.HttpSdcClient;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.utils.NotificationSender;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.Wrapper;
import org.onap.sdc.utils.kafka.KafkaDataResponse;
import org.onap.sdc.utils.kafka.SdcKafkaConsumer;
import org.onap.sdc.utils.kafka.SdcKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionClientImpl implements IDistributionClient {

    private static final int TERMINATION_TIMEOUT = 60;
    private final Logger log;

    private SdcConnectorClient sdcConnector;
    private ScheduledExecutorService executorPool = null;
    private SdcKafkaProducer producer;
    protected Configuration configuration;
    private INotificationCallback callback;
    private IStatusCallback statusCallback;
    private boolean isConsumerGroupGenerated = false;
    private NotificationSender notificationSender;
    private final ConfigurationValidator configurationValidator = new ConfigurationValidator();

    private boolean isInitialized;
    private boolean isStarted;
    private boolean isTerminated;

    public DistributionClientImpl() {
        this(LoggerFactory.getLogger(DistributionClientImpl.class));
    }

    public DistributionClientImpl(Logger log) {
        this.log = log;
    }

    @Override
    public synchronized IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback, IStatusCallback statusCallback) {
        IDistributionClientResult initResult;
        if (!conf.isConsumeProduceStatusTopic()) {
            initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG,
                "configuration is invalid: isConsumeProduceStatusTopic() should be set to 'true'");

        } else if (isNull(statusCallback)) {
            initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG,
                "configuration is invalid: statusCallback is not defined");
        } else {
            this.statusCallback = statusCallback;
            initResult = init(conf, notificationCallback);
        }
        return initResult;
    }

    @Override
    public synchronized IDistributionClientResult init(IConfiguration conf, INotificationCallback callback) {

        log.info("DistributionClient - init");

        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateNotInitilized(errorWrapper);
        if (errorWrapper.isEmpty()) {
            validateNotTerminated(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            this.configuration = validateAndInitConfiguration(errorWrapper, conf).getSecond();
            this.sdcConnector = createSdcConnector(configuration);
        }
        if (errorWrapper.isEmpty()) {
            validateArtifactTypesWithSdcServer(conf, errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            this.callback = callback;
        }
        if (errorWrapper.isEmpty()) {
            initKafkaData(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            initKafkaProducer(errorWrapper, configuration);
        }
        if (errorWrapper.isEmpty()) {
            this.notificationSender = new NotificationSender(producer);
        }
        IDistributionClientResult result;
        if (errorWrapper.isEmpty()) {
            isInitialized = true;
            result = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS,
                "distribution client initialized successfully");
        } else {
            result = errorWrapper.getInnerElement();
        }

        return result;
    }

    @Override
    public IConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public synchronized IDistributionClientResult updateConfiguration(IConfiguration conf) {

        log.info("update DistributionClient configuration");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);

        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }

        IDistributionClientResult updateResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS,
            "configuration updated successfully");

        boolean needToUpdateConsumer = false;

        if (conf.getRelevantArtifactTypes() != null && !conf.getRelevantArtifactTypes().isEmpty()) {
            configuration.setRelevantArtifactTypes(conf.getRelevantArtifactTypes());
            needToUpdateConsumer = true;
        }
        if (isPollingIntervalValid(conf.getPollingInterval())) {
            configuration.setPollingInterval(conf.getPollingInterval());
            needToUpdateConsumer = true;
        }
        if (isPollingTimeoutValid(conf.getPollingTimeout())) {
            configuration.setPollingTimeout(conf.getPollingTimeout());
            needToUpdateConsumer = true;
        }
        if (conf.getConsumerGroup() != null) {
            configuration.setConsumerGroup(conf.getConsumerGroup());
            isConsumerGroupGenerated = false;
            needToUpdateConsumer = true;
        } else if (!isConsumerGroupGenerated) {
            String generatedConsumerGroup = UUID.randomUUID().toString();
            configuration.setConsumerGroup(generatedConsumerGroup);
            isConsumerGroupGenerated = true;
        }

        if (needToUpdateConsumer) {
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
        SdcKafkaConsumer kafkaConsumer = null;
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (errorWrapper.isEmpty()) {
            validateNotStarted(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            try {
                kafkaConsumer = new SdcKafkaConsumer(configuration);
                kafkaConsumer.subscribe(configuration.getNotificationTopicName());
            } catch (KafkaException | IllegalArgumentException e) {
                handleMessagingClientInitFailure(errorWrapper, e);
            }
        }
        if (errorWrapper.isEmpty()) {
            startNotificationConsumer(kafkaConsumer);
            startStatusConsumer(errorWrapper, executorPool);
        }
        if (!errorWrapper.isEmpty()) {
            startResult = errorWrapper.getInnerElement();
        } else {
            startResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS,
                "distribution client started successfully");
            isStarted = true;
        }
        return startResult;
    }

    private void startNotificationConsumer(SdcKafkaConsumer kafkaConsumer) {
        List<String> relevantArtifactTypes = configuration.getRelevantArtifactTypes();
        // Remove nulls from list - workaround for how configuration is built
        relevantArtifactTypes.removeAll(Collections.singleton(null));
        NotificationConsumer consumer = new NotificationConsumer(kafkaConsumer, callback, relevantArtifactTypes, this);
        executorPool = Executors.newScheduledThreadPool(DistributionClientConstants.POOL_SIZE);
        executorPool.scheduleAtFixedRate(consumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);
    }

    private void startStatusConsumer(Wrapper<IDistributionClientResult> errorWrapper, ScheduledExecutorService executorPool) {
        if (configuration.isConsumeProduceStatusTopic()) {
            try {
                SdcKafkaConsumer kafkaConsumer = new SdcKafkaConsumer(configuration);
                kafkaConsumer.subscribe(configuration.getStatusTopicName());
                StatusConsumer statusConsumer = new StatusConsumer(kafkaConsumer, statusCallback);
                executorPool.scheduleAtFixedRate(statusConsumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);
            } catch (KafkaException | IllegalArgumentException e) {
                handleMessagingClientInitFailure(errorWrapper, e);
            }
        }
    }

    @Override
    public synchronized IDistributionClientResult stop() {

        log.info("stop DistributionClient");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        shutdownExecutor();

        sdcConnector.close();
        isInitialized = false;
        isTerminated = true;

        return new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "distribution client stopped successfully");
    }

    @Override
    public IDistributionClientDownloadResult download(IArtifactInfo artifactInfo) {
        log.info("DistributionClient - download");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            IDistributionClientResult result = errorWrapper.getInnerElement();
            return new DistributionClientDownloadResultImpl(result.getDistributionActionResult(), result.getDistributionMessageResult());
        }
        return sdcConnector.downloadArtifact(artifactInfo);
    }

    SdcConnectorClient createSdcConnector(Configuration configuration) {
        return new SdcConnectorClient(configuration, new HttpSdcClient(configuration.getSdcAddress(),
            new HttpClientFactory(configuration),
            new HttpRequestFactory(configuration.getUser(), configuration.getPassword())));
    }

    private void validateArtifactTypesWithSdcServer(IConfiguration conf, Wrapper<IDistributionClientResult> errorWrapper) {
        Either<List<String>, IDistributionClientResult> eitherValidArtifactTypesList = sdcConnector.getValidArtifactTypesList();
        if (eitherValidArtifactTypesList.isRight()) {
            DistributionActionResultEnum errorType = eitherValidArtifactTypesList.right().value().getDistributionActionResult();
            // Support the case of a new client and older SDC Server which does not have the API
            if (errorType != DistributionActionResultEnum.SDC_NOT_FOUND) {
                errorWrapper.setInnerElement(eitherValidArtifactTypesList.right().value());
            }
        } else {
            final List<String> artifactTypesFromSdc = eitherValidArtifactTypesList.left().value();
            boolean isArtifactTypesValid = artifactTypesFromSdc.containsAll(conf.getRelevantArtifactTypes());
            if (!isArtifactTypesValid) {
                List<String> invalidArtifactTypes = new ArrayList<>(conf.getRelevantArtifactTypes());
                invalidArtifactTypes.removeAll(artifactTypesFromSdc);
                DistributionClientResultImpl errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_CONTAINS_INVALID_ARTIFACT_TYPES,
                        "configuration contains invalid artifact types:" + invalidArtifactTypes + " valid types are:" + artifactTypesFromSdc);
                errorWrapper.setInnerElement(errorResponse);
            } else {
                log.debug("Artifact types: {} were validated with SDC server", conf.getRelevantArtifactTypes());
            }
        }
    }

    private void initKafkaData(Wrapper<IDistributionClientResult> errorWrapper) {
        log.debug("Get MessageBus cluster information from SDC");
        Either<KafkaDataResponse, IDistributionClientResult> kafkaData = sdcConnector.getKafkaDistData();
        if (kafkaData.isRight()) {
            errorWrapper.setInnerElement(kafkaData.right().value());
        } else {
            KafkaDataResponse kafkaDataResponse = kafkaData.left().value();
            configuration.setMsgBusAddress(kafkaDataResponse.getKafkaBootStrapServer());
            configuration.setNotificationTopicName(kafkaDataResponse.getDistrNotificationTopicName());
            configuration.setStatusTopicName(kafkaDataResponse.getDistrStatusTopicName());
            log.debug("MessageBus cluster info retrieved successfully {}", kafkaData.left().value());
        }
    }

    private void validateNotInitilized(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isInitialized) {
            log.warn("distribution client already initialized");
            DistributionClientResultImpl alreadyInitResponse = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_INITIALIZED,
                "distribution client already initialized");
            errorWrapper.setInnerElement(alreadyInitResponse);
        }
    }

    @Override
    public IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage) {
        log.info("DistributionClient - sendDownloadStatus");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
    }

    private IDistributionClientResult sendStatus(IDistributionStatusMessageJsonBuilder statusBuilder) {
        return notificationSender.send(configuration.getStatusTopicName(), statusBuilder.build());
    }

    private void initKafkaProducer(Wrapper<IDistributionClientResult> errorWrapper, Configuration configuration) {
        try {
            if (producer == null) {
                producer = new SdcKafkaProducer(configuration);
            }
        } catch (KafkaException | IllegalStateException e) {
            handleMessagingClientInitFailure(errorWrapper, e);
        }
    }

    @Override
    public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage) {
        log.info("DistributionClient - sendDeploymentStatus");
        return sendErrorStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
    }

        @Override
    public IDistributionClientResult sendNotificationStatus(StatusMessage status) {
        log.info("DistributionClient - sendNotificationStatus");
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        IDistributionStatusMessageJsonBuilder builder = DistributionStatusMessageJsonBuilderFactory.prepareBuilderForNotificationStatus(
            getConfiguration().getConsumerID(),
            status.getTimestamp(),
            status.getDistributionID(),
            status.getArtifactURL(),
            status.getStatus());
        return sendStatus(builder);
    }

    /* *************************** Private Methods *************************************************** */

    private IDistributionClientResult restartConsumer() {
        shutdownExecutor();
        return start();
    }

    protected Pair<DistributionActionResultEnum, Configuration> validateAndInitConfiguration(Wrapper<IDistributionClientResult> errorWrapper, IConfiguration conf) {
        DistributionActionResultEnum result = configurationValidator.validateConfiguration(conf, statusCallback);

        Configuration configurationInit = null;
        if (result == DistributionActionResultEnum.SUCCESS) {
            configurationInit = createConfiguration(conf);
        } else {
            DistributionClientResultImpl initResult = new DistributionClientResultImpl(result, "configuration is invalid: " + result.name());
            log.error(initResult.toString());
            errorWrapper.setInnerElement(initResult);
        }

        return new Pair<>(result, configurationInit);
    }

    private Configuration createConfiguration(IConfiguration conf) {
        Configuration configurationCreate = new Configuration(conf);
        if (!isPollingIntervalValid(conf.getPollingInterval())) {
            configurationCreate.setPollingInterval(DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
        }
        if (!isPollingTimeoutValid(conf.getPollingTimeout())) {
            configurationCreate.setPollingTimeout(DistributionClientConstants.POLLING_TIMEOUT_SEC);
        }
        if (conf.getConsumerGroup() == null) {
            String generatedConsumerGroup = UUID.randomUUID().toString();
            configurationCreate.setConsumerGroup(generatedConsumerGroup);
            isConsumerGroupGenerated = true;
        }
        //Default use HTTPS with SDC
        if (conf.isUseHttpsWithSDC() == null) {
            configurationCreate.setUseHttpsWithSDC(true);
        }
        return configurationCreate;
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
            validateInitialized(errorWrapper);
        }
        if (errorWrapper.isEmpty()) {
            validateNotTerminated(errorWrapper);
        }

    }

    private void validateInitialized(Wrapper<IDistributionClientResult> errorWrapper) {
        if (!isInitialized) {
            log.debug("client was not initialized");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_NOT_INITIALIZED,
                "distribution client was not initialized");
            errorWrapper.setInnerElement(result);
        }
    }

    private void validateNotStarted(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isStarted) {
            log.debug("client already started");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_ALREADY_STARTED,
                "distribution client already started");
            errorWrapper.setInnerElement(result);
        }
    }

    private void validateNotTerminated(Wrapper<IDistributionClientResult> errorWrapper) {
        if (isTerminated) {
            log.debug("client was terminated");
            IDistributionClientResult result = new DistributionClientResultImpl(DistributionActionResultEnum.DISTRIBUTION_CLIENT_IS_TERMINATED,
                "distribution client was terminated");
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


    private void handleMessagingClientInitFailure(Wrapper<IDistributionClientResult> errorWrapper, Exception e) {
        final String errorMessage = "Failed initializing messaging component:" + e.getMessage();
        IDistributionClientResult errorResponse = new DistributionClientResultImpl(DistributionActionResultEnum.MESSAGING_CLIENT_INIT_FAILED, errorMessage);
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

    private IDistributionClientResult sendErrorStatus(IDistributionStatusMessageJsonBuilder builder) {
        Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
        validateRunReady(errorWrapper);
        if (!errorWrapper.isEmpty()) {
            return errorWrapper.getInnerElement();
        }
        return sendStatus(builder);
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
        return gson.fromJson(vfModuleJsonString, type);
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

    private HttpHost getHttpProxyHost() {
        HttpHost proxyHost = null;
        if (Boolean.TRUE.equals(configuration.isUseSystemProxy() && System.getProperty("http.proxyHost") != null) && System.getProperty("http.proxyPort") != null) {
            proxyHost = new HttpHost(System.getProperty("http.proxyHost"),
                    Integer.parseInt(System.getProperty("http.proxyPort")));
        } else if (configuration.getHttpProxyHost() != null && configuration.getHttpProxyPort() != 0) {
            proxyHost = new HttpHost(configuration.getHttpProxyHost(), configuration.getHttpProxyPort());
        }
        return proxyHost;
    }

    private HttpHost getHttpsProxyHost() {
        HttpHost proxyHost = null;
        if (configuration.isUseSystemProxy() && System.getProperty("https.proxyHost") != null && System.getProperty("https.proxyPort") != null) {
            proxyHost = new HttpHost(System.getProperty("https.proxyHost"),
                    Integer.parseInt(System.getProperty("https.proxyPort")));
        } else if (configuration.getHttpsProxyHost() != null && configuration.getHttpsProxyPort() != 0) {
            proxyHost = new HttpHost(configuration.getHttpsProxyHost(), configuration.getHttpsProxyPort());
        }
        return proxyHost;
    }


}
