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

public class ArtifactInfoImpl implements IArtifactInfo {

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

    ArtifactInfoImpl() {
    }

    private ArtifactInfoImpl(IArtifactInfo iArtifactInfo) {
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

    public static List<ArtifactInfoImpl> convertToArtifactInfoImpl(List<IArtifactInfo> list) {
        List<ArtifactInfoImpl> ret = new ArrayList<>();
        if (list != null) {
            for (IArtifactInfo artifactInfo : list) {
                ret.add(new ArtifactInfoImpl(artifactInfo));
            }
        }
        return ret;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    public String getArtifactURL() {
        return artifactURL;
    }

    public void setArtifactURL(String artifactURL) {
        this.artifactURL = artifactURL;
    }

    public String getArtifactChecksum() {
        return artifactChecksum;
    }

    public void setArtifactChecksum(String artifactChecksum) {
        this.artifactChecksum = artifactChecksum;
    }

    public String getArtifactDescription() {
        return artifactDescription;
    }

    public void setArtifactDescription(String artifactDescription) {
        this.artifactDescription = artifactDescription;
    }

    public Integer getArtifactTimeout() {
        return artifactTimeout;
    }

    public void setArtifactTimeout(Integer artifactTimeout) {
        this.artifactTimeout = artifactTimeout;
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

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getArtifactUUID() {
        return artifactUUID;
    }

    public void setArtifactUUID(String artifactUUID) {
        this.artifactUUID = artifactUUID;
    }

    public String getGeneratedFromUUID() {
        return generatedFromUUID;
    }

    public void setGeneratedFromUUID(String generatedFromUUID) {
        this.generatedFromUUID = generatedFromUUID;
    }

    public IArtifactInfo getGeneratedArtifact() {
        return generatedArtifact;
    }

    public void setGeneratedArtifact(IArtifactInfo generatedArtifact) {
        this.generatedArtifact = generatedArtifact;
    }

    public List<IArtifactInfo> getRelatedArtifacts() {
        List<IArtifactInfo> temp = new ArrayList<>();
        if (relatedArtifactsInfo != null) {
            temp.addAll(relatedArtifactsInfo);
        }
        return temp;
    }

    public void setRelatedArtifacts(List<String> relatedArtifacts) {
        this.relatedArtifacts = relatedArtifacts;
    }

    public void setRelatedArtifactsInfo(List<IArtifactInfo> relatedArtifactsInfo) {
        this.relatedArtifactsInfo = relatedArtifactsInfo;
    }

    public List<String> getRelatedArtifactsUUID() {
        return relatedArtifacts;
    }

}
