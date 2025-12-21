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

import java.util.ArrayList;
import java.util.List;

import org.onap.sdc.api.notification.IArtifactInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtifactInfo implements IArtifactInfo {

    private String artifactName;
    private String artifactType;
    private String artifactURL;
    private String artifactChecksum;
    private String artifactDescription;
    private Integer artifactTimeout;
    private String artifactVersion;
    private String artifactUUID;
    private String generatedFromUUID;
    private IArtifactInfo generatedArtifact;
    private List<String> relatedArtifacts;
    private List<IArtifactInfo> relatedArtifactsInfo;

    private ArtifactInfo(IArtifactInfo iArtifactInfo) {
        artifactName = iArtifactInfo.getArtifactName();
        artifactType = iArtifactInfo.getArtifactType();
        artifactURL = iArtifactInfo.getArtifactURL();
        artifactChecksum = iArtifactInfo.getArtifactChecksum();
        artifactDescription = iArtifactInfo.getArtifactDescription();
        artifactTimeout = iArtifactInfo.getArtifactTimeout();
        artifactVersion = iArtifactInfo.getArtifactVersion();
        artifactUUID = iArtifactInfo.getArtifactUUID();
        generatedArtifact = iArtifactInfo.getGeneratedArtifact();
        relatedArtifactsInfo = iArtifactInfo.getRelatedArtifacts();
        relatedArtifacts = fillRelatedArtifactsUUID(relatedArtifactsInfo);
    }


    private List<String> fillRelatedArtifactsUUID(List<IArtifactInfo> relatedArtifactsInfo) {
        List<String> relatedArtifactsUUID = null;
        if (relatedArtifactsInfo != null && !relatedArtifactsInfo.isEmpty()) {
            relatedArtifactsUUID = new ArrayList<>();
            for (IArtifactInfo curr : relatedArtifactsInfo) {
                relatedArtifactsUUID.add(curr.getArtifactUUID());
            }
        }
        return relatedArtifactsUUID;
    }

    public static List<ArtifactInfo> convertToArtifactInfoImpl(List<IArtifactInfo> list) {
        List<ArtifactInfo> ret = new ArrayList<>();
        if (list != null) {
            for (IArtifactInfo artifactInfo : list) {
                ret.add(new ArtifactInfo(artifactInfo));
            }
        }
        return ret;
    }

    public List<IArtifactInfo> getRelatedArtifacts() {
        List<IArtifactInfo> temp = new ArrayList<>();
        if (relatedArtifactsInfo != null) {
            temp.addAll(relatedArtifactsInfo);
        }
        return temp;
    }

    public List<String> getRelatedArtifactsUUID() {
        return relatedArtifacts;
    }

    @Override
    public String toString() {
        return "BaseArtifactInfoImpl [artifactName=" + artifactName
                + ", artifactType=" + artifactType + ", artifactURL="
                + artifactURL + ", artifactChecksum=" + artifactChecksum
                + ", artifactDescription=" + artifactDescription
                + ", artifactVersion=" + artifactVersion
                + ", artifactUUID=" + artifactUUID
                + ", artifactTimeout=" + artifactTimeout + "]";
    }
}
