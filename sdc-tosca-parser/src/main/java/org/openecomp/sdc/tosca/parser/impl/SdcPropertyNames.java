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

public class SdcPropertyNames {
    public static String PROPERTY_NAME_INVARIANTUUID = "invariantUUID";
    public static String PROPERTY_NAME_UUID = "UUID";
    public static String PROPERTY_NAME_CUSTOMIZATIONUUID = "customizationUUID";
    public static String PROPERTY_NAME_VERSION = "version";
    
    //Groups metadata
    public static String PROPERTY_NAME_VFMODULEMODELINVARIANTUUID = "vfModuleModelInvariantUUID";
    public static String PROPERTY_NAME_VFMODULEMODELUUID = "vfModuleModelUUID";
    public static String PROPERTY_NAME_VFMODULECUSTOMIZATIONUUID = "vfModuleCustomizationUUID";
    public static String PROPERTY_NAME_VFMODULEMODELVERSION = "vfModuleModelVersion";
    public static String PROPERTY_NAME_VFMODULEMODELNAME = "vfModuleModelName";
    
    //Groups properties
    public static String PROPERTY_NAME_VFMODULETYPE = "vf_module_type";
    public static String PROPERTY_NAME_VFMODULELABEL = "vf_module_label";
    public static String PROPERTY_NAME_MINVFMODULEINSTANCES = "min_vf_module_instances";
    public static String PROPERTY_NAME_MAXVFMODULEINSTANCES = "max_vf_module_instances";
    public static String PROPERTY_NAME_INITIALCOUNT = "initial_count";

    
    public static String PROPERTY_NAME_DESCRIPTION = "description";
    public static String PROPERTY_NAME_TYPE = "type";
    public static String PROPERTY_NAME_CATEGORY = "category";
    public static String PROPERTY_NAME_SUBCATEGORY = "subcategory";
    public static String PROPERTY_NAME_RESOURCEVENDOR = "resourceVendor";
    public static String PROPERTY_NAME_RESOURCEVENDORRELEASE = "resourceVendorRelease";
    //VFC
    public static String PROPERTY_NAME_NFCCODE = "nfc_code";
    public static String PROPERTY_NAME_VMTYPETAG = "vm_type_tag";
    public static String PROPERTY_NAME_VMTYPE = "vm_type";
    public static String PROPERTY_NAME_VFCNAMING_ECOMPGENERATEDNAMING="vfc_naming#ecomp_generated_naming";
    public static String PROPERTY_NAME_VFCNAMING_NAMINGPOLICY="vfc_naming#naming_policy";
    //VF
    public static String PROPERTY_NAME_NFTYPE = "nf_type";
    public static String PROPERTY_NAME_NFROLE = "nf_role";
    public static String PROPERTY_NAME_NFFUNCTION = "nf_function";
    public static String PROPERTY_NAME_NFCODE = "nf_code";
    public static String PROPERTY_NAME_MININSTANCES = "min_instances";
    public static String PROPERTY_NAME_MAXINSTANCES = "max_instances";
    public static String PROPERTY_NAME_AVAILABILITYZONEMAXCOUNT = "availability_zone_max_count";
    public static String PROPERTY_NAME_AVAILABILITYZONECOUNT = "availability_zone_count";
    public static String PROPERTY_NAME_NAME = "name";
    public static String PROPERTY_NAME_VNFECOMPNAMING_ECOMPGENERATEDNAMING="vnf_ecomp_naming#ecomp_generated_naming";
    public static String PROPERTY_NAME_VNFECOMPNAMING_NAMINGPOLICY="vnf_ecomp_naming#naming_policy";
    public static String PROPERTY_NAME_ECOMPGENERATEDVMASSIGNMENTS = "ecomp_generated_vm_assignments";
    //Service
    public static String PROPERTY_NAME_SERVICENAMING_DEFAULT_ECOMPGENERATEDNAMING="service_naming#default#ecomp_generated_naming";
    public static String PROPERTY_NAME_SERVICENAMING_DEFAULT_NAMINGPOLICY="service_naming#default#naming_policy";
    //VL
    public static String PROPERTY_NAME_NETWORKTYPE="network_type";
    public static String PROPERTY_NAME_NETWORKROLE="network_role";
    public static String PROPERTY_NAME_NETWORKROLETAG="network_role_tag";
    public static String PROPERTY_NAME_NETWORKTECHNOLOGY="network_technology";
    public static String PROPERTY_NAME_NETWORKSCOPE="network_scope";
    public static String PROPERTY_NAME_NETWORKECOMPNAMING_ECOMPGENERATEDNAMING="network_ecomp_naming#ecomp_generated_naming";
    public static String PROPERTY_NAME_NETWORKECOMPNAMING_NAMINGPOLICY="network_ecomp_naming#naming_policy";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_ISSHAREDNETWORK="network_assignments#is_shared_network";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_ISEXTERNALNETWORK="network_assignments#is_external_network";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_ECOMPGENERATEDNETWORKASSIGNMENT="network_assignments#ecomp_generated_network_assignment";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_USEIPV4="network_assignments#ipv4_subnet_default_assignments#use_ipv4";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_IPNETWORKADDRESSPLAN="network_assignments#ipv4_subnet_default_assignments#ip_network_address_plan";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_DHCPENABLED="network_assignments#ipv4_subnet_default_assignments#dhcp_enabled";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_IPVERSION="network_assignments#ipv4_subnet_default_assignments#ip_version";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_CIDRMASK="network_assignments#ipv4_subnet_default_assignments#cidr_mask";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV4SUBNETDEFAULTASSIGNMENTS_MINSUBNETSCOUNT="network_assignments#ipv4_subnet_default_assignments#min_subnets_count";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_USEIPV6="network_assignments#ipv6_subnet_default_assignments#use_ipv6";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_IPNETWORKADDRESSPLAN="network_assignments#ipv6_subnet_default_assignments#ip_network_address_plan";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_DHCPENABLED="network_assignments#ipv6_subnet_default_assignments#dhcp_enabled";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_IPVERSION="network_assignments#ipv6_subnet_default_assignments#ip_version";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_CIDRMASK="network_assignments#ipv6_subnet_default_assignments#cidr_mask";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_IPV6SUBNETDEFAULTASSIGNMENTS_MINSUBNETSCOUNT="network_assignments#ipv6_subnet_default_assignments#min_subnets_count";

    /*public static String PROPERTY_NAME_NETWORKASSIGNMENTS_PROVIDERNETWORK_ISPROVIDERNETWORK="network_assignments#provider_network#is_provider_network";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_PROVIDERNETWORK_PHYSICALNETWORKNAME="network_assignments#provider_network#physical_network_name";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_PROVIDERNETWORK_NUMA="network_assignments#provider_network#numa";
    public static String PROPERTY_NAME_NETWORKASSIGNMENTS_PROVIDERNETWORK_PNICINSTANCE="network_assignments#provider_network#pnic_instance";
    */
    
    public static String PROPERTY_NAME_PROVIDERNETWORK_ISPROVIDERNETWORK="provider_network#is_provider_network";
    public static String PROPERTY_NAME_PROVIDERNETWORK_PHYSICALNETWORKNAME="provider_network#physical_network_name";
    public static String PROPERTY_NAME_PROVIDERNETWORK_NUMA="provider_network#numa";
    public static String PROPERTY_NAME_PROVIDERNETWORK_PNICINSTANCE="provider_network#pnic_instance";
    
    public static String PROPERTY_NAME_NETWORKFLOWS_ISBOUNDTOVPN="network_flows#is_bound_to_vpn";
    public static String PROPERTY_NAME_NETWORKFLOWS_VPNBINDING="network_flows#vpn_binding";
}
