/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2019 Nokia. All rights reserved.
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

import com.google.common.hash.Hashing;
import fj.data.Either;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class GeneralUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralUtils.class.getName());
    private static final int STRING_LENGTH_DIVIDER = 4;

    private  GeneralUtils() {
    }

    public static String calculateMD5(String data) {
        String calculatedMd5 = Hashing.md5().hashString(data, StandardCharsets.UTF_8).toString();
        // encode base-64 result
        byte[] encodeBase64 = Base64.getEncoder().encode(calculatedMd5.getBytes());
        return new String(encodeBase64);
    }

    public static String calculateMD5(byte[] decodedPayload) {
        String decodedMd5 = Hashing.md5().hashBytes(decodedPayload).toString();
        byte[] encodeMd5 = Base64.getEncoder().encode(decodedMd5.getBytes());
        return new String(encodeMd5);
    }

    public static boolean isBase64Encoded(String str) {
        boolean isEncoded = false;
        try {
            // If no exception is caught, then it is possibly a base64 encoded string
            Base64.getDecoder().decode(str);
            // checks if the string was properly padded to the
            isEncoded = ((str.length() % STRING_LENGTH_DIVIDER == 0) && (Pattern.matches("\\A[a-zA-Z0-9/+]+={1,2}\\z", str)));

        } catch (Exception e) {
            // If exception is caught, then it is not a base64 encoded string
            isEncoded = false;
        }
        return isEncoded;
    }
}
