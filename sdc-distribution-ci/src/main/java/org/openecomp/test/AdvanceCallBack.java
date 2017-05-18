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

import java.io.FileOutputStream;
import java.io.IOException;

import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.results.IDistributionClientDownloadResult;
import org.openecomp.sdc.utils.DistributionActionResultEnum;

public class AdvanceCallBack extends SimpleCallback{
	
	

	public AdvanceCallBack(IDistributionClient client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void postDownloadLogic( IDistributionClientDownloadResult downloadResult) {
           if( downloadResult.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS){
                  saveArtifactPayloadToDisk(downloadResult);
           }

    }
    
    protected void saveFile(byte[] bs, String fileName) {
    	 try { 
    		 String downloadPath = SimpleConfiguration.downloadPath();
    		 FileOutputStream fileOuputStream = new FileOutputStream(downloadPath + fileName); 
			 fileOuputStream.write(bs);
			 fileOuputStream.close();
    	 }   catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		 }  
 }
 
 protected void saveArtifactPayloadToDisk(IDistributionClientDownloadResult downloadResult) {
        System.out.println("################ Downloaded Artifact Payload Start ################");
        String fileName = downloadResult.getArtifactFilename();
        saveFile(downloadResult.getArtifactPayload(), fileName);
        System.out.println("################ Downloaded Artifact Payload End ################");
 }


	

}
