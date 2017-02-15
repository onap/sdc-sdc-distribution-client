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

package org.openecomp.sdc.utils;



import org.apache.commons.codec.binary.Base64;
import org.openecomp.sdc.impl.mock.DistributionClientDownloadResultStubImpl;
import org.openecomp.sdc.utils.GeneralUtils;



public class ArtifactsUtils {
	static DistributionClientDownloadResultStubImpl distributionClientDownloadResultStubImpl = new DistributionClientDownloadResultStubImpl();
	
	public static byte [] getArtifactPayload(){
		return distributionClientDownloadResultStubImpl.getArtifactPayload();
	}
	
	public static String getValidChecksum(){
		
		String payloadStr = new String(distributionClientDownloadResultStubImpl.getArtifactPayload());
				
		byte[] decodedPayload = Base64.decodeBase64(payloadStr);
		String checkSum = GeneralUtils.calculateMD5 (new String(decodedPayload));
		
		return checkSum;
	}

}
