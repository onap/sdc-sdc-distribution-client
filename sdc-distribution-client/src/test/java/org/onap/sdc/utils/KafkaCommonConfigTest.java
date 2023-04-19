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
        List<String> msgBusAddress = new ArrayList<>();
        msgBusAddress.add("address1");
        testConfigNoSSL.setMsgBusAddress(msgBusAddress);
        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig(testConfigNoSSL);
        Properties consumerProperties = kafkaCommonConfig.getConsumerProperties();
        Assertions.assertEquals(consumerProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG), testConfigNoSSL.getKafkaSaslJaasConfig());
    }

    @Test
    public void testProducerPropertiesWithSSL(){
        List<String> msgBusAddress = new ArrayList<>();
        msgBusAddress.add("address1");
        testConfigWithSSL.setMsgBusAddress(msgBusAddress);
        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig(testConfigWithSSL);
        Properties consumerProperties = kafkaCommonConfig.getProducerProperties();

        Assertions.assertNull(consumerProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        Assertions.assertEquals(consumerProperties.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG), testConfigNoSSL.getTrustStorePassword());
    }
}
