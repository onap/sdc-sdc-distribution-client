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

package org.onap.sdc.utils;

public enum DistributionStatusEnum {
    /**
     * Can be sent  when ONAP component  successfully  downloaded the specific artifact
     */
    DOWNLOAD_OK,

    /**
     * Can be sent when ONAP component failed to download  the specific artifact (corrupted file)
     */
    DOWNLOAD_ERROR,

    /**
     * Can be sent only  if  the  repeated  distribution notification  event is  sent when  the ONAP component  already  downloaded  the  artifact  , but  still  not  stored it in the  local  repository .
     */
    ALREADY_DOWNLOADED,

    /**
     * Can be sent  when ONAP component  successfully  deployed the specific artifact in the  local repository
     */
    DEPLOY_OK,

    /**
     * Can be sent when ONAP component failed  to  store  the downloaded  artifact  in the local  repository
     */
    DEPLOY_ERROR,

    /**
     * Sent  when  the  repeated  distribution notification  event is sent for already  stored  in the  local  repository  service artifact  ( artifact's version and  checksum match the one stored  in the local repository)
     */
    ALREADY_DEPLOYED,
    /**
     * ONAP component is requested to publish this status once component successfully complete downloading and storing all the data it needs from the service.
     */
    COMPONENT_DONE_OK,

    /**
     * ONAP component is requested to publish this status when component failed to download or failed to store one or more of the mandatory information it requires from the service model.
     * <p>
     * It is recommended to populate the errorReason field with appropriate description of the error
     */
    COMPONENT_DONE_ERROR,
    /**
     * The DISTRIBUTION_COMPLETE_OK/ERROR status indicating the overall ONAP components status of retrieving and storing the information.
     */
    DISTRIBUTION_COMPLETE_OK,

    DISTRIBUTION_COMPLETE_ERROR
}
