.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2021 ONAP contributors, Nokia

Architecture
============

..
   * This section is used to describe a software component from a high level
     view of capability, common usage scenarios, and interactions with other
     components required in the usage scenarios.

   * The architecture section is typically: provided in a platform-component
     and sdk collections; and referenced from developer and user guides.

   * This note must be removed after content has been added.


Capabilities
------------

The SDC distribution client provides an interface to enable projects to download the relevant
artifacts from SDC during runtime distribution of the resource/service.

SDC provides an engine to distribute the asset’s deployment artifacts (“Recipes”).

A key output of SDC is a set of models containing descriptions of the asset capabilities
(“descriptors”) and instructions (“Recipes”) to manage them.

Usage Scenarios
---------------
- Get Distribution Notification event

    The client polls the relevant NOTIFICATION topic for distribution events sent from SDC.

- Get Deployment Artifact from SDC

    Once a new notification event is received, the client will download the relevant artifacts that it
    has defined as part of it configuration.

- Send Distribution Status event

    As part of the processing of the downloaded artifacts, the client will publish it's progress to SDC
    via the relevant STATUS topic.
    Once the download has ben successfully completed, the client must publish it's final response
    COMPONENT_DONE_OK to the STATUS topic.


Interactions
------------

.. _SDCE-6 Swagger api: https://docs.onap.org/projects/onap-sdc/en/latest/_downloads/4eca2a3848d70e58566570a5ef889efb/swagger-sdce-6.json
.. _SDCE-7 Swagger api: https://docs.onap.org/projects/onap-sdc/en/latest/_downloads/542e76906472dae2e00adfad5fc7d879/swagger-sdce-7.json

SDC
^^^
The client interacts with the following SDC apis on initialisation:

- ``/sdc/v1/artifactTypes``

    Get the current artifact types from SDC to validate against the clients configured list.

    See `SDCE-6 Swagger api`_ for more details

- ``/sdc/v1/distributionKafkaData``

    Get the kafka distribution config from SDC to be used during publish and subscribe

    See `SDCE-6 Swagger api`_ for more details

The client interacts with the following SDC apis during distribution:

- ``/sdc/v1/catalog/services/{serviceName}/{serviceVersion}/resourceInstances/{resourceInstanceName}/artifacts/{artifactName}``

    Get the artifact for a particular resource instance defined in the artifactUrl of the notification event

    See `SDCE-7 Swagger api`_ for more details

- ``/sdc/v1/catalog/services/{serviceName}/{serviceVersion}/artifacts/{artifactName}``

    Get the artifact defined in the artifactUrl of the notification event

    See `SDCE-7 Swagger api`_ for more details

Kafka
^^^^^
The client uses kafka as it's messaging bus to publish and subscribe to the relevant SDC topics.