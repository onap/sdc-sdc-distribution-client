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

import org.openecomp.sdc.api.results.IDistributionClientDownloadResult;

/** Mock Implementation */
public class DistributionClientDownloadResultStubImpl extends DistributionClientResultStubImpl implements IDistributionClientDownloadResult {

	@Override
	public byte[] getArtifactPayload() {
		String mockPayload = "heat_template_version: 2013-05-23\r\n" + 
				"\r\n" + 
				"description: >\r\n" + 
				"  HOT template that creates one COR network (direct).\r\n" + 
				"\r\n" + 
				"parameters:\r\n" + 
				"  cor_direct_net_name:\r\n" + 
				"    type: string\r\n" + 
				"    description: Name of COR direct network\r\n" + 
				"  cor_direct_net_cidr:\r\n" + 
				"    type: string\r\n" + 
				"    description: Direct network address (CIDR notation)\r\n" + 
				"  cor_direct_net_gateway:\r\n" + 
				"    type: string\r\n" + 
				"    description: Direct network gateway address\r\n" + 
				"  cor_direct_net_RT:\r\n" + 
				"    type: string\r\n" + 
				"    description: Direct network route-target (RT)\r\n" + 
				"\r\n" + 
				"resources:\r\n" + 
				"  cor_direct_net:\r\n" + 
				"    type: OS::Contrail::VirtualNetwork\r\n" + 
				"    properties:\r\n" + 
				"      name: { get_param: cor_direct_net_name }\r\n" + 
				"      route_targets: [ get_param: cor_direct_net_RT ]\r\n" + 
				"\r\n" + 
				"  cor_direct_ip_subnet:\r\n" + 
				"    type: OS::Neutron::Subnet\r\n" + 
				"    properties:\r\n" + 
				"      network_id: { get_resource: cor_direct_net }\r\n" + 
				"      cidr: {get_param: cor_direct_net_cidr}\r\n" + 
				"      gateway_ip: { get_param: cor_direct_net_gateway }\r\n";
		
		return mockPayload.getBytes();
	}

	@Override
	public String getArtifactName() {
		// TODO Auto-generated method stub
		return "MackArtifactName";
	}

	@Override
	public String getArtifactFilename() {
		// TODO Auto-generated method stub
		return "MackArtifactName";
	}

}
