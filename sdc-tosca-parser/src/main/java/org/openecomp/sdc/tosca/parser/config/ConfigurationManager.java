package org.openecomp.sdc.tosca.parser.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.openecomp.sdc.tosca.parser.utils.YamlToObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ConfigurationManager {

	private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class.getName());

	private static final String CONFIGURATION_FILE = "config/configuration.yaml";
	private static volatile ConfigurationManager instance;
	private Configuration configuration;


    private ConfigurationManager() {
		URL url = Resources.getResource(CONFIGURATION_FILE);
		String configFileContents = null;
		try {
			configFileContents = Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			log.error("ConfigurationManager - Failed to load configuration file");
		}
		YamlToObjectConverter yamlToObjectConverter = new YamlToObjectConverter();
		this.configuration = yamlToObjectConverter.convertFromString(configFileContents, Configuration.class);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			synchronized (ConfigurationManager.class){
				if (instance == null){
					instance = new ConfigurationManager();
				}
			}
		}
		return instance;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
