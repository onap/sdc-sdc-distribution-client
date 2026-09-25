/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2026 Deutsche Telekom. All rights reserved.
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

import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;

/**
 * Artifact download and status publishing for a distribution notification, without the lifecycle of
 * {@link IDistributionClient}.
 */
public interface IDistributionOperations {

    /**
     * Downloads an artifact from SDC Catalog <br>
     *
     * @param artifactInfo - the info about the Artifact to be downloaded
     * @return IDistributionClientDownloadResult
     */
    IDistributionClientDownloadResult download(IArtifactInfo artifactInfo);

    /**
     * Build and publish Distribution Download Status event to Distribution
     * Status Topic
     *
     * @param statusMessage - the status message to be published
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage);

    /**
     * Build and publish Distribution Download Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage - the status message to be published
     * @param errorReason - the error details
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDownloadStatus(IDistributionStatusMessage statusMessage, String errorReason);

    /**
     * Build and publish Distribution Deployment Status event to Distribution
     * Status Topic
     *
     * @param statusMessage - the status message to be published
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage);

    /**
     * Build and publish Distribution Deployment Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage - the status message to be published
     * @param errorReason - the error details
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendDeploymentStatus(IDistributionStatusMessage statusMessage, String errorReason);

    /**
     * Build and publish Distribution Component Status event to Distribution
     * Status Topic
     *
     * @param statusMessage - the status message to be published
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage);

    /**
     * Build and publish Distribution Component Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage - the status message to be published
     * @param errorReason - the error details
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendComponentDoneStatus(IComponentDoneStatusMessage statusMessage, String errorReason);

    /**
     * Build and publish Distribution Final Status event to Distribution
     * Status Topic
     *
     * @param statusMessage - the status message to be published
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage);

    /**
     * Build and publish Distribution Final Status event to Distribution
     * Status Topic With Error Reason.
     *
     * @param statusMessage - the status message to be published
     * @param errorReason - the error details
     * @return IDistributionClientResult
     */
    IDistributionClientResult sendFinalDistrStatus(IFinalDistrStatusMessage statusMessage, String errorReason);

    IDistributionClientResult sendNotificationStatus(StatusMessage statusMessage);
}
