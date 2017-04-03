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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.Property;
import org.openecomp.sdc.toscaparser.api.TopologyTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.yaml.snakeyaml.Yaml;

public class SdcCsarHelperImpl implements ISdcCsarHelper {

	private ToscaTemplate toscaTemplate;
	private static Yaml defaultYaml = new Yaml();


	public SdcCsarHelperImpl(ToscaTemplate toscaTemplate) {
		this.toscaTemplate = toscaTemplate;
	}

	@Override
	//Sunny flow  - covered with UT, flat and nested
	public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String leafValuePath) {
		String[] split = leafValuePath.split("#");
		List<Property> properties = nodeTemplate.getProperties();
		Optional<Property> findFirst = properties.stream().filter(x -> x.getName().equals(split[0])).findFirst();
		if (findFirst.isPresent()){
			Property property = findFirst.get();
			Object current = property.getValue();
			if (split.length > 1){
				current = defaultYaml.load((String)current); 
				for (int i = 1; i < split.length; i++) {
					current = ((Map<String, Object>)current).get(split[i]);
				}
			}
			return String.valueOf(current);
		}
		return null;
	}

	@Override
	public List<NodeTemplate> getServiceVlList() {
		return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VL);

	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getServiceVfList() {
		return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VF);
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType) {
		List<NodeTemplate> res = new ArrayList<>();
		List<NodeTemplate> nodeTemplates = toscaTemplate.getNodeTemplates();
		for (NodeTemplate nodeTemplate : nodeTemplates){
			if (nodeType.equals(nodeTemplate.getTypeDefinition().getType())){
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
		for (NodeTemplate nodeTemplate : nodeTemplates){
			if (customizationId.equals(nodeTemplate.getMetadata().get(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))){
				return nodeTemplate;
			}
		}
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

	@Override
	public String getServiceInputLeafValue(String inputLeafValuePath) {
		//toscaTemplate.getTopologyTemplate().getNodeTemplates().get(0).getProperties().get(0).
		return null;//getLeafPropertyValue(toscaTemplate, inputLeafValuePath);
	}

	@Override
	public String getServiceSubstitutionMappingsTypeName() {
		return toscaTemplate.getTopologyTemplate().getSubstitutionMappings().getNodeDefinition().getType();
	}

	@Override
	public Map<String, String> getServiceMetadata() {
		TopologyTemplate topologyTemplate = toscaTemplate.getTopologyTemplate();
		System.out.println(topologyTemplate.toString());
		return topologyTemplate.getMetadata();
	}

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

		List<NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
		List<NodeTemplate> res = new ArrayList<>();
		for (NodeTemplate nodeTemplateEntry : nodeTemplates){
			//TODO switch back to type condition
			if (nodeTemplateEntry.getTypeDefinition().getType().contains("."+sdcType.toLowerCase()+".")){
				//if (sdcType.equals(nodeTemplateEntry.getMetadata().get(SdcPropertyNames.PROPERTY_NAME_TYPE))){
				res.add(nodeTemplateEntry);
			}
		}
		return res;
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
	//TODO constant strings
	public List<NodeTemplate> getAllottedResources() {
		List<NodeTemplate> nodeTemplates = toscaTemplate.getTopologyTemplate().getNodeTemplates();
		return nodeTemplates.stream().filter(x -> x.getMetadata() != null && x.getMetadata().get("category").equals("allotted_resources")).collect(Collectors.toList());
	}

	@Override
	//Sunny flow - covered with UT
	public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate) {
		//Can be done
		return nodeTemplate.getTypeDefinition().getType();
	}

	/*//Not part of API, for inner/test use
	public NodeTemplate getNodeTemplateByName(TopologyTemplate topologyTemplate, String topologyName){
		List<NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
		Optional<NodeTemplate> findFirst = nodeTemplates.stream().filter(x -> x.getName().equals(topologyName)).findFirst();
		return findFirst.isPresent() ? findFirst.get() : null;
	}*/
}
