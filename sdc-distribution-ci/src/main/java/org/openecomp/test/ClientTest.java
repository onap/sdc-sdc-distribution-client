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

package org.openecomp.test;

import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.consumer.INotificationCallback;
import org.openecomp.sdc.api.results.IDistributionClientResult;
import org.openecomp.sdc.impl.DistributionClientFactory;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class ClientTest {
	public static void main(String[] args) throws Exception {
		try (SdcToscaParserFactory toscaParserFactory = SdcToscaParserFactory.getInstance()){
			//ISdcCsarHelper csarHelper = toscaParserFactory.getSdcCsarHelper("C:\\Users\\pa0916\\Desktop\\Work\\ASDC\\CSARs\\service-ServiceFdnt-csar-nt-metadata.csar");
			ISdcCsarHelper csarHelper = toscaParserFactory.getSdcCsarHelper("C:\\Users\\pa0916\\Desktop\\Work\\ASDC\\CSARs\\service-ServiceFdnt-csar.csar");
			String serviceSubstitutionMappingsTypeName = csarHelper.getServiceSubstitutionMappingsTypeName();
			System.out.println("serviceSubstitutionMappingsTypeName is "+serviceSubstitutionMappingsTypeName);
			String nodeTemplatePropertyLeafValue = csarHelper.getNodeTemplatePropertyLeafValue(csarHelper.getServiceVfList().get(0), "nf_role");
			System.out.println("property is "+nodeTemplatePropertyLeafValue);
		} 
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.getLogger("org.apache.http").setLevel(Level.INFO);

		IDistributionClient client = DistributionClientFactory.createDistributionClient();
		INotificationCallback callback;
		Boolean download = SimpleConfiguration.toDownload();
		if( download ){
			callback = new AdvanceCallBack(client);
		}
		else{
			callback = new SimpleCallback(client);
		}
		IDistributionClientResult result = client.init(new SimpleConfiguration(), callback);

		System.out.println(result.getDistributionMessageResult());

		System.out.println("Starting client...");
		IDistributionClientResult startResult = client.start();

		// Thread.sleep(10000);
		// client.stop();

		System.out.println(startResult.getDistributionMessageResult());

	}

}
