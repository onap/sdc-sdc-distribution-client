/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2024 Deutsche Telekom Intellectual Property. All rights reserved.
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

package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IStatusData;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Disabled
public class SerializationTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void thatINotificationDataDefaultImplementationCanBeDeserialized() {
    String json = "src/test/resources/messages/distribution.json";
    String notification = Files.readString(Paths.get(json));

    INotificationData notificationData = mapper.readValue(notification, INotificationData.class);
    assertNotNull(notificationData);
    String expectedNotification = notification.replace("resoucreType", "resourceType"); // The resourceType attribute has a @TypeAlias for resoucreType
    JSONAssert.assertEquals(expectedNotification, mapper.writeValueAsString(notificationData), JSONCompareMode.LENIENT);
  }

  @Test
  @SneakyThrows
  void thatIStatusDataDefaultImplementationCanBeDeserialized() {
    String json = "src/test/resources/messages/distributionStatus.json";
    String distributionStatus = Files.readString(Paths.get(json));

    IStatusData statusData = mapper.readValue(distributionStatus, IStatusData.class);
    assertNotNull(statusData);
    JSONAssert.assertEquals(distributionStatus, mapper.writeValueAsString(statusData), JSONCompareMode.LENIENT);
  }
}
