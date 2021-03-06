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

package org.onap.sdc.api.results;

/**
 * Distribution Client Result For Download API
 */
public interface IDistributionClientDownloadResult extends IDistributionClientResult {


    byte[] getArtifactPayload();


    /**
     * @return
     * @deprecated This method is deprecated and will be removed in 1710.
     * It returns <b>attachment; filename="filename"</b> rather than <b>"filename"</b>.
     * Please use {@link #getArtifactFilename()}.
     */
    @Deprecated
    String getArtifactName();

    /**
     * Returns the filename of the artifact.
     *
     * @return the filename of the artifact.
     */
    String getArtifactFilename();
}
