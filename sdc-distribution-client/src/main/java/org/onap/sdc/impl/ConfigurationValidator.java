/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionClientConstants;

public class ConfigurationValidator {

    private Map<Function<IConfiguration, Boolean>, DistributionActionResultEnum> cachedValidators;

    public DistributionActionResultEnum validateConfiguration(IConfiguration conf, IStatusCallback statusCallback) {
        final Map<Function<IConfiguration, Boolean>, DistributionActionResultEnum> validators = getValidators(statusCallback);

        for (Map.Entry<Function<IConfiguration, Boolean>, DistributionActionResultEnum> validation : validators.entrySet()) {
            if (isConfigurationNotValid(conf, validation)) {
                return getError(validation);
            }
        }
        return DistributionActionResultEnum.SUCCESS;
    }

    private synchronized Map<Function<IConfiguration, Boolean>, DistributionActionResultEnum> getValidators(IStatusCallback statusCallback) {
        if(this.cachedValidators == null) {
            final Map<Function<IConfiguration, Boolean>, DistributionActionResultEnum> validators = new LinkedHashMap<>();
            validators.put(isConfigurationNotDefined(), DistributionActionResultEnum.CONFIGURATION_IS_MISSING);
            validators.put(isCustomerIdNotSet(), DistributionActionResultEnum.CONF_MISSING_CONSUMER_ID);
            validators.put(isUserNotSet(), DistributionActionResultEnum.CONF_MISSING_USERNAME);
            validators.put(isPasswordNotSet(), DistributionActionResultEnum.CONF_MISSING_PASSWORD);
            validators.put(isMsgBusAddressNotSet(), DistributionActionResultEnum.CONF_MISSING_MSG_BUS_ADDRESS);
            validators.put(isSdcAddressNotSet(), DistributionActionResultEnum.CONF_MISSING_SDC_FQDN);
            validators.put(isFqdnValid(), DistributionActionResultEnum.CONF_INVALID_SDC_FQDN);
            validators.put(isEnvNameNotSet(), DistributionActionResultEnum.CONF_MISSING_ENVIRONMENT_NAME);
            validators.put(isRelevantArtifactTypesNotSet(), DistributionActionResultEnum.CONF_MISSING_ARTIFACT_TYPES);
            validators.put(isConsumeStatusTopicWithCallbackNotSet(statusCallback), DistributionActionResultEnum.CONF_INVALID_CONSUME_PRODUCE_STATUS_TOPIC_FALG);
            this.cachedValidators = validators;
        }
        return this.cachedValidators;
    }

    private DistributionActionResultEnum getError(Map.Entry<Function<IConfiguration, Boolean>, DistributionActionResultEnum> validation) {
        return validation.getValue();
    }

    private boolean isConfigurationNotValid(IConfiguration conf, Map.Entry<Function<IConfiguration, Boolean>, DistributionActionResultEnum> validation) {
        return validation.getKey().apply(conf);
    }

    private Function<IConfiguration, Boolean> isConsumeStatusTopicWithCallbackNotSet(IStatusCallback statusCallback) {
        return it -> it.isConsumeProduceStatusTopic() && Objects.isNull(statusCallback);
    }

    private Function<IConfiguration, Boolean> isRelevantArtifactTypesNotSet() {
        return it -> it.getRelevantArtifactTypes() == null || it.getRelevantArtifactTypes().isEmpty();
    }

    private Function<IConfiguration, Boolean> isEnvNameNotSet() {
        return it -> it.getEnvironmentName() == null || it.getEnvironmentName().isEmpty();
    }

    private Function<IConfiguration, Boolean> areFqdnsValid() {
        return it -> !isValidFqdns(it.getMsgBusAddress());
    }

    private Function<IConfiguration, Boolean> isFqdnValid() {
        return it -> !isValidFqdn(it.getSdcAddress());
    }

    private Function<IConfiguration, Boolean> isMsgBusAddressNotSet() {
        return it -> it.getMsgBusAddress() == null || it.getMsgBusAddress().isEmpty();
    }

    private Function<IConfiguration, Boolean> isPasswordNotSet() {
        return it -> it.getPassword() == null || it.getPassword().isEmpty();
    }

    private Function<IConfiguration, Boolean> isConfigurationNotDefined() {
        return Objects::isNull;
    }

    private Function<IConfiguration, Boolean> isUserNotSet() {
        return it -> it.getUser() == null || it.getUser().isEmpty();
    }

    private Function<IConfiguration, Boolean> isCustomerIdNotSet() {
        return it -> it.getConsumerID() == null || it.getConsumerID().isEmpty();
    }

    private Function<IConfiguration, Boolean> isSdcAddressNotSet() {
        return it -> it.getSdcAddress() == null || it.getSdcAddress().isEmpty();
    }

    static boolean isValidFqdn(String fqdn) {
            Matcher matcher = DistributionClientConstants.FQDN_PATTERN.matcher(fqdn);
            return matcher.matches();
    }

    static boolean isValidFqdns(List<String> fqdns) {
        return fqdns.stream().allMatch(ConfigurationValidator::isValidFqdn);
    }
}
