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

package org.onap.sdc.impl;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.IDistributionStatusMessageJsonBuilder;
import org.onap.sdc.api.consumer.*;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.http.SdcConnectorClient;
import org.onap.sdc.http.TopicRegistrationResponse;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionClientConstants;
import org.onap.sdc.utils.GeneralUtils;
import org.onap.sdc.utils.Wrapper;
import org.onap.sdc.api.consumer.*;
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
import com.att.nsa.cambria.client.CambriaPublisher.message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fj.data.Either;

public class DistributionClientImpl implements IDistributionClient {

	private static Logger log = LoggerFactory.getLogger(DistributionClientImpl.class.getName());

	protected SdcConnectorClient asdcConnector = new SdcConnectorClient();
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

	private boolean isInitialized, isStarted, isTerminated;

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
			generateConsumerGroup();
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
						.withSocketTimeout(configuration.getPollingTimeout() * 1000).build();
				
			} catch (MalformedURLException | GeneralSecurityException e) {
				handleCambriaInitFailure(errorWrapper, e);
			}
		}
		if (errorWrapper.isEmpty()) {
			
			List<String> relevantArtifactTypes = configuration.getRelevantArtifactTypes();
			// Remove nulls from list - workaround for how configuration is built
			while (relevantArtifactTypes.remove(null));
			
			NotificationConsumer consumer = new NotificationConsumer(cambriaNotificationConsumer, callback, relevantArtifactTypes, this);
			executorPool = Executors.newScheduledThreadPool(DistributionClientConstants.POOL_SIZE);
			executorPool.scheduleAtFixedRate(consumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);
			
			handleStatusConsumer(errorWrapper, executorPool);
		}
		if (!errorWrapper.isEmpty()) {
			startResult =  errorWrapper.getInnerElement();
		}
		else{
			startResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "distribution client started successfuly");
			isStarted = true;
		}
		return startResult;
	}

	private void handleStatusConsumer(Wrapper<IDistributionClientResult> errorWrapper, ScheduledExecutorService executorPool) {
		if( configuration.isConsumeProduceStatusTopic()){
			CambriaConsumer cambriaStatusConsumer = null;
			try {
				cambriaStatusConsumer = new ConsumerBuilder().authenticatedBy(credential.getApiKey(), credential.getApiSecret()).knownAs(configuration.getConsumerGroup(), configuration.getConsumerID()).onTopic(statusTopic).usingHttps(configuration.isUseHttpsWithDmaap()).usingHosts(brokerServers)
						.withSocketTimeout(configuration.getPollingTimeout() * 1000).build();
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
		return asdcConnector.dowloadArtifact(artifactInfo);
	}
	@Override
	public synchronized IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback,
			IStatusCallback statusCallback) {
		IDistributionClientResult initResult;
		if( !conf.isConsumeProduceStatusTopic() ){
			initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG, "configuration is invalid: isConsumeProduceStatusTopic() should be set to 'true'" );

		}
		else if( isNull(statusCallback) ){
			initResult = new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG, "configuration is invalid: statusCallback is not defined" );
		}
		else{
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
			validateAndInitConfiguration(errorWrapper, conf);
		}
		// 1. get ueb server list from configuration
		if (errorWrapper.isEmpty()) {
			initUebServerList(errorWrapper);
		}
		// 2.validate artifact types against asdc server
		if (errorWrapper.isEmpty()) {
			validateArtifactTypesWithAsdcServer(conf, errorWrapper);
		}
		// 3. create keys
		if (errorWrapper.isEmpty()) {
			this.callback = callback;
			createUebKeys(errorWrapper);
		}
		// 4. register for topics
		if (errorWrapper.isEmpty()) {
			registerForTopics(errorWrapper);
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

	private void registerForTopics(Wrapper<IDistributionClientResult> errorWrapper) {
		Either<TopicRegistrationResponse, DistributionClientResultImpl> registerAsdcTopics = asdcConnector.registerAsdcTopics(credential);
		if (registerAsdcTopics.isRight()) {

			try {
				cambriaIdentityManager.deleteCurrentApiKey();
			} catch (HttpException | IOException e) {
				log.debug("failed to delete cambria keys", e);
			}
			errorWrapper.setInnerElement(registerAsdcTopics.right().value());
		} else {
			TopicRegistrationResponse topics = registerAsdcTopics.left().value();
			notificationTopic = topics.getDistrNotificationTopicName();
			statusTopic = topics.getDistrStatusTopicName();
		}

	}

	private void createUebKeys(Wrapper<IDistributionClientResult> errorWrapper) {
		initCambriaClient(errorWrapper);
		if (errorWrapper.isEmpty()) {
			log.debug("create keys");
			DistributionClientResultImpl createKeysResponse = createUebKeys();
			if (createKeysResponse.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
				errorWrapper.setInnerElement(createKeysResponse);
			}
		}
	}

	private void validateArtifactTypesWithAsdcServer(IConfiguration conf, Wrapper<IDistributionClientResult> errorWrapper) {
		asdcConnector.init(configuration);
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

	private void initUebServerList(Wrapper<IDistributionClientResult> errorWrapper) {
		log.debug("get ueb cluster server list from component(configuration file)");

		Either<List<String>, IDistributionClientResult> serverListResponse = getUEBServerList();
		if (serverListResponse.isRight()) {
			errorWrapper.setInnerElement(serverListResponse.right().value());
		} else {

			brokerServers = serverListResponse.left().value();
		}

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
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}

		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
	}

	private IDistributionClientResult sendStatus(IDistributionStatusMessageJsonBuilder builder) {
		DistributionClientResultImpl statusResult = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
		log.info("DistributionClient - sendStatus");
		Either<CambriaBatchingPublisher, IDistributionClientResult> eitherPublisher = getCambriaPublisher();
		if (eitherPublisher.isRight()) {
			return eitherPublisher.right().value();
		}
		CambriaBatchingPublisher pub = eitherPublisher.left().value();

		log.debug("after create publisher server list " + brokerServers.toString());
		String jsonRequest = builder.build();

		log.debug("try to send status " + jsonRequest);

		try {
			pub.send("MyPartitionKey", jsonRequest);
			Thread.sleep(1000L);
		} catch (IOException e) {
			log.debug("DistributionClient - sendDownloadStatus. Failed to send download status");
		} catch (InterruptedException e) {
			log.debug("DistributionClient - sendDownloadStatus. thread was interrupted");
		}

		finally {

			try {
				List<message> stuck = pub.close(10L, TimeUnit.SECONDS);

				if (!stuck.isEmpty()) {
					log.debug("DistributionClient - sendDownloadStatus. " + stuck.size() + " messages unsent");
				} else {
					statusResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "messages successfully sent");
				}
			} catch (IOException | InterruptedException e) {
				log.debug("DistributionClient - sendDownloadStatus. failed to send messages and close publisher ");
			}

		}
		return statusResult;
	}

	private Either<CambriaBatchingPublisher, IDistributionClientResult> getCambriaPublisher() {
		CambriaBatchingPublisher cambriaPublisher = null;
			try {
				cambriaPublisher = new PublisherBuilder().onTopic(statusTopic).usingHttps(configuration.isUseHttpsWithDmaap()).usingHosts(brokerServers).build();
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
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));
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

	protected DistributionClientResultImpl createUebKeys() {
		DistributionClientResultImpl response = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "keys created successfuly");
		try {
			String description = String.format(DistributionClientConstants.CLIENT_DESCRIPTION, configuration.getConsumerID());
			credential = cambriaIdentityManager.createApiKey(DistributionClientConstants.EMAIL, description);
			cambriaIdentityManager.setApiCredentials(credential.getApiKey(), credential.getApiSecret());

		} catch (HttpException | CambriaApiException | IOException e) {
			response = new DistributionClientResultImpl(DistributionActionResultEnum.UEB_KEYS_CREATION_FAILED, "failed to create keys: " + e.getMessage());
			log.error(response.toString());
		}
		return response;
	}

	private IDistributionClientResult restartConsumer() {
		shutdownExecutor();
		return start();
	}

	protected DistributionActionResultEnum validateAndInitConfiguration(Wrapper<IDistributionClientResult> errorWrapper, IConfiguration conf) {
		DistributionActionResultEnum result = DistributionActionResultEnum.SUCCESS;

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
		} else if (!isValidFqdns(conf.getMsgBusAddress())){
			result = DistributionActionResultEnum.CONF_INVALID_MSG_BUS_ADDRESS;
		} else if (conf.getEnvironmentName() == null || conf.getEnvironmentName().isEmpty()) {
			result = DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME;
		} else if (conf.getRelevantArtifactTypes() == null || conf.getRelevantArtifactTypes().isEmpty()) {
			result = DistributionActionResultEnum.CONF_MISSING_ARTIFACT_TYPES;
		}
		else if( conf.isConsumeProduceStatusTopic() && Objects.isNull(statusCallback) ){
			result = DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG;
		}
		// DistributionActionResultEnum.SUCCESS
		else {
			handleValidConf(conf);
		}

		if (result != DistributionActionResultEnum.SUCCESS) {

			DistributionClientResultImpl initResult = new DistributionClientResultImpl(result, "configuration is invalid: " + result.name());

			log.error(initResult.toString());
			errorWrapper.setInnerElement(initResult);
		}
		return result;
	}

	private void handleValidConf(IConfiguration conf) {
		this.configuration = new Configuration(conf);
		if (!isPollingIntervalValid(conf.getPollingInterval())) {
			configuration.setPollingInterval(DistributionClientConstants.MIN_POLLING_INTERVAL_SEC);
		}
		if (!isPollingTimeoutValid(conf.getPollingTimeout())) {
			configuration.setPollingTimeout(DistributionClientConstants.POLLING_TIMEOUT_SEC);
		}
		if (conf.getConsumerGroup() == null) {
			generateConsumerGroup();
		}
		
		//Default use HTTPS with DMAAP
		if (conf.isUseHttpsWithDmaap() == null){
			configuration.setUseHttpsWithDmaap(true);
		}
	}

	private void generateConsumerGroup() {
		String generatedConsumerGroup = UUID.randomUUID().toString();
		configuration.setConsumerGroup(generatedConsumerGroup);
		isConsumerGroupGenerated = true;
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
		if (executorPool == null)
			return;

		executorPool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!executorPool.awaitTermination(60, TimeUnit.SECONDS)) {
				executorPool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executorPool.awaitTermination(60, TimeUnit.SECONDS))
					log.error("Pool did not terminate");
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
				if (configuration.isUseHttpsWithDmaap()){
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
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}

		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));

	}

	@Override
	public IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage, String errorReason) {
		log.info("DistributionClient - sendDeploymentStatus with errorReason");
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));

	}

	@Override
	public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage) {
		log.info("DistributionClient - sendComponentDone status");
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));

	}
	
	@Override
	public IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage,
			String errorReason) {
		log.info("DistributionClient - sendComponentDone status with errorReason");
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));
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
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getSimpleBuilder(statusMessage));

	}

	
	@Override
	public IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage,
			String errorReason) {
		log.info("DistributionClient - sendFinalDistributionStatus status with errorReason");
		Wrapper<IDistributionClientResult> errorWrapper = new Wrapper<>();
		validateRunReady(errorWrapper);
		if (!errorWrapper.isEmpty()) {
			return errorWrapper.getInnerElement();
		}
		return sendStatus(DistributionStatusMessageJsonBuilderFactory.getErrorReasonBuilder(statusMessage, errorReason));
		
		
	}
	
	public Either<List<String>,IDistributionClientResult> getUEBServerList() {
		List<String> msgBusAddresses = configuration.getMsgBusAddress();
		if(msgBusAddresses.isEmpty()){
			return Either.right(new DistributionClientResultImpl(DistributionActionResultEnum.CONF_MISSING_MSG_BUS_ADDRESS, "Message bus address was not found in the config file"));
		}
		else{
			return GeneralUtils.convertToValidHostName(msgBusAddresses);
		}
	}

	

	

	
}
