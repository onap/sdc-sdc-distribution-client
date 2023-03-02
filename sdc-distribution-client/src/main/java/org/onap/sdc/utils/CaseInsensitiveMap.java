/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2023 Deutsche Telekom Intellectual Property. All rights reserved.
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

import java.util.HashMap;

/**
 * A custom map implementation that allows case insensitive key checks
 *
 * This is used for response/request headers that can be transformed to
 * lowercase by the ingress/envoy sidecar.
 * HTTP headers are case insensitive according to RFC 2616
 * 
 * @author mszwalkiewicz
 */
public class CaseInsensitiveMap<K, V> extends HashMap<K, V> {

    public boolean containsCaseInsensitiveKey(String key) {
        for (K existingKey : keySet()) {
            if (existingKey.toString().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public V getCaseInsensitiveKey(String key) {
        for (K existingKey : keySet()) {
            if (existingKey.toString().equalsIgnoreCase(key)) {
                return super.get(existingKey);
            }
        }
        return null;
    }
}
