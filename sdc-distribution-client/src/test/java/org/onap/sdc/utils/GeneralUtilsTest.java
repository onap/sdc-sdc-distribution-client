/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.util.Base64;
import org.junit.Test;

public class GeneralUtilsTest {

    private static final String TEXT_TO_CODE = "This is example text.";

    @Test
    public void shouldCalculateMD5ForBytes() {
        String hashed = GeneralUtils.calculateMD5(TEXT_TO_CODE.getBytes());
        byte[] decoded = Base64.getDecoder().decode(hashed);
        HashCode expected = Hashing.md5().hashString(TEXT_TO_CODE, Charsets.UTF_8);
        assertEquals(expected.toString(), new String(decoded));
    }

    @Test
    public void shouldCalculateMD5ForString() {
        String hashed = GeneralUtils.calculateMD5(TEXT_TO_CODE);
        byte[] decoded = Base64.getDecoder().decode(hashed);
        HashCode expected = Hashing.md5().hashString(TEXT_TO_CODE, Charsets.UTF_8);
        assertEquals(expected.toString(), new String(decoded));
    }

    @Test
    public void shouldValidateBase64EncodedString() {
        HashCode expected = Hashing.md5().hashString(TEXT_TO_CODE, Charsets.UTF_8);
        String base64String = Base64.getEncoder().encodeToString(expected.asBytes());
        assertTrue(GeneralUtils.isBase64Encoded(base64String));
    }

    @Test
    public void shouldInvalidateBase64EncodedString() {
        String base64String = Base64.getEncoder().encodeToString(TEXT_TO_CODE.getBytes());
        assertFalse(GeneralUtils.isBase64Encoded(base64String));
    }

}