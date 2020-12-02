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

package org.onap.sdc.api;

import java.util.List;

import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.IVfModuleMetadata;

public interface IDistributionClient {

    /**
     * Update the configuration of the distribution client <br>
     * Updatable configuration parameters are: pollingInterval, pollingTimeout,
     * consumerGroup and relevantArtifactTypes
     *
     * @param newConf - contains updated configuration
     * @return IDistributionClientResult
     */
    IDistributionClientResult updateConfiguration(IConfiguration newConf);

    /**
     * Retrieve the configuration of the distribution client <br>
     *
     * @return IConfiguration
     */
    IConfiguration getConfiguration();

    /**
     * Start distribution client <br>
     * - start polling notification topic <br>
     *
     * @return IDistributionClientResult
     */
    IDistributionClientResult start();

    /**
     * Stop distribution client <br>
     * - stop polling notification topic <br>
     * - unregister topics (via ASDC) <br>
     * - delete keys from UEB
     *
     * @return IDistributionClientResult
     */
    IDistributionClientResult stop();

    /**
     * Downloads an artifact from ASDC Catalog <br>
     *
     * @param artifactInfo
     * @return IDistributionClientDownloadResult
     */
    IDistributionClientDownloadResult download(IArtifactInfo artifactInfo);

    /**
     * Initialize the distribution client <br>
     * - fetch the UEB server list from ASDC <br>
     * - create keys in UEB <br>
     * - register for topics (via ASDC) <br>
     * - set the notification callback <br>
     * <p>
     * Note: all configuration fields are mandatory. <br>
     * Password must be in clear text and not encrypted <br>
     * ONAP-Component MUST store password as SHA-2 (256) hashed with
     * dynamically generated salt value <br>
     *
     * @param conf
     * @param callback
     * @return IDistributionClientResult
     */
    IDistributionClientResult init(IConfiguration conf, INotificationCallback callback);

    /**
     * Initialize the distribution client <br>
     * - fetch the UEB server list from ASDC <br>
     * - create keys in UEB <br>
     * - register for topics (via ASDC) <br>
     * - set the notification callback <br>
     * <p>
     * Note: all configuration fields are mandatory. <br>
     * Password must be in clear text and not encrypted <br>
     * ONAP-Component MUST store password as SHA-2 (256) hashed with
     * dynamically generated salt value <br>
     *
     * @param conf
     * @param notificationCallback
     * @param statusCallback
     * @return IDistributionClientResult
     */
    IDistributionClientResult init(IConfiguration conf, INotificationCallback notificationCallback,
                                   IStatusCallback statusCallback);

    /**
     * Build and publish Distribution Download Status event to Distribution
     * Status Topic
     *
     * @param statusMessage
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage);

    /**
     * Build and publish Distribution Download Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage
     * @param errorReason
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage, String errorReason);

    /**
     * Build and publish Distribution Deployment Status event to Distribution
     * Status Topic
     *
     * @param statusMessage
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage);

    /**
     * Build and publish Distribution Deployment Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage
     * @param errorReason
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage, String errorReason);

    /**
     * Build and publish Distribution Component Status event to Distribution
     * Status Topic
     *
     * @param statusMessage
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage);

    /**
     * Build and publish Distribution Component Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage
     * @param errorReason
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage, String errorReason);


    /**
     * Build and publish Distribution Final Status event to Distribution
     * Status Topic
     *
     * @param statusMessage
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage);


    /**
     * Build and publish Distribution Final Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage
     * @param errorReason
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage, String errorReason);


    /**
     * This method parses artifact of type VF_MODULES_METADATA payload data
     * .<br>
     * @deprecated Method is deprecated due to VF Module changes. Only backward
     * compatibility is supported.<br>
     *
     * @param artifactPayload
     * @return IVfModuleMetadata list
     */
    @Deprecated
    List<IVfModuleMetadata> decodeVfModuleArtifact(byte[] artifactPayload);


}
