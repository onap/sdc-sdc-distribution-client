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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvanceCallBack extends SimpleCallback {

    private static final Logger LOG = LoggerFactory.getLogger(AdvanceCallBack.class);

    public AdvanceCallBack(IDistributionClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void postDownloadLogic(IDistributionClientDownloadResult downloadResult) {
        if (downloadResult.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS) {
            saveArtifactPayloadToDisk(downloadResult);
        }
    }

    protected void saveFile(byte[] bs, String fileName) {
        String downloadPath = SimpleConfiguration.downloadPath();
        try (FileOutputStream fileOutputStream = new FileOutputStream(downloadPath + fileName)) {
            fileOutputStream.write(bs);
            fileOutputStream.close();
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            // Auto-generated catch block
        }
    }

    protected void saveArtifactPayloadToDisk(IDistributionClientDownloadResult downloadResult) {
        LOG.info("################ Downloaded Artifact Payload Start ################");
        String fileName = downloadResult.getArtifactFilename();
        saveFile(downloadResult.getArtifactPayload(), fileName);
        LOG.info("################ Downloaded Artifact Payload End ################");
    }
}
