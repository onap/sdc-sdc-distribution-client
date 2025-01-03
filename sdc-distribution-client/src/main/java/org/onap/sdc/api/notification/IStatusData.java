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

package org.onap.sdc.api.notification;

import org.onap.sdc.impl.StatusDataImpl;
import org.onap.sdc.utils.DistributionStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = StatusDataImpl.class)
public interface IStatusData {
    /**
     * Global Distribution Identifier: UUID generated by SDC per each
     * distribution activation.<br>
     * Generated UUID is compliant with RFC 4122.<br>
     * It is a 128-bit value formatted into blocks of hexadecimal digits
     * separated by a hyphen ("-").<br>
     * Ex.: AA97B177-9383-4934-8543-0F91A7A02836
     */
    String getDistributionID();

    /**
     * Unique ID of ONAP component instance (e.x INSTAR name).
     */
    String getConsumerID();

    /**
     * The predefined ONAP component name configured on the component.
     */
    String getComponentName();

    /**
     * Timestamp of the distribution status report creation. The number of
     * seconds that have elapsed since January 1, 1970
     */
    Long getTimestamp();

    /**
     * Resource URL of the downloaded/deployed artifact - URL specified in the
     * distribution notification message.
     */
    String getArtifactURL();

    /**
     * Status Event type
     */
    DistributionStatusEnum getStatus();

    /**
     * Error Reason describing the Status Event.
     */
    String getErrorReason();


}
