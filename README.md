# ONAP SDC Distribution client

---
---

## Introduction

ONAP SDC Distribution client is delivered as helper JAR that can be used by clients that work with SDC.
It listens for notifications from SDC, download artifacts from SDC, and send response back to SDC.

## Compiling ONAP SDC Distribution client

As mentioned in the [onap wiki](https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16220206/Setting+Up+Your+Development+Environment),
the [settings.xml](https://git.onap.org/oparent/plain/settings.xml) from the oparent project must be
installed in your ~/.m2 folder and referenced by your IDE.

Once maven is set up properly, ONAP SDC Distribution client can be compiled easily using maven command: `mvn clean install`
The result is JAR file under "target" folder


### How to use ONAP SDC Distribution client

Every client that wants to use the JAR, need to implement IConfiguration interface.

See the SDC ONAP read the docs for more detail in relation to the sdc-distribution-client usage.
https://docs.onap.org/projects/onap-sdc/en/kohn/sdcsdks.html#sdc-tosca-and-sdc-distribution-client

## Logging

Loggin can be done using log4j
Example of log.properties file:
```ini
-------------------------------
log4j.rootCategory=DEBUG, CONSOLE, LOGFILE
log4j.logger.org.onap=TRACE, CONSOLE, LOGFILE

# CONSOLE is set to be a ConsoleAppender using a PatternLayout.
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%p %d{yyyy-MM-dd HH:mm:ss.SSS Z} %c{1} - %m%n

# LOGFILE is set to be a File appender using a PatternLayout.
log4j.appender.LOGFILE=org.apache.log4j.RollingFileAppender
log4j.appender.LOGFILE.File=logs/wordnik.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%p %d{yyyy-MM-dd HH:mm:ss.SSS Z} %c{1} - %m%n
log4j.appender.LOGFILE.MaxFileSize=10MB
log4j.appender.LOGFILE.MaxBackupIndex=10
```

## Getting Help

*** to be completed on release ***

SDC@lists.onap.org

SDC Javadoc and Maven site

*** to be completed on rrelease ***
