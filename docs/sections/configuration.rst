.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (C) 2021 Nordix Foundation

.. _config:

Configuration
=============

The sdc-distribution-client configuration in ONAP is generally done via a config file
loaded to the container.

Pre-requisites
--------------
The parent project must provide a mechanism to load the configuration at runtime.

This configuration is then passed as input to the parent projects implementation of the
sdc-distribution-client's IConfiguration interface.

For more detailed information regarding the abstract methods of the interface that must be
overridden byt the parent project, please refer to the javadocs.

Client configuration
--------------------
The table below shows a brief outline of the client config.

.. note::
    Attention should be given to any methods which provide default values.

+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| Name                      | Description                                                   | Type    | Required | Default |
+===========================+===============================================================+=========+==========+=========+
| sdcAddress                | SDC Distribution Engine address                               | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| user                      | User Name for SDC distribution consumer authentication        | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| password                  | User Password for SDC distribution consumer authentication    | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| environmentName           | The environment name. Must match what is set in SDC           | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| useHttpsWithSdc           | Whether to use https towards SDC                              | boolean | N        | true    |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| keyStorePath              | Y If useHttpsWithSdc is true                                  | String  | See desc | NA      |
|                           | N if activateServerTLSAuth set to false                       |         |          |         |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| keyStorePassword          | Y If useHttpsWithSdc is true                                  | String  | See desc | NA      |
|                           | N if activateServerTLSAuth set to false                       |         |          |         |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| activateServerTLSAuth     | mTLS setting.                                                 | boolean | Y        | NA      |
|                           | If false, KeyStore path and password must be set              |         |          |         |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| consumeProduceStatusTopic | Whether to consume from both SDC distribution topics          | boolean | N        | false   |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| consumerGroup             | Group Id to use when consuming from the NOTIFICATION topic    | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| consumerId                | Consumer Id to use when consuming from the NOTIFICATION topic | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| pollingInterval           | Interval between polls on the NOTIFICATION topic in sec       | int     | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| pollingTimeout            | NOTIFICATION topic polling timeout in sec                     | int     | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| relevantArtifactTypes     | List of artifact types the client should consume              | String  | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| filterEmptyResource       | See IConfiguration interface javadoc for more detail          | boolean | Y        | NA      |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| useSystemProxy            | Whether to use system or config proxies towards SDC           | boolean | N        | false   |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| httpsProxyHost            | The https proxy host to use towards SDC                       | String  | N        | null    |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| httpsProxyPort            | The https proxy port to use towards SDC                       | int     | N        | 0       |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| httpProxyHost             | The http proxy host to use towards SDC                        | String  | N        | null    |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+
| httpProxyPort             | The http proxy port to use towards SDC                        | int     | N        | 0       |
+---------------------------+---------------------------------------------------------------+---------+----------+---------+


Client Kafka config
^^^^^^^^^^^^^^^^^^^

The client will use kafka as a messaging bus to publish and subscribe to the relevant SDC topics.

The client's kafka configuration in ONAP should be handled via Strimzi kafka's
KafkaUser custom resource definition which provides access control to the relevant kafka topics.

This is done at helm chart level for each component that requires access to kafka topics.

+-----------------------+---------------------------------------------------------------+--------+----------+----------------------------------------------+
| Name                  | Description                                                   | Type   | Required | Default                                      |
+=======================+===============================================================+========+==========+==============================================+
| kafkaSecurityProtocol | The kafka security.protocol to use towards the kafka endpoint | String | N        | SASL_PLAINTEXT                               |
+-----------------------+---------------------------------------------------------------+--------+----------+----------------------------------------------+
| kafkaSaslMechanism    | The kafka sasl.mechanism to use towards the kafka endpoint    | String | N        | SCRAM-SHA-512                                |
+-----------------------+---------------------------------------------------------------+--------+----------+----------------------------------------------+
| KafkaSaslJaasConfig   | The kafka sasl.jaas.config to use towards the kafka endpoint  | String | Y        | Environment variable set to SASL_JAAS_CONFIG |
+-----------------------+---------------------------------------------------------------+--------+----------+----------------------------------------------+

.. note::
    The sasl.jaas.config for the client to use will default to an environment variable set as SASL_JAAS_CONFIG.

    This is expected to be in the form of org.apache.kafka.common.security.scram.ScramLoginModule required username="user" password="password";


Initializing the client
^^^^^^^^^^^^^^^^^^^^^^^

Once the client config is loaded, it can then be passed to the IDistributionClient to initialize.

..  code-block:: java

    // CustomClientConfig implementing IConfiguration
    CustomClientConfig config = loadClientConfig();

    IDistributionClient client = DistributionClientFactory.createDistributionClient();

    // MyNotificationCallback implementing INotificationCallback
    MyNotificationCallback callback;

    IDistributionClientResult result = client.init(config, callback);
    //Verify the result is SUCCESS, otherwise there’s a problem setting up vs either SDC or Kafka clients
    System.out.println(result.getDistributionMessageResult());

    System.out.println("Starting client...");
    IDistributionClientResult startResult = client.start();
    // Verify the result is SUCCESS, otherwise there’s a problem in receiving/sending notifications to/from kafka topics
    System.out.println(startResult.getDistributionMessageResult());
