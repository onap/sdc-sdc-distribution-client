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
import org.openecomp.sdc.api.notification.IResourceInstance;

class JsonContainerResourceInstance implements IResourceInstance{
	JsonContainerResourceInstance (){}
	private String resourceInstanceName;
	private String resourceCustomizationUUID;
	private String resourceName;
	private String resourceVersion;
	private String resoucreType;
	private String resourceUUID;
	private String resourceInvariantUUID;
	private String category;
	private String subcategory;
	private List<ArtifactInfoImpl> artifacts;
	
	private JsonContainerResourceInstance(IResourceInstance resourceInstance){
		resourceInstanceName = resourceInstance.getResourceInstanceName();
		resourceCustomizationUUID = resourceInstance.getResourceCustomizationUUID();
		resourceName = resourceInstance.getResourceName();
		resourceVersion = resourceInstance.getResourceVersion();
		resoucreType = resourceInstance.getResourceType();
		resourceUUID = resourceInstance.getResourceUUID();
		resourceInvariantUUID = resourceInstance.getResourceInvariantUUID();
		category = resourceInstance.getCategory();
		subcategory = resourceInstance.getSubcategory();
		artifacts = ArtifactInfoImpl.convertToArtifactInfoImpl(resourceInstance.getArtifacts());
	}
	
	public static List<JsonContainerResourceInstance> convertToJsonContainer(List<IResourceInstance> resources){
		 List<JsonContainerResourceInstance> buildResources = new ArrayList<JsonContainerResourceInstance>();
		 if( resources != null ){
			 for( IResourceInstance resourceInstance : resources ){
				 buildResources.add(new JsonContainerResourceInstance(resourceInstance));
			 }
		 }
		 return buildResources;
	}
	
	@Override
	public String getResourceInstanceName() {
		return resourceInstanceName;
	}

	public void setResourceInstanceName(String resourceInstanceName) {
		this.resourceInstanceName = resourceInstanceName;
	}
	
	@Override
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public String getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	@Override
	public String getResourceType() {
		return resoucreType;
	}

	public void setResoucreType(String resoucreType) {
		this.resoucreType = resoucreType;
	}

	@Override
	public String getResourceUUID() {
		return resourceUUID;
	}

	public void setResourceUUID(String resourceUUID) {
		this.resourceUUID = resourceUUID;
	}

	@Override
	public List<IArtifactInfo> getArtifacts() {
		List<IArtifactInfo> temp = new ArrayList<IArtifactInfo>();
		if( artifacts != null ){
			temp.addAll(artifacts);
		}
		return temp;
	}

	public void setArtifacts(List<ArtifactInfoImpl> artifacts) {
		this.artifacts = artifacts;
	}
	
	public List<ArtifactInfoImpl> getArtifactsImpl(){
		return artifacts;
	}
	
	@Override
	public String getResourceInvariantUUID() {
		return resourceInvariantUUID;
	}
	
	public void setResourceInvariantUUID(String resourceInvariantUUID) {
		this.resourceInvariantUUID = resourceInvariantUUID;
	}
	public String getResourceCustomizationUUID() {
		return resourceCustomizationUUID;
	}

	public void setResourceCustomizationUUID(String resourceCustomizationUUID) {
		this.resourceCustomizationUUID = resourceCustomizationUUID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
}
