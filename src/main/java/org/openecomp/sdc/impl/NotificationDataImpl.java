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

package org.openecomp.sdc.impl;

import java.util.ArrayList;
import java.util.List;

import org.openecomp.sdc.api.notification.IArtifactInfo;
import org.openecomp.sdc.api.notification.INotificationData;
import org.openecomp.sdc.api.notification.IResourceInstance;


class NotificationDataImpl implements INotificationData {

	private String distributionID;
	private String serviceName;
	private String serviceVersion;
	private String serviceUUID;
	private String serviceDescription;
	private String serviceInvariantUUID;
	private List<JsonContainerResourceInstance> resources;
	private List<ArtifactInfoImpl> serviceArtifacts;

	@Override
	public String getDistributionID() {
		return distributionID;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String getServiceVersion() {
		return serviceVersion;
	}

	@Override
	public String getServiceUUID() {
		return serviceUUID;
	}

	public void setDistributionID(String distributionID) {
		this.distributionID = distributionID;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setServiceUUID(String serviceUUID) {
		this.serviceUUID = serviceUUID;
	}

	

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@Override
	public String toString() {
		return "NotificationDataImpl [distributionID=" + distributionID
				+ ", serviceName=" + serviceName + ", serviceVersion="
				+ serviceVersion + ", serviceUUID=" + serviceUUID+"]";
	}

	@Override
	public List<IResourceInstance> getResources() {
		List<IResourceInstance> ret = new ArrayList<IResourceInstance>();
		if( resources != null ){
			ret.addAll(resources);
		}
		return ret;
	}

	public void setResources(List<IResourceInstance> resources){
		this.resources = JsonContainerResourceInstance.convertToJsonContainer(resources);
	}
	
	public List<JsonContainerResourceInstance> getResourcesImpl(){
		return resources;
	}
	
	List<ArtifactInfoImpl> getServiceArtifactsImpl(){
		return serviceArtifacts;
	}

	@Override
	public List<IArtifactInfo> getServiceArtifacts() {
		
		List<IArtifactInfo> temp = new ArrayList<IArtifactInfo>();
		if( serviceArtifacts != null ){
			temp.addAll(serviceArtifacts);
		}
		return temp;
	}

	void setServiceArtifacts(List<ArtifactInfoImpl> relevantServiceArtifacts) {
		serviceArtifacts = relevantServiceArtifacts;
		
	}
	
	@Override
	public String getServiceInvariantUUID() {
		return serviceInvariantUUID;
	}
	
	
	public void setServiceInvariantUUID(String serviceInvariantUUID) {
		this.serviceInvariantUUID = serviceInvariantUUID;
	}
	@Override
	public IArtifactInfo getArtifactMetadataByUUID(String artifactUUID){
		IArtifactInfo ret = findArtifactInfoByUUID(artifactUUID, serviceArtifacts);
		if( ret == null && resources != null ){
			for( JsonContainerResourceInstance currResourceInstance : resources ){
				ret = findArtifactInfoByUUID(artifactUUID, currResourceInstance.getArtifactsImpl());
				if( ret != null ){
					break;
				}
			}
		}
		return ret;

	}

	private IArtifactInfo findArtifactInfoByUUID(String artifactUUID, List<ArtifactInfoImpl> listToCheck) {
		IArtifactInfo ret = null;
		if( listToCheck != null ){
			for(IArtifactInfo curr: listToCheck ){
				if(curr.getArtifactUUID().equals(artifactUUID) ){
					ret = curr;
					break;
				}
			}
		}
		return ret;
	}
}
