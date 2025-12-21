# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [2.2.0] - 11/01/2025

### Added

- add a `sdc-distribution-client-api` maven submodule

### Changed

- move `org.onap.sdc.api.*` package out of the `sdc-distribution-client` maven module into the `sdc-distribution-client-api` module
- change the import for the `DistributionStatusEnum`: `org.onap.sdc.utils.DistributionStatusEnum` -> `org.onap.sdc.api.notification.DistributionStatusEnum`
- change the import for the `DistributionActionResultEnum`: `org.onap.sdc.utils.DistributionActionResultEnum` -> `org.onap.sdc.api.results.DistributionActionResultEnum`

## [1.4.2] - 17/12/2020

### Changed

- [SDC-3400](https://jira.onap.org/browse/SDC-3400) - Upgrade SDC Distribution Client code to use Java 11
  Upgrade libraries to the newest versions
  Upgrade Junit to 5
  Remove old CI project
  Create new integration tests for client inicialization
