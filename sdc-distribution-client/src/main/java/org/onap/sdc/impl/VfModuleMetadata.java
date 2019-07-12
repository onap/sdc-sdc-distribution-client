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

import java.util.List;

import org.onap.sdc.api.notification.IVfModuleMetadata;

final class VfModuleMetadata implements IVfModuleMetadata {
    private VfModuleMetadata() {
        //This Class is only built by parsing Json
    }

    private String vfModuleModelName;
    private String vfModuleModelInvariantUUID;
    private String vfModuleModelVersion;
    private String vfModuleModelUUID;
    private String vfModuleModelDescription;
    private boolean isBase;
    private List<String> artifacts;

    public String getVfModuleModelName() {
        return vfModuleModelName;
    }

    public String getVfModuleModelInvariantUUID() {
        return vfModuleModelInvariantUUID;
    }

    public String getVfModuleModelVersion() {
        return vfModuleModelVersion;
    }

    public String getVfModuleModelUUID() {
        return vfModuleModelUUID;
    }

    public String getVfModuleModelDescription() {
        return vfModuleModelDescription;
    }

    public boolean isBase() {
        return isBase;
    }

    public List<String> getArtifacts() {
        return artifacts;
    }
}
