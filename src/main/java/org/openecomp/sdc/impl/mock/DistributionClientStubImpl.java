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

package org.openecomp.sdc.impl.mock;

import java.util.List;

import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.consumer.IConfiguration;
import org.openecomp.sdc.api.consumer.IDistributionStatusMessage;
import org.openecomp.sdc.api.consumer.INotificationCallback;
import org.openecomp.sdc.api.notification.IArtifactInfo;
import org.openecomp.sdc.api.notification.IVfModuleMetadata;
import org.openecomp.sdc.api.results.IDistributionClientDownloadResult;
import org.openecomp.sdc.api.results.IDistributionClientResult;
/** Mock Implementation */
public class DistributionClientStubImpl implements IDistributionClient{
	public DistributionClientStubImpl(){
		
	}
	
	public IDistributionClientResult updateConfiguration(IConfiguration newConf) {
		return new DistributionClientResultStubImpl();
	}

	public IDistributionClientResult start() {
		return new DistributionClientResultStubImpl();
	}

	public IDistributionClientResult stop() {
		return new DistributionClientResultStubImpl();
	}

	public IDistributionClientResult sendDownloadStatus( IDistributionStatusMessage statusMessage) {
		return new DistributionClientResultStubImpl();
	}

	public IDistributionClientResult sendDeploymentStatus( IDistributionStatusMessage statusMessage) {
		return new DistributionClientResultStubImpl();
	}

	@Override
	public IDistributionClientDownloadResult download(IArtifactInfo artifactInfo) {
		return new DistributionClientDownloadResultStubImpl();
	}

	@Override
	public IDistributionClientResult init(IConfiguration conf, INotificationCallback callback) {
		return new DistributionClientResultStubImpl();
	}

	@Override
	public IConfiguration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDistributionClientResult sendDownloadStatus(
			IDistributionStatusMessage statusMessage, String errorReason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDistributionClientResult sendDeploymentStatus(
			IDistributionStatusMessage statusMessage, String errorReason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IVfModuleMetadata> decodeVfModuleArtifact(byte[] artifactPayload) {
		// TODO Auto-generated method stub
		return null;
	}
	


}
