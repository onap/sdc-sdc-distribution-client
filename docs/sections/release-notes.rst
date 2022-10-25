.. This work is licensed under a Creative Commons Attribution 4.0
   International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (C) 2022 Nordix Foundation

.. _release_notes:


..      ===========================
..      * * *      LONDON     * * *
..      ===========================


*************
Release Notes
*************

..
   * The release note needs to be updated for each ONAP release
   * Except the section "Release data" all other sections are optional and should be
     applied where applicable
   * Only the current release is to be documented in this document
   * This note needs to be removed before publishing the final result


London
========


Abstract
========

This document provides the release notes for the London release.

Summary
=======
Client migration to use kafka as it's main messaging bus to publish and subscribe to the
SDC distribution topics.
Removal of all interaction with DMaaP Message Router apis.

Release Data
============

+--------------------------------------+--------------------------------------+
| **Project**                          | sdc/sdc-distribution-client          |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Jars**                             |  2.0.0                               |
|                                      |                                      |
+--------------------------------------+--------------------------------------+

New features
------------
Move client to use kafka native to publish and subscribe to SDC topics
https://jira.onap.org/browse/DMAAP-1745

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~

..  code-block:: xml

    <groupId>org.onap.sdc.sdc-distribution-client</groupId>
    <artifactId>sdc-distribution-client</artifactId>
    <version>2.0.0</version>

Known Limitations, Issues and Workarounds
=========================================

System Limitations
------------------
NA

Known Vulnerabilities
---------------------
NA

Workarounds
-----------
NA

Security Notes
--------------
NA

References
==========

For more information on the ONAP London release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org
