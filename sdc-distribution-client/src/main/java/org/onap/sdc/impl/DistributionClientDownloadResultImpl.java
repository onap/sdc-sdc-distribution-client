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

import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.utils.DistributionActionResultEnum;

public class DistributionClientDownloadResultImpl extends DistributionClientResultImpl implements IDistributionClientDownloadResult {
    private byte[] artifactPayload;
    private String artifactName;


    public DistributionClientDownloadResultImpl(
            DistributionActionResultEnum responseStatus, String responseMessage) {
        super(responseStatus, responseMessage);

    }

    public DistributionClientDownloadResultImpl(
            DistributionActionResultEnum responseStatus,
            String responseMessage, String artifactName, byte[] artifactPayload) {
        super(responseStatus, responseMessage);
        this.artifactPayload = artifactPayload;
        this.artifactName = artifactName;
    }


    public void setArtifactPayload(byte[] payload) {
        this.artifactPayload = payload;
    }


    public byte[] getArtifactPayload() {

        return artifactPayload;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    @Override
    public String getArtifactFilename() {
        //Fix of bug 292443 in TDP
        if (artifactName == null || !artifactName.matches("attachment;\\s*filename=\".*?\"")) {
            return artifactName;
        }
        String fileName = "filename=\"";
        return artifactName.substring(artifactName.indexOf(fileName) + fileName.length(), artifactName.lastIndexOf("\""));
    }

}
