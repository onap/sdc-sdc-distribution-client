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

package org.onap.sdc.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fj.data.Either;

public class GeneralUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralUtils.class.getName());
    public static final int STRING_LENGTH_DIVIDER = 4;

    private  GeneralUtils() {

    }

    public static String calculateMD5(String data) {
        String calculatedMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(data);
        // encode base-64 result
        byte[] encodeBase64 = Base64.encodeBase64(calculatedMd5.getBytes());
        String encodeBase64Str = new String(encodeBase64);
        return encodeBase64Str;

    }

    public static String calculateMD5(byte[] decodedPayload) {
        String decodedMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(decodedPayload);
        byte[] encodeMd5 = Base64.encodeBase64(decodedMd5.getBytes());
        return new String(encodeMd5);
    }

    public static boolean isBase64Encoded(String str) {
        boolean isEncoded = false;
        try {
            // If no exception is caught, then it is possibly a base64 encoded string
            byte[] data = Base64.decodeBase64(str);
            // checks if the string was properly padded to the
            isEncoded = ((str.length() % STRING_LENGTH_DIVIDER == 0) && (Pattern.matches("\\A[a-zA-Z0-9/+]+={1,2}\\z", str)));

        } catch (Exception e) {
            // If exception is caught, then it is not a base64 encoded string
            isEncoded = false;
        }
        return isEncoded;
    }


    public static Either<List<String>, IDistributionClientResult> convertToValidHostName(List<String> msgBusAddresses) {
        List<String> uebLocalHostsNames = new ArrayList<>();
        for (String name : msgBusAddresses) {
            try {
                uebLocalHostsNames.add(InetAddress.getByName(name).getHostName());
            } catch (UnknownHostException e) {
                LOGGER.debug("UnknownHost: {}", e.getMessage(), e);
            }
        }
        Either<List<String>, IDistributionClientResult> response;
        if (uebLocalHostsNames.isEmpty()) {
            response = Either.right(new DistributionClientResultImpl(DistributionActionResultEnum.CONF_INVALID_MSG_BUS_ADDRESS, "configuration is invalid: " + DistributionActionResultEnum.CONF_INVALID_MSG_BUS_ADDRESS.name()));

        } else {
            response = Either.left(uebLocalHostsNames);
        }
        return response;
    }
}
