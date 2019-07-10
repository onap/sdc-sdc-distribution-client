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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.sdc.utils.DistributionActionResultEnum;


public class DistributionClientDownloadResultTest {
	
	public static DistributionClientDownloadResultImpl distributionClientDownloadResult;
	
	@BeforeClass
	public static void init(){
		distributionClientDownloadResult = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "");
	}
	
	@Test
	public void testNonHeaderFilename(){
		distributionClientDownloadResult.setArtifactName("service-BkPerformanceSrvs-csar.csar");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testNullFilename(){
		distributionClientDownloadResult.setArtifactName(null);
		assertNull(distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderNoSpace(){
		distributionClientDownloadResult.setArtifactName("attachment;filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderOneSpace(){
		distributionClientDownloadResult.setArtifactName("attachment; filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderManySpaces(){
		distributionClientDownloadResult.setArtifactName("attachment;         filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameEmpty(){
		distributionClientDownloadResult.setArtifactName("attachment; filename=\"\"");
		assertEquals("", distributionClientDownloadResult.getArtifactFilename());
	}
}
