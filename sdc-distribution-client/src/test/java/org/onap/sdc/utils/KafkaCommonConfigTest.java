/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2023 Nordix Foundation. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.onap.sdc.impl.Configuration;
import org.onap.sdc.utils.kafka.KafkaCommonConfig;

public class KafkaCommonConfigTest {
    private static final Configuration testConfigNoSSL = new Configuration(new TestConfiguration());
    private static final Configuration testConfigWithSSL = new Configuration(new TestConfigurationSSLProtocol());

    @Test
    public void testConsumerPropertiesNoSSL(){
        String msgBusAddress = "address1";
        testConfigNoSSL.setMsgBusAddress(msgBusAddress);
        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig(testConfigNoSSL);
        Properties consumerProperties = kafkaCommonConfig.getConsumerProperties();
        Assertions.assertEquals(consumerProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG), testConfigNoSSL.getKafkaSaslJaasConfig());
    }

    @Test
    public void testProducerPropertiesWithSSL(){
        String msgBusAddress = "address1";
        testConfigWithSSL.setMsgBusAddress(msgBusAddress);
        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig(testConfigWithSSL);
        Properties consumerProperties = kafkaCommonConfig.getProducerProperties();

        Assertions.assertNull(consumerProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        Assertions.assertEquals(consumerProperties.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG), testConfigNoSSL.getTrustStorePassword());
    }
}
