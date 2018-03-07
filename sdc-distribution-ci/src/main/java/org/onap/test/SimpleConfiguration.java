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

package org.onap.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.onap.sdc.api.consumer.IConfiguration;

public class SimpleConfiguration implements IConfiguration{

	/*public String getUser()
	  {
	    return System.getProperty("user");
	  }
	  
	public List<String> getRelevantArtifactTypes() {
		return ArtifactTypeEnum.getAllTypes();
	}
		
	  public int getPollingTimeout()
	  {
	    return 20;
	  }
	  
	  public int getPollingInterval()
	  {
	    return 20;
	  }
	  
	  public String getPassword()
	  {
	    return System.getProperty("password");
	  }
	  
	  public String getEnvironmentName()
	  {
	    return System.getProperty("env");
	  }
	  
	  public String getConsumerID()
	  {
	    return System.getProperty("consumerID");
	  }
	  
	  public String getConsumerGroup()
	  {
	    return System.getProperty("groupID");
	  }
	  
	  public String getAsdcAddress()
	  {
	    return System.getProperty("beAddress");
	  }
	  
	  public String getKeyStorePath()
	  {
	    return "";
	  }
	  
	  public String getKeyStorePassword()
	  {
	    return "Aa123456";
	  }
	  
	  public boolean activateServerTLSAuth()
	  {
	    return Boolean.parseBoolean(System.getProperty("auth"));
//		res.add(ArtifactTypeEnum.HEAT_ARTIFACT);
//		res.add(ArtifactTypeEnum.HEAT_ENV);
//		res.add(ArtifactTypeEnum.MURANO_PKG);
//		res.add(ArtifactTypeEnum.VF_LICENSE);
//		res.add(ArtifactTypeEnum.APPC_CONFIG);
//		res.add(ArtifactTypeEnum.MODEL_INVENTORY_PROFILE);
//		res.add(ArtifactTypeEnum.VNF_CATALOG);
//		res.add(ArtifactTypeEnum.APPC_CONFIG);
//		res.add(ArtifactTypeEnum.VF_MODULES_METADATA);
//		return "PROD-Tedy-Only";
//		return "A-AI";
//		return "A-AI";
	  }
	  
	  @Override
		public boolean isFilterInEmptyResources() {
			return false;
		}
	  
	  public static String downloadPath() {
			return "c:\\temp\\";
		}
	  
	  public static Boolean toDownload() {
			return false;
		}*/
	  
	public String getUser() {
		return "ci";
	}
	
	public List<String> getRelevantArtifactTypes() {

//		List<String> res = new ArrayList<String>();
//		for (ArtifactTypeEnum type : AssetTypeEnum.values()){
//			res.add(type.name());
//		}
		return ArtifactTypeEnum.getAllTypes();
	}

	
	public int getPollingTimeout() {
		return 20;
	}
	
	public int getPollingInterval() {
		return 20;
	}
	
	public String getPassword() {
		return "123456";
	}
	
	public String getEnvironmentName() {
		return "PROD";
	}
	
	public String getConsumerID() {
		return "consumerVasya";
	}
	
	public String getConsumerGroup() {
		return "groupVasya";
		
	}
	
	public static Boolean toDownload() {
		return true;
	}
	
	public static String downloadPath() {
		return "c:\\temp\\";
	}
	
	public String getAsdcAddress() {
		return "127.0.0.1:8443";
	}

	@Override
	public List<String> getMsgBusAddress() {
		return new ArrayList<>();
	}

	@Override
	public String getKeyStorePath() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getKeyStorePassword() {
		
		return "Aa123456";
	}

	@Override
	public boolean activateServerTLSAuth() {
		
		return false;
	}

	@Override
	public boolean isFilterInEmptyResources() {
		return false;
	}

	@Override
	public Boolean isUseHttpsWithDmaap() {
		return true;
	}

	@Override
	public boolean isConsumeProduceStatusTopic() {
		return false;
	}

}
