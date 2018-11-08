# ONAP SDC Distribution client


---
---

# Introduction

ONAP SDC Distribution client is delivered as helper JAR that can be used by clients that work with SDC.
It register to SDC for getting notifications, listen for notification from SDC, download artifacts from SDC, and send response back to SDC.


# Compiling ONAP SDC Distribution client

ONAP SDC Distribution client can be compiled easily using maven command: `mvn clean install`
The result is JAR file under "target" folder


### How to use ONAP SDC Distribution client
Every client that wants to use the JAR, need to implement IConfiguration interface.

Configuration parameters:
--------------------------
AsdcAddress 			: ASDC Distribution Engine address. Value can be either hostname (with or without port), IP:port or FQDN (Fully Qualified Domain Name).
User					: User Name for ASDC distribution consumer authentication.
Password				: User Password for ASDC distribution consumer authentication.
PollingInterval			: Distribution Client Polling Interval towards UEB in seconds. Can Be reconfigured in runtime.
PollingTimeout			: Distribution Client Timeout in seconds waiting to UEB server response in each fetch interval. Can Be reconfigured in runtime.
RelevantArtifactTypes	: List of artifact types. If the service contains any of the artifacts in the list, the callback will be activated. Can Be reconfigured in runtime.
ConsumerGroup			: Returns the consumer group defined for this ONAP component, if no consumer group is defined return null. 
EnvironmentName			: Returns the environment name (testing, production etc... Can Be reconfigured in runtime.
ConsumerID				: Unique ID of ONAP component instance (e.x INSTAR name).
KeyStorePath			: Return full path to Client's Key Store that contains either CA certificate or the ASDC's public key (e.g /etc/keystore/asdc-client.jks). file will be deployed with asdc-distribution jar
KeyStorePassword		: Return client's Key Store password.
activateServerTLSAuth	: Sets whether ASDC server TLS authentication is activated. If set to false, Key Store path and password are not needed to be set.

Example of configuration file implementing IConfiguration interface:
--------------------------------------------------------------------
package org.onap.conf;

import java.util.ArrayList;
import java.util.List;

import org.onap.asdc.api.consumer.IConfiguration;
import org.onap.asdc.utils.ArtifactTypeEnum;

public class SimpleConfiguration implements IConfiguration{
	int randomSeed;
	String asdcAddress;
	
	public SimpleConfiguration(){
		randomSeed = ((int)(Math.random()*1000));
		asdcAddress = "127.0.0.1:8443";
	}
	public String getUser() {
		return "ci";
	}
	
	public List<String> getRelevantArtifactTypes() {
		List<String> res = new ArrayList<>();
		for(ArtifactTypeEnum artifactTypeEnum : ArtifactTypeEnum.values()){
			res.add(artifactTypeEnum.name());
		}
		return res;
	}
	
	public int getPollingTimeout() {
		return 20;
	}
	
	public int getPollingInterval() {
		return 20;
	}
	
	public String getPassword() {
		return "123456";
	}
	
	public String getEnvironmentName() {
		return "PROD";
	}
	
	public String getConsumerID() {
		return "unique-Consumer-ID"+randomSeed;
	}
	
	public String getConsumerGroup() {
		return "unique-Consumer-Group"+randomSeed;
	}
	
	public String getAsdcAddress() {
		return asdcAddress;
	}
	
	public void setAsdcAddress(String asdcAddress) {
		this.asdcAddress = asdcAddress;
	}
	@Override
	public String getKeyStorePath() {
		return null;
	}
	@Override
	public String getKeyStorePassword() {
		return null;
	}
	@Override
	public boolean activateServerTLSAuth() {
		return false;
	}

}


# Logging
Loggin can be done using log4j
Example of log.properties file:
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


# Getting Help

*** to be completed on release ***

SDC@lists.onap.org

SDC Javadoc and Maven site
 
*** to be completed on rrelease ***

