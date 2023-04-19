package org.onap.sdc.utils.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.onap.sdc.impl.Configuration;
import java.util.Properties;
import java.util.UUID;

public class KafkaCommonConfig {
    private final Configuration configuration;
    public KafkaCommonConfig(Configuration configuration){
        this.configuration = configuration;
    }

    public Properties getConsumerProperties(){
        Properties props = new Properties();
        setCommonProperties(props);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, configuration.getKafkaConsumerMaxPollInterval() * 1000);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID() + "-consumer-" + UUID.randomUUID());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, configuration.getKafkaConsumerSessionTimeout() * 1000);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getConsumerGroup());
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


        return props;
    }

    public Properties getProducerProperties(){
        Properties props = new Properties();
        setCommonProperties(props);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, configuration.getConsumerID() + "-producer-" + UUID.randomUUID());

        return props;
    }

    private void setCommonProperties(Properties props) {
        String securityProtocolConfig = configuration.getKafkaSecurityProtocolConfig();
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocolConfig);
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.getMsgBusAddress());

        if("SSL".equals(securityProtocolConfig)) {
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, configuration.getTrustStorePassword());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, configuration.getTrustStorePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, configuration.getKeyStorePassword());
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, configuration.getKeyStorePath());
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, configuration.getKeyStorePassword());
        }
        else{
            props.put(SaslConfigs.SASL_JAAS_CONFIG, configuration.getKafkaSaslJaasConfig());
            props.put(SaslConfigs.SASL_MECHANISM, configuration.getKafkaSaslMechanism());
        }
    }

}
