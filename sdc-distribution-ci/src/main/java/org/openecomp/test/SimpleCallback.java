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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.sdc.api.IDistributionClient;
import org.openecomp.sdc.api.consumer.IDistributionStatusMessage;
import org.openecomp.sdc.api.consumer.INotificationCallback;
import org.openecomp.sdc.api.notification.IArtifactInfo;
import org.openecomp.sdc.api.notification.INotificationData;
import org.openecomp.sdc.api.notification.IResourceInstance;
import org.openecomp.sdc.api.notification.IVfModuleMetadata;
import org.openecomp.sdc.api.results.IDistributionClientDownloadResult;
import org.openecomp.sdc.api.results.IDistributionClientResult;
import org.openecomp.sdc.utils.ArtifactTypeEnum;
import org.openecomp.sdc.utils.DistributionActionResultEnum;
import org.openecomp.sdc.utils.DistributionStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleCallback implements INotificationCallback {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCallback.class);
    protected IDistributionClient client;
    public List<IArtifactInfo> iArtifactInfo;

    public final Map<String, IDistributionClientResult> simpleCallbackResults = new HashMap<String, IDistributionClientResult>();

    public Map<String, IDistributionClientResult> getSimpleCallbackResults() {
        return simpleCallbackResults;
    }

    public List<IArtifactInfo> getIArtifactInfo() {
        return iArtifactInfo;
    }

    public SimpleCallback(IDistributionClient client) {
        this.client = client;
    }


    public void activateCallback(INotificationData data) {

        List<IArtifactInfo> artifacts = getArtifacts(data);

        for (IArtifactInfo iArtifactInfo : artifacts) {

            IArtifactInfo artifactMetadataByUUID = data.getArtifactMetadataByUUID(iArtifactInfo.getArtifactUUID());
            assertEquals("check artifact checksum", iArtifactInfo.getArtifactChecksum(),
                artifactMetadataByUUID.getArtifactChecksum());
            LOG.info(artifactMetadataByUUID.getArtifactURL());
            if (artifactMetadataByUUID.getArtifactType().equals(ArtifactTypeEnum.VF_MODULES_METADATA)) {
                IDistributionClientDownloadResult download = client.download(iArtifactInfo);
                if (download.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS) {
                    List<IVfModuleMetadata> decodeVfModuleArtifact = client
                        .decodeVfModuleArtifact(download.getArtifactPayload());
//					assertEquals("decoded not equal to actual group amount ",  decodeVfModuleArtifact.size(), 2);
                    if (!decodeVfModuleArtifact.isEmpty()) {
                        for (IVfModuleMetadata moduleMetadata : decodeVfModuleArtifact) {
                            List<String> moduleArtifacts = moduleMetadata.getArtifacts();
                            if (moduleArtifacts != null) {

                                for (String artifactId : moduleArtifacts) {

                                    IArtifactInfo artifactInfo = data.getArtifactMetadataByUUID(artifactId);
                                    IDistributionClientDownloadResult downloadArt = client.download(artifactInfo);
                                    assertEquals(downloadArt.getDistributionActionResult(),
                                        DistributionActionResultEnum.SUCCESS);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (IArtifactInfo relevantArtifact : artifacts) {
            // Download Artifact
            IDistributionClientDownloadResult downloadResult = client.download(relevantArtifact);

            postDownloadLogic(downloadResult);

            simpleCallbackResults.put("downloadResult", downloadResult);
            LOG.info("downloadResult: " + downloadResult.toString());
            LOG.info("<<<<<<<<<<< Artifact content >>>>>>>>>>");
            LOG.info(Decoder.encode(downloadResult.getArtifactPayload()));

            /////Print artifact content to console///////

//			byte[] contentInBytes = BaseEncoding.base64().decode(Decoder.encode(downloadResult.getArtifactPayload()));
//			try {
//				LOG.info("Source content: " + new String(contentInBytes, "UTF-8"));
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
            LOG.info("ArtInfo_timeout: " + relevantArtifact.getArtifactTimeout());
            LOG.info("ArtInfo_Art_description: " + relevantArtifact.getArtifactDescription());
            LOG.info("ArtInfo_Art_CheckSum: " + relevantArtifact.getArtifactChecksum());
            LOG.info("ArtInfo_Art_Url: " + relevantArtifact.getArtifactURL());
            LOG.info("ArtInfo_Art_Type: " + relevantArtifact.getArtifactType());
            LOG.info("ArtInfo_Art_Name: " + relevantArtifact.getArtifactName());
            LOG.info("ArtInfo_UUID: " + relevantArtifact.getArtifactUUID());
            LOG.info("ArtInfo_Version: " + relevantArtifact.getArtifactVersion());
            LOG.info("ArtInfo_RelatedArtifacts: " + relevantArtifact.getRelatedArtifacts());

            LOG.info("ArtInfo_Serv_description: " + data.getServiceDescription());
            LOG.info("ArtInfo_Serv_Name: " + data.getServiceName());
            LOG.info("Get_serviceVersion: " + data.getServiceVersion());
            LOG.info("Get_Service_UUID: " + data.getServiceUUID());
            LOG.info("ArtInfo_DistributionId: " + data.getDistributionID());
            LOG.info("ArtInfo_ServiceInvariantUUID: " + data.getServiceInvariantUUID());

            //		assertTrue("response code is not 200, returned :" + downloadResult.getDistributionActionResult(), downloadResult.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS );

            try {
                String payload = new String(downloadResult.getArtifactPayload());
//				LOG.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//				LOG.info(payload);
//				LOG.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

            } catch (Exception e) {
                LOG.error("catch");
//				break;
                // TODO: handle exception
            }

            if (downloadResult.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS) {
                handleSuccessfullDownload(data, relevantArtifact, downloadResult.getArtifactPayload());
            } else {
                handleFailedDownload(data, relevantArtifact);
            }
        }
//		if (data != null){
//			iArtifactInfo.addAll(artifacts);
//		}

    }

    private List<IArtifactInfo> getArtifacts(INotificationData data) {
        List<IArtifactInfo> ret = new ArrayList<IArtifactInfo>();
        List<IResourceInstance> resources = data.getResources();
//		data.getArtifactMetadataByUUID(arg0)
        List<String> relevantArtifactTypes = client.getConfiguration().getRelevantArtifactTypes();

        List<IArtifactInfo> collect = resources.stream().flatMap(e -> e.getArtifacts().stream())
            .filter(p -> relevantArtifactTypes.contains(p.getArtifactType())).collect(Collectors.toList());
//		if( resources != null ){
//			for( IResourceInstance resourceInstance : resources){
//				if( resourceInstance.getArtifacts() != null ){
//					
//					
//					
//					ret.addAll(resourceInstance.getArtifacts());
//					
//					
//				}
//			}
//		}
        ret.addAll(collect);

        List<IArtifactInfo> servicesArt = data.getServiceArtifacts();
        if (servicesArt != null) {
            ret.addAll(servicesArt);
        }

        LOG.info("I am here: " + ret.toString());
        return ret;
    }


    private void handleFailedDownload(INotificationData data,
        IArtifactInfo relevantArtifact) {
        // Send Download Status
        IDistributionClientResult sendDownloadStatus = client.sendDownloadStatus(
            buildStatusMessage(client, data, relevantArtifact, DistributionStatusEnum.DOWNLOAD_ERROR));
        postDownloadStatusSendLogic(sendDownloadStatus);
    }

    private void handleSuccessfullDownload(INotificationData data, IArtifactInfo relevantArtifact, byte[] payload) {
        // Send Download Status
        IDistributionClientResult sendDownloadStatus = client
            .sendDownloadStatus(buildStatusMessage(client, data, relevantArtifact, DistributionStatusEnum.DOWNLOAD_OK));

        simpleCallbackResults.put("sendDownloadStatus", sendDownloadStatus);
//		assertTrue("response code is not 200, returned :" + sendDownloadStatus.getDistributionActionResult(), sendDownloadStatus.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS );

        // Doing deployment ...
        postDownloadStatusSendLogic(sendDownloadStatus);
        boolean isDeployedSuccessfully = handleDeployment(data, relevantArtifact, payload);
        IDistributionClientResult deploymentStatus;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LOG.error(e.getLocalizedMessage(), e);
        }
        if (isDeployedSuccessfully) {
            deploymentStatus = client.sendDeploymentStatus(
                buildStatusMessage(client, data, relevantArtifact, DistributionStatusEnum.DEPLOY_OK));

            simpleCallbackResults.put("sendDeploymentStatus", deploymentStatus);
//			assertTrue("response code is not 200, returned :" + deploymentStatus.getDistributionActionResult(), deploymentStatus.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS );

        } else {
            deploymentStatus = handleFailedDeployment(data, relevantArtifact);
        }

        postDeploymentStatusSendLogic(deploymentStatus);
    }

    private IDistributionClientResult handleFailedDeployment(INotificationData data, IArtifactInfo relevantArtifact) {
        IDistributionClientResult deploymentStatus;
        boolean isAlreadyDeployed = checkIsDeployed();
        if (isAlreadyDeployed) {
            deploymentStatus = client.sendDeploymentStatus(
                buildStatusMessage(client, data, relevantArtifact, DistributionStatusEnum.ALREADY_DEPLOYED));
        } else {
            deploymentStatus = client.sendDeploymentStatus(
                buildStatusMessage(client, data, relevantArtifact, DistributionStatusEnum.DEPLOY_ERROR));
        }
        return deploymentStatus;
    }

    protected void postDownloadLogic(IDistributionClientDownloadResult downloadResult) {
        // TODO Auto-generated method stub

    }

    private void postDownloadStatusSendLogic(
        IDistributionClientResult sendDownloadStatus) {
        // TODO Auto-generated method stub

    }

    private void postDeploymentStatusSendLogic(
        IDistributionClientResult deploymentStatus) {
        // TODO Auto-generated method stub

    }

    private boolean checkIsDeployed() {
        return false;
    }

    private boolean handleDeployment(INotificationData data, IArtifactInfo relevantArtifact, byte[] payload) {
        if (relevantArtifact.getArtifactType().equals(ArtifactTypeEnum.VF_MODULES_METADATA.name())) {

            try {
                List<IArtifactInfo> serviceArtifacts = data.getServiceArtifacts();
                List<IResourceInstance> resourcesArtifacts = data.getResources();

                JSONArray jsonData = new JSONArray(new String(payload));
                boolean artifactIsFound = true;
                for (int index = 0; index < jsonData.length(); index++) {

                    JSONObject jsonObject = (JSONObject) jsonData.get(index);
                    JSONArray artifacts = (JSONArray) jsonObject.get("artifacts");
                    for (int i = 0; i < artifacts.length(); i++) {
                        String artifact = artifacts.getString(i);

                        Optional<IArtifactInfo> serviceArtifactFound = serviceArtifacts.stream()
                            .filter(x -> x.getArtifactUUID().equals(artifact)).findFirst();

                        boolean isResourceFound = false;
                        for (IResourceInstance resourcesArtifact : resourcesArtifacts) {
                            Optional<IArtifactInfo> resourceArtifactFound = resourcesArtifact.getArtifacts()
                                .stream().filter(x -> x.getArtifactUUID().equals(artifact)).findFirst();
                            isResourceFound = resourceArtifactFound.isPresent() || isResourceFound;
                        }

                        if (!serviceArtifactFound.isPresent() && !isResourceFound) {
                            artifactIsFound = false;
                            LOG.info("################ Artifact: " + artifact
                                + " NOT FOUND in Notification Data ################");
                        }
                    }
                }
                return artifactIsFound;
            } catch (Exception e) {
                LOG.error("################ Couldn't convert vf_modules_metadata.json to json : " + e.getMessage(), e);
                return false;
            }
        } else {
            return true;
        }

//		to return deploy_error use return false
//		return false;
    }

    public static IDistributionStatusMessage buildStatusMessage(
        final IDistributionClient client, final INotificationData data,
        final IArtifactInfo relevantArtifact,
        final DistributionStatusEnum status) {
        return new IDistributionStatusMessage() {

            public long getTimestamp() {
                return System.currentTimeMillis();
            }

            public DistributionStatusEnum getStatus() {
                return status;
            }

            public String getDistributionID() {
                return data.getDistributionID();
            }

            public String getConsumerID() {
                return client.getConfiguration().getConsumerID();
            }

            public String getArtifactURL() {
                return relevantArtifact.getArtifactURL();
            }
        };
    }
}
