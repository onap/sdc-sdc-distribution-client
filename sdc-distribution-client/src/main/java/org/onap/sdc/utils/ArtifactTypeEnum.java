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

public enum ArtifactTypeEnum {
    HEAT,
    HEAT_VOL,
    HEAT_NET,
    MURANO_PKG,
    HEAT_ENV,
    YANG_XML,
    OTHER,
    VF_LICENSE,
    VENDOR_LICENSE,
    MODEL_INVENTORY_PROFILE,
    MODEL_QUERY_SPEC,
    APPC_CONFIG,
    VNF_CATALOG,
    HEAT_NESTED,
    HEAT_ARTIFACT,
    VF_MODULES_METADATA,
    ETSI_PACKAGE,
    YANG_MODULE,
    PM_DICTIONARY,
    VES_EVENTS,
    TOSCA_TEMPLATE,
    TOSCA_CSAR,
    //DCAE Artifacts
    DCAE_TOSCA, DCAE_JSON, DCAE_POLICY, DCAE_DOC,
    DCAE_EVENT, DCAE_INVENTORY_TOSCA, DCAE_INVENTORY_JSON,
    DCAE_INVENTORY_POLICY, DCAE_INVENTORY_DOC,
    DCAE_INVENTORY_BLUEPRINT, DCAE_INVENTORY_EVENT;

}
