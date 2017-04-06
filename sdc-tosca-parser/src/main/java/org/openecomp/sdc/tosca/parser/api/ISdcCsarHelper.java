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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.openecomp.sdc.tosca.parser.api;


import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.Metadata;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.parameters.Input;


public interface ISdcCsarHelper {

	/**
	 * Get all node templates by node_type for this CSAR service.
	 *  
	 * @param nodeType - the TOSCA type of the node.
	 * @return service node templates of this type.
	 */
	public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType);

	/**
	 * Get groups of a VF with type "org.openecomp.groups.VfModule".
	 * 
	 * @param vfCustomizationUuid - customizationUuid of VF instance.
	 * @return list of vfModule groups.
	 */
	public List<Group> getVfModulesByVf(String vfCustomizationUuid);


	/**
	 * Get any property leaf value for node template by full path separated by #.<br>
	 * For example, for node template with this property:<br><br>
	 *   network_assignments:<br>
          &nbsp;&nbsp;ecomp_generated_network_assignment: true<br>
          &nbsp;&nbsp;is_shared_network: false<br>
          &nbsp;&nbsp;is_external_network: false<br>
          &nbsp;&nbsp;ipv4_subnet_default_assignments:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;use_ipv4: true<br>
            &nbsp;&nbsp;&nbsp;&nbsp;ip_network_address_plan: 1.2.3.4<br>
            &nbsp;&nbsp;&nbsp;&nbsp;dhcp_enabled: true<br>
            &nbsp;&nbsp;&nbsp;&nbsp;ip_version: 4<br>
            &nbsp;&nbsp;&nbsp;&nbsp;cidr_mask: 24<br>
            &nbsp;&nbsp;&nbsp;&nbsp;min_subnets_count: 1<br>
          &nbsp;&nbsp;ipv6_subnet_default_assignments:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;use_ipv6: false<br><br>
            
	 * calling<br> 
	 * getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv6_subnet_default_assignments#use_ipv6")<br> 
	 * will return "false".
	 * @param nodeTemplate - nodeTemplate where the property should be looked up.
	 * @param pathToPropertyLeafValue - the full path of the required property.
	 * @return the leaf value as String, or null if there's no such property, or it's not a leaf.
	 */
	public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String pathToPropertyLeafValue);
	
	/**
	 * Get any property leaf value for a group definition by full path separated by #.
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for a group.
	 * @param group - group where the property should be looked up.
	 * @param propertyName - the name of the required property.
	 * @return the leaf value as String, or null if there's no such property, or it's not a leaf.
	 */
	public String getGroupPropertyLeafValue(Group group, String propertyName);


	/**
	 * Get all VL node templates of the CSAR service.
	 * @return - all VL node templates.
	 */
	public List<NodeTemplate> getServiceVlList();

	/**
	 * Get all VF node templates of the CSAR service.
	 * @return - all VF node templates.
	 */
	public List<NodeTemplate> getServiceVfList();


	/**
	 * 
	 * Get a property from a metadata object.<br>
	 * This is just sugaring method, same as calling metadata.getMetadataPropertyValue(metadataPropertyName).<br>
	 * 
	 * For metadata object representing the below: <br><br>
	 * 
	 *  metadata:<br>
        &nbsp;&nbsp;invariantUUID: 4598a404-00e1-42a6-8767-0bda343e2066<br>
        &nbsp;&nbsp;UUID: e17940d6-42f8-4989-bad0-31de5addc619<br>
        &nbsp;&nbsp;customizationUUID: 83d086b2-a861-4d3b-aa84-3bfbb9b2ec20<br>
        &nbsp;&nbsp;version: '0.1'<br>
        &nbsp;&nbsp;name: vIPR_ATM<br>
        &nbsp;&nbsp;description: vIPR_ATM<br>
        &nbsp;&nbsp;type: VF<br>
        &nbsp;&nbsp;category: category1<br>
        &nbsp;&nbsp;subcategory: subCategory1<br><br>
        
        calling<br> 
        getMetadataPropertyValue(metadata,"invariantUUID")<br>
        will return "4598a404-00e1-42a6-8767-0bda343e2066".
        
	 * @param metadata - metadata object.
	 * @param metadataPropertyName - the name of the metadata property.
	 * @return metadata property value
	 */
	public String getMetadataPropertyValue(Metadata metadata, String metadataPropertyName);
	
	
	/**
	 * Get input leaf value for the CSAR service, by full path separated by #.<br>
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for an input full path.
	 * The expected format is "input_name#default[optionally #rest_of_path]"
	 * @param inputLeafValuePath by full path separated by #.
	 * @return input leaf value for the service.
	 */
	public String getServiceInputLeafValueOfDefault(String inputLeafValuePath);
	
	/**
	 * Get the type name of the CSAR service's substitution mappings element.<br> 
	 * 
	 * For the below:<br><br>
	 * 
	 * substitution_mappings:<br>
       &nbsp;&nbsp;type: org.openecomp.services.ViprATM<br>

    	calling<br> 
    	getServiceSubstitutionMappingsTypeName()<br>
    	 will return "org.openecomp.services.ViprATM"
	 * @return - the type name of the CSAR service's substitution mappings element
	 */
	public String getServiceSubstitutionMappingsTypeName();
	
	/**
	 * Get the CSAR service metadata
	 * @return - the service metadata object.
	 */
	public Metadata getServiceMetadata();
	
	/**
	 * Get all VFC node templates from a specified VF.
	 * @param vfCustomizationId - customizationUuid of the VF node template.
	 * @return all VFC node templates from a specified VF
	 */
	public List<NodeTemplate> getVfcListByVf(String vfCustomizationId);
	
	/**
	 * Get all CP node templates from a specified VF.
	 * @param vfCustomizationId - customizationUuid of the VF node template.
	 * @return all CP node templates from a specified VF
	 */
	public List<NodeTemplate> getCpListByVf(String vfCustomizationId);
	
	/**
	 * Get all members of this group definition.<br>
	 * 
	 * For example, for this group definition:<br><br>
	 * 
	 *   ViprAtm..vIPR-ATM-Base..module-0:<br>   
      &nbsp;&nbsp;type: org.openecomp.groups.VfModule<br>      
      &nbsp;&nbsp;.................<br>
      &nbsp;&nbsp;members: [vIPR_ATM_Ha_Two, vIPR_ATM_Ha_One, vIPR_ATM_OAM_SG, vIPR_ATM_HA_TWO_SG, vIPR_ATM_HA_ONE_SG]<br><br>
      
      calling<br> 
      getMembersOfVfModule(NoteTemplate vfNodeTemplate, Group group)<br>
      will return List of the following Node templates in the vfNodeTemplate: "vIPR_ATM_Ha_Two, vIPR_ATM_Ha_One, vIPR_ATM_OAM_SG, vIPR_ATM_HA_TWO_SG, vIPR_ATM_HA_ONE_SG"<br>
	 * @param vf - VF to return the node templates from.
	 * @param vfModule - group to return the members from.
	 * @return node templates from vf with the names as in members section.
     * 
	 */
	public List<NodeTemplate> getMembersOfVfModule(NodeTemplate vf, Group vfModule);
	
	
	/**
	 * Get list of node template pairs, where for each pair,<br> 
	 * the left node template in pair has requirement with name reqName, <br>
	 * which should be satisfied with respective capability by the right node template in pair.<br>
	 * 
	 * For example, if we have the below two node templates in the vIPR VF:<br><br>
	 * 
	 * oam_extCP:<br>
      &nbsp;&nbsp;type: org.openecomp.resources.cp.extCP<br> 
      &nbsp;&nbsp;requirements:<br>
        &nbsp;&nbsp;&nbsp;&nbsp;- virtualBinding: vipr_atm_firewall<br><br>
	 * 
	 * vipr_atm_firewall: <br>
      &nbsp;&nbsp;type: org.openecomp.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm<br>
      ........<br><br>
	 * 
        
     * calling<br>
     * getNodeTemplatePairsByReqName(getCpListByVf(viprCustomUuid), getVfcListByVf(viprCustomUuid), "virtualBinding")<br>
     * will return a list with one Pair - where left element of pair will be "oam_extCP" node template,<br>
     * and right element will be "vipr_atm_firewall" node template.<br>
     * 
	 * @param listOfReqNodeTemplates - list of node templates in which the "reqName" requirement should be looked.
	 * @param listOfCapNodeTemplates - list of node templates in which the capability matching the "reqName" requirement should be looked.
	 * @param reqName - the name of a requirement definition to match by.
	 * @return
	 */
	public List<Pair<NodeTemplate,NodeTemplate>> getNodeTemplatePairsByReqName(List<NodeTemplate> listOfReqNodeTemplates, List<NodeTemplate> listOfCapNodeTemplates, String reqName);
	
	/**
	 * Get all allotted node templates from this service.
	 * @return all allotted node templates from this service.
	 */
	public List<NodeTemplate> getAllottedResources();
	
	/**
	 * Get node_type of a node template.<br>
	 * 
	 * For this node template:<br>
	 * 
	 * vipr_atm_firewall: <br>
      &nbsp;&nbsp;type: org.openecomp.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm<br>
      ........<br><br>
     * 
     * the function will return "org.openecomp.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm"
     *  
	 * @param nodeTemplate - node template object
	 * @return - node type string.
	 */
	public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate);
	
	/**
	 * Get the CSAR service inputs list.
	 * @return - the service inputs list.
	 */
	public List<Input> getServiceInputs();
	
}
