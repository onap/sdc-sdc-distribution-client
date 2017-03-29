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

package org.openecomp.sdc.tosca.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.TopologyTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.tosca.parser.impl.Types;

public class SdcCsarHelperImpl implements ISdcCsarHelper {

	private ToscaTemplate toscaTemplate;
	
	public SdcCsarHelperImpl(ToscaTemplate toscaTemplate) {
		this.toscaTemplate = toscaTemplate;
	}

	@Override
	public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String leafValuePath) {
		//TODO
		return null;/*getLeafPropertyValue(nodeTemplate, leafValuePath);*/
	}

	@Override
	public List<NodeTemplate> getServiceVlList() {
		return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VL);

	}

	@Override
	public List<NodeTemplate> getServiceVfList() {
		return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VF);
	}
	
	@Override
	public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType) {
		List<NodeTemplate> res = new ArrayList<>();
		List<NodeTemplate> nodeTemplates = toscaTemplate.getNodeTemplates();
		for (NodeTemplate nodeTemplate : nodeTemplates){
			if (nodeTemplate.getTypeDefinition().getType().equals(nodeType)){
				res.add(nodeTemplate);
			}
		}
		return res;
	}

	@Override
	public List<NodeTemplate> getVfcListByVf(String vfCustomizationId) {
		List<NodeTemplate> serviceVfList = getServiceVfList();
		NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
		return getNodeTemplateBySdcType(vfInstance, Types.TYPE_VFC);
	}

	//Assumed to be unique property for the list
	private NodeTemplate getNodeTemplateByCustomizationUuid(List<NodeTemplate> nodeTemplates, String customizationId){
		//TODO Metadata is missing
		/*for (NodeTemplate nodeTemplate : nodeTemplates){
			if (nodeTemplate.getMetadata().getMetadataPropertyValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID).equals(customizationId)){
				return nodeTemplate;
			}
		}*/
		return null;
	}

	@Override
	public List<Group> getVfModulesByVf(String vfCustomizationUuid) {
		List<NodeTemplate> serviceVfList = getServiceVfList();
		List<Group> res = new ArrayList<>();
		NodeTemplate nodeTemplateByCustomizationUuid = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationUuid);
		//Asked Yaniv about getGroups from NodeTemplate
		/*
		List<Group> groups = nodeTemplateByCustomizationUuid.;
		for (Group group : groups){
			if (Types.TYPE_VFMODULE.equals(group.getType())){
				res.add(group);
			}
		}*/
		return res;
	}

	//Metadata question
	/*@Override
	public String getMetadataPropertyValue(Metadata metadata, String metadataPropertyName) {
		return metadata.getMetadataPropertyValue(metadataPropertyName);
	}*/

	@Override
	public String getServiceInputLeafValue(String inputLeafValuePath) {
		toscaTemplate.getTopologyTemplate().getNodeTemplates().get(0).getTypeDefinition().getType();
		return null;//getLeafPropertyValue(toscaTemplate, inputLeafValuePath);
	}

	@Override
	public String getServiceSubstitutionMappingsTypeName() {
		return toscaTemplate.getTopologyTemplate().getSubstitutionMappings().getNodeDefinition().getType();
	}

	//Metadata question
	/*@Override
	public Metadata getServiceMetadata() {
		return toscaTemplate.getMetadata();
	}*/

	//Get property from group
	@Override
	public String getGroupPropertyLeafValue(Group group, String propertyName) {
		return null;//getLeafPropertyValue(group, propertyName);
	}
	
	private List<NodeTemplate> getNodeTemplateBySdcType(NodeTemplate nodeTemplate, String sdcType){
		//Need metadata to fetch by type
		
		/*List<NodeTemplate> nodeTemplates = nodeTemplate.getNestedNodeTemplates();
		List<NodeTemplate> res = new ArrayList<>();
		for (NodeTemplate nodeTemplateEntry : nodeTemplates){
			if (nodeTemplateEntry.getMetadata().getMetadataPropertyValue(SdcPropertyNames.PROPERTY_NAME_TYPE).equals(sdcType)){
				res.add(nodeTemplateEntry);
			}
		}*/
		return null;
	}
	
	private List<NodeTemplate> getNodeTemplateBySdcType(TopologyTemplate topologyTemplate, String sdcType){
		//Need metadata to fetch by type
		
/*		List<NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
		List<NodeTemplate> res = new ArrayList<>();
		for (NodeTemplate nodeTemplateEntry : nodeTemplates){
			if (nodeTemplateEntry.getMetadata().getMetadataPropertyValue(SdcPropertyNames.PROPERTY_NAME_TYPE).equals(sdcType)){
				res.add(nodeTemplateEntry);
			}
		}
*/		return null;
	}

	@Override
	public List<NodeTemplate> getCpListByVf(String vfCustomizationId) {
		List<NodeTemplate> serviceVfList = getServiceVfList();
		NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
		return getNodeTemplateBySdcType(vfInstance, Types.TYPE_CP);
	}

	@Override
	public List<String> getMembersOfGroup(Group group) {
		//Can be done
		return null;//toscaTemplate.getTopologyTemplate().getSubstitutionMappings().getNodeTemplates().get(0).get
	}

	@Override
	public List<Pair<NodeTemplate, NodeTemplate>> getNodeTemplatePairsByReqName(
			List<NodeTemplate> listOfReqNodeTemplates, List<NodeTemplate> listOfCapNodeTemplates, String reqName) {
		//TODO - Can be done
		return new ArrayList<>();
	}

	@Override
	public List<NodeTemplate> getAllottedResources() {
		//TODO - Need metadata
		return new ArrayList<>();
	}

	@Override
	public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate) {
		//Can be done
		return nodeTemplate.getTypeDefinition().getType();
	}
	
	
}
