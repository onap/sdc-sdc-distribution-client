.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (C) 2022 Nordix Foundation

.. _build:

Build
=====

..
  * This section is used to describe how a software component is built from
    source into something ready for use either in a run-time environment or to
    build other components.

  * This section is typically provided for a platform-component, application,
    and sdk; and referenced in developer guides.


Environment
-----------

- JDK 11

- Maven 3.6.*

  - local .m2 settings.xml set to https://git.onap.org/oparent/plain/settings.xml

For more information regarding Env set up see `Setting Up Your Development Environment`_.

.. _Setting Up Your Development Environment: https://wiki.onap.org/display/DW/Setting+Up+Your+Development+Environment


.. _Build steps:

Steps
-----

Run the following from project root:

``mvn clean install``

The result is JAR file under the ``sdc-distribution-client/target`` folder

