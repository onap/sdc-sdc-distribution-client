/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.sdc.api.results.DistributionActionResultEnum;

class DistributionClientDownloadResultTest {

    public static DistributionClientDownloadResultImpl distributionClientDownloadResult;

    @BeforeAll
    public static void init() {
        distributionClientDownloadResult = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "");
    }

    @Test
    void testNonHeaderFilename() {
        distributionClientDownloadResult.setArtifactName("service-BkPerformanceSrvs-csar.csar");
        assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
    }

    @Test
    void testNullFilename() {
        distributionClientDownloadResult.setArtifactName(null);
        assertNull(distributionClientDownloadResult.getArtifactFilename());
    }

    @Test
    void testFilenameFromHeaderNoSpace() {
        distributionClientDownloadResult.setArtifactName("attachment;filename=\"service-BkPerformanceSrvs-csar.csar\"");
        assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
    }

    @Test
    void testFilenameFromHeaderOneSpace() {
        distributionClientDownloadResult.setArtifactName("attachment; filename=\"service-BkPerformanceSrvs-csar.csar\"");
        assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
    }

    @Test
    void testFilenameFromHeaderManySpaces() {
        distributionClientDownloadResult.setArtifactName("attachment;         filename=\"service-BkPerformanceSrvs-csar.csar\"");
        assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
    }

    @Test
    void testFilenameEmpty() {
        distributionClientDownloadResult.setArtifactName("attachment; filename=\"\"");
        assertEquals("", distributionClientDownloadResult.getArtifactFilename());
    }
}
