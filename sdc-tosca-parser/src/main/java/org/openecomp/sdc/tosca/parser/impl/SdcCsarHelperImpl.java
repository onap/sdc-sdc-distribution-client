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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.utils.GeneralUtility;
import org.openecomp.sdc.tosca.parser.utils.SdcToscaUtility;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.Metadata;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.Property;
import org.openecomp.sdc.toscaparser.api.SubstitutionMappings;
import org.openecomp.sdc.toscaparser.api.TopologyTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.toscaparser.api.elements.NodeType;
import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class SdcCsarHelperImpl implements ISdcCsarHelper {


	private ToscaTemplate toscaTemplate;
	private static Logger log = LoggerFactory.getLogger(SdcCsarHelperImpl.class.getName());

	public SdcCsarHelperImpl(ToscaTemplate toscaTemplate) {
		this.toscaTemplate = toscaTemplate;
	}

	@Override
	//Sunny flow  - covered with UT, flat and nested
	public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String leafValuePath) {
		if (nodeTemplate == null)  {
			log.error("getNodeTemplatePropertyLeafValue - nodeTemplate is null");
			return null;
		}
		if (GeneralUtility.isEmptyString(leafValuePath))  {
			log.error("getNodeTemplatePropertyLeafValue - leafValuePath is null or empty");
			return null;
		}
		log.trace("getNodeTemplatePropertyLeafValue - nodeTemplate is : {}, leafValuePath is {} ", nodeTemplate, leafValuePath);
		String[] split = leafValuePath.split("#");
		List<Property> properties = nodeTemplate.getProperties();
		log.trace("getNodeTemplatePropertyLeafValue - properties of nodeTemplate are : {}", properties);
		return processProperties(split, properties);
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getServiceVlList() {
		List<NodeTemplate> serviceVlList = getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VL);
		log.trace("getServiceVlList - the VL list is {}", serviceVlList);
		return serviceVlList;
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getServiceVfList() {
		List<NodeTemplate> serviceVfList = getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), Types.TYPE_VF);
		log.trace("getServiceVfList - the VF list is {}", serviceVfList);
		return serviceVfList;
	}

	@Override
	//Sunny flow - covered with UT
	public String getMetadataPropertyValue(Metadata metadata, String metadataPropertyName) {
		if (GeneralUtility.isEmptyString(metadataPropertyName))  {
			log.error("getMetadataPropertyValue - the metadataPropertyName is null or empty");
			return null;
		}
		if (metadata == null)  {
			log.error("getMetadataPropertyValue - the metadata is null");
			return null;
		}
		String metadataPropertyValue = metadata.getValue(metadataPropertyName);
		log.trace("getMetadataPropertyValue - metadata is {} metadataPropertyName is {} the value is : {}", metadata, metadataPropertyName , metadataPropertyValue);
		return metadataPropertyValue;
	}


	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType) {
		if (GeneralUtility.isEmptyString(nodeType)) {
			log.error("getServiceNodeTemplatesByType - nodeType - is null or empty");
			return new ArrayList<>();
		}

		List<NodeTemplate> res = new ArrayList<>();
		List<NodeTemplate> nodeTemplates = toscaTemplate.getNodeTemplates();
		for (NodeTemplate nodeTemplate : nodeTemplates){
			if (nodeType.equals(nodeTemplate.getTypeDefinition().getType())){
				res.add(nodeTemplate);
			}
		}

		log.trace("getServiceNodeTemplatesByType - NodeTemplate list value is: {}", res);
		return res;
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getVfcListByVf(String vfCustomizationId) {
		if (GeneralUtility.isEmptyString(vfCustomizationId)) {
			log.error("getVfcListByVf - vfCustomizationId - is null or empty");
			return new ArrayList<>();
		}

		List<NodeTemplate> serviceVfList = getServiceVfList();
		NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
		log.trace("getVfcListByVf - serviceVfList value: {}, vfInstance value: {}", serviceVfList, vfInstance);
		return getNodeTemplateBySdcType(vfInstance, Types.TYPE_VFC);
	}

	@Override
	//Sunny flow - covered with UT
	public List<Group> getVfModulesByVf(String vfCustomizationUuid) {
		List<NodeTemplate> serviceVfList = getServiceVfList();
		log.trace("getVfModulesByVf - VF list is {}", serviceVfList);
		NodeTemplate nodeTemplateByCustomizationUuid = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationUuid);
		log.trace("getVfModulesByVf - getNodeTemplateByCustomizationUuid is {}, customizationUuid {}", nodeTemplateByCustomizationUuid, vfCustomizationUuid);
		if (nodeTemplateByCustomizationUuid != null){
			/*SubstitutionMappings substitutionMappings = nodeTemplateByCustomizationUuid.getSubstitutionMappings();
			if (substitutionMappings != null){
				List<Group> groups = substitutionMappings.getGroups();
				if (groups != null){
					List<Group> collect = groups.stream().filter(x -> "org.openecomp.groups.VfModule".equals(x.getTypeDefinition().getType())).collect(Collectors.toList());
					log.trace("getVfModulesByVf - VfModules are {}", collect);
					return collect;
				}
			}*/
			String name = nodeTemplateByCustomizationUuid.getName();
			String normaliseComponentInstanceName = SdcToscaUtility.normaliseComponentInstanceName(name);
			List<Group> serviceLevelGroups = toscaTemplate.getTopologyTemplate().getGroups();
			log.trace("getVfModulesByVf - VF node template name {}, normalized name {}. Searching groups on service level starting with VF normalized name...", name, normaliseComponentInstanceName);
			if (serviceLevelGroups != null){
				List<Group> collect = serviceLevelGroups
						.stream()
						.filter(x -> "org.openecomp.groups.VfModule".equals(x.getTypeDefinition().getType()) && x.getName().startsWith(normaliseComponentInstanceName))
						.collect(Collectors.toList());
				log.trace("getVfModulesByVf - VfModules are {}", collect);
				return collect;
			}
		}
		return new ArrayList<>();
	}

	@Override
	//Sunny flow - covered with UT
	public String getServiceInputLeafValueOfDefault(String inputLeafValuePath) {
		if (GeneralUtility.isEmptyString(inputLeafValuePath)) {
			log.error("getServiceInputLeafValueOfDefault - inputLeafValuePath is null or empty");
			return null;
		}

		String[] split = inputLeafValuePath.split("#");
		if (split.length < 2 || !split[1].equals("default")){
			log.error("getServiceInputLeafValue - inputLeafValuePath should be of format <input name>#default[optionally #<rest of path>] ");
			return null;
		}

		List<Input> inputs = toscaTemplate.getInputs();
		log.trace("getServiceInputLeafValue - the leafValuePath is  {} , the inputs are {}", inputLeafValuePath, inputs);
		if (inputs != null){
			Optional<Input> findFirst = inputs.stream().filter(x -> x.getName().equals(split[0])).findFirst();
			if (findFirst.isPresent()){
				log.trace("getServiceInputLeafValue - find first item is {}", findFirst.get());
				Input input = findFirst.get();
				Object current = input.getDefault();
				if (current == null){
					log.error("getServiceInputLeafValue - this input has no default");
					return null;
				}
				if (split.length > 2){
					current = new Yaml().load(current.toString()); 
					for (int i = 2; i < split.length; i++) {
						if (current instanceof Map){
							current = ((Map<String, Object>)current).get(split[i]);
						} else {
							log.error("getServiceInputLeafValue - found an unexpected leaf where expected to find a complex type");
							return null;
						}
					}
				}
				if (current != null){
					log.trace("getServiceInputLeafValue - the input default leaf value is {}", String.valueOf(current));
					return String.valueOf(current);
				}
			}
		}
		log.error("getServiceInputLeafValue - value not found");
		return null;
	}


	@Override
	//Sunny flow - covered with UT
	public String getServiceSubstitutionMappingsTypeName() {
		SubstitutionMappings substitutionMappings = toscaTemplate.getTopologyTemplate().getSubstitutionMappings();
		if (substitutionMappings == null) {
			log.trace("getServiceSubstitutionMappingsTypeName - No Substitution Mappings defined");
			return null;
		}
		log.trace("getServiceSubstitutionMappingsTypeName - SubstitutionMappings value: {}", substitutionMappings);

		NodeType nodeType = substitutionMappings.getNodeDefinition();
		if (nodeType == null) {
			log.trace("getServiceSubstitutionMappingsTypeName - No Substitution Mappings node defined");
			return null;
		}
		log.trace("getServiceSubstitutionMappingsTypeName - nodeType value: {}", nodeType);

		return nodeType.getType();
	}

	@Override
	//Sunny flow - covered with UT
	public Metadata getServiceMetadata() {
		return toscaTemplate.getMetadata();
	}

	@Override
	//Sunny flow - covered with UT
	public List<Input> getServiceInputs() {
		return toscaTemplate.getInputs();
	}

	@Override
	//Sunny flow - covered with UT
	public String getGroupPropertyLeafValue(Group group, String leafValuePath) {
		if (group == null) {
			log.error("getGroupPropertyLeafValue - group is null");
			return null;
		}

		if (GeneralUtility.isEmptyString(leafValuePath)) {
			log.error("getGroupPropertyLeafValue - leafValuePath is null or empty");
			return null;
		}

		String[] split = leafValuePath.split("#");
		List<Property> properties = group.getProperties();
		return processProperties(split, properties);
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getCpListByVf(String vfCustomizationId) {
		List<NodeTemplate> cpList = new ArrayList<>();
		if (GeneralUtility.isEmptyString(vfCustomizationId)){
			log.error("getCpListByVf vfCustomizationId string is empty");
			return cpList;
		}

		List<NodeTemplate> serviceVfList = getServiceVfList();
		if (serviceVfList == null || serviceVfList.size() == 0){
			log.error("getCpListByVf Vfs not exist for vfCustomizationId {}",vfCustomizationId);
			return cpList;
		}
		NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
		log.debug("getCpListByVf vf list is {}", vfInstance);
		if (vfInstance == null) {
			log.debug("getCpListByVf vf list is null");
			return cpList;
		}
		cpList = getNodeTemplateBySdcType(vfInstance, Types.TYPE_CP);
		if(cpList == null || cpList.size()==0)
			log.trace("getCpListByVf cps not exist for vfCustomizationId {}",vfCustomizationId);
		return cpList;
	}

	@Override
	//Sunny flow - covered with UT
	public List<NodeTemplate> getMembersOfVfModule(NodeTemplate vf, Group serviceLevelVfModule) {
		if (vf == null) {
			log.error("getMembersOfVfModule - vf is null");
			return new ArrayList<>();
		}

		if (serviceLevelVfModule == null || serviceLevelVfModule.getMetadata() == null || serviceLevelVfModule.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID) == null) {
			log.error("getMembersOfVfModule - vfModule or its metadata is null. Cannot match a VF group based on invariantUuid from missing metadata.");
			return new ArrayList<>();
		}
		
		
		SubstitutionMappings substitutionMappings = vf.getSubstitutionMappings();
		if (substitutionMappings != null){
			List<Group> groups = substitutionMappings.getGroups();
			if (groups != null){
				Optional<Group> findFirst = groups
				.stream()
				.filter(x -> (x.getMetadata() != null && serviceLevelVfModule.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID).equals(x.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID)))).findFirst();
				if (findFirst.isPresent()){
					log.trace("getMembersOfVfModule - Found VF level group with vfModuleModelInvariantUUID {}", serviceLevelVfModule.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID));
					List<String> members = findFirst.get().getMembers();
					log.trace("getMembersOfVfModule - members section is {}", members);
					if (members != null){
						List<NodeTemplate> collect = substitutionMappings.getNodeTemplates().stream().filter(x -> members.contains(x.getName())).collect(Collectors.toList());
						log.trace("getMembersOfVfModule - Node templates are {}", collect);
						return collect;
					}
				}
			}
		}
		return new ArrayList<>();
	}

	@Override
	//Sunny flow - covered with UT
	public List<Pair<NodeTemplate, NodeTemplate>> getNodeTemplatePairsByReqName(
			List<NodeTemplate> listOfReqNodeTemplates, List<NodeTemplate> listOfCapNodeTemplates, String reqName) {
		if (listOfReqNodeTemplates == null || listOfCapNodeTemplates == null || reqName == null){
			//TODO error message
			return new ArrayList<>();
		}

		List<Pair<NodeTemplate, NodeTemplate>> pairsList = new ArrayList<>();

		if (listOfReqNodeTemplates != null){
			for (NodeTemplate reqNodeTemplate : listOfReqNodeTemplates) {
				List<Map<String, Map<String, Object>>> requirements = reqNodeTemplate.getRequirements();
				if (requirements != null){
					for (Map<String, Map<String, Object>> reqEntry : requirements){
						Map<String, Object> reqEntryMap = reqEntry.get(reqName);

						if (reqEntryMap != null){
							Object node = reqEntryMap.get("node");
							if (node != null){
								String nodeString = (String)node;
								Optional<NodeTemplate> findFirst = listOfCapNodeTemplates.stream().filter(x -> x.getName().equals(nodeString)).findFirst();
								if (findFirst.isPresent()){
									pairsList.add(new ImmutablePair<NodeTemplate, NodeTemplate>(reqNodeTemplate, findFirst.get()));
								}
							}
						}
					}
				}
			}
		}
		return pairsList;
	}

	@Override
	//Sunny flow - covered with UT
	//TODO constant strings
	public List<NodeTemplate> getAllottedResources() {
		List<NodeTemplate> nodeTemplates = null;
		nodeTemplates = toscaTemplate.getTopologyTemplate().getNodeTemplates();
		if (nodeTemplates.isEmpty()) {
			log.error("getAllottedResources nodeTemplates not exist");
		}
		nodeTemplates = nodeTemplates.stream().filter(
				x -> x.getMetadata() != null && x.getMetadata().getValue("category").equals("Allotted Resources"))
				.collect(Collectors.toList());
		if (nodeTemplates.isEmpty()) {
			log.trace("getAllottedResources -  allotted resources not exist");
		} else {
			log.trace("getAllottedResources - the allotted resources list is {}", nodeTemplates);
		}

		return nodeTemplates;
	}
	@Override
	//Sunny flow - covered with UT
	public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate) {
		if(nodeTemplate == null){

			log.error("getTypeOfNodeTemplate nodeTemplate is null");
			return null;
		}
		log.debug("getTypeOfNodeTemplate node template type is {}",nodeTemplate.getTypeDefinition().getType());
		return nodeTemplate.getTypeDefinition().getType();
	}

	/************************************* helper functions ***********************************/
	private List<NodeTemplate> getNodeTemplateBySdcType(NodeTemplate nodeTemplate, String sdcType){
		if (nodeTemplate == null)  {
			log.error("getNodeTemplateBySdcType - nodeTemplate is null or empty");
			return new ArrayList<>();
		}

		if (GeneralUtility.isEmptyString(sdcType))  {
			log.error("getNodeTemplateBySdcType - sdcType is null or empty");
			return new ArrayList<>();
		}

		SubstitutionMappings substitutionMappings = nodeTemplate.getSubstitutionMappings();

		if (substitutionMappings != null) {
			List<NodeTemplate> nodeTemplates = substitutionMappings.getNodeTemplates();
			if (nodeTemplates != null && nodeTemplates.size() > 0)
				return nodeTemplates.stream().filter(x -> (x.getMetadata() != null && sdcType.equals(x.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)))).collect(Collectors.toList());
			else
				log.trace("getNodeTemplateBySdcType - SubstitutionMappings' node Templates not exist");
		} else
			log.trace("getNodeTemplateBySdcType - SubstitutionMappings not exist");

		return new ArrayList<>();
	}

	private List<NodeTemplate> getNodeTemplateBySdcType(TopologyTemplate topologyTemplate, String sdcType){
		if (GeneralUtility.isEmptyString(sdcType))  {
			log.error("getNodeTemplateBySdcType - sdcType is null or empty");
			return new ArrayList<>();
		}

		if (topologyTemplate == null) {
			log.error("getNodeTemplateBySdcType - topologyTemplate is null");
			return new ArrayList<>();
		}

		List<NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();

		if (nodeTemplates != null && nodeTemplates.size() > 0)
			return nodeTemplates.stream().filter(x -> (x.getMetadata() != null && sdcType.equals(x.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)))).collect(Collectors.toList());

		log.trace("getNodeTemplateBySdcType - topologyTemplate's nodeTemplates not exist");
		return new ArrayList<>();
	}

	//Assumed to be unique property for the list
	private NodeTemplate getNodeTemplateByCustomizationUuid(List<NodeTemplate> nodeTemplates, String customizationId){
		log.trace("getNodeTemplateByCustomizationUuid - nodeTemplates {}, customizationId {}", nodeTemplates, customizationId);
		Optional<NodeTemplate> findFirst = nodeTemplates.stream().filter(x -> (x.getMetadata() != null && customizationId.equals(x.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)))).findFirst();
		return findFirst.isPresent() ? findFirst.get() : null;
	}

	private String processProperties(String[] split, List<Property> properties) {
		log.trace("processProperties - the leafValuePath is  {} , the properties are {}", Arrays.toString(split), properties.toString());
		Optional<Property> findFirst = properties.stream().filter(x -> x.getName().equals(split[0])).findFirst();
		if (findFirst.isPresent()){
			log.trace("processProperties - find first item is {}", findFirst.get());
			Property property = findFirst.get();
			Object current = property.getValue();
			if (current == null){
				log.error("processProperties - this property has no value");
				return null;
			}
			if (split.length > 1){
				current = new Yaml().load(current.toString()); 
				for (int i = 1; i < split.length; i++) {
					if (current instanceof Map){
						current = ((Map<String, Object>)current).get(split[i]);
					} else {
						log.error("processProperties - found an unexpected leaf where expected to find a complex type");
						return null;
					}
				}
			}
			if (current != null){
				log.trace("processProperties - the property value is {}", String.valueOf(current));
				return String.valueOf(current);
			}
		}
		log.error("processProperties - Dont find property");
		return null;
	}	
}
