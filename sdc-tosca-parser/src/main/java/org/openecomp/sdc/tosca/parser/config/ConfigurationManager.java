package org.openecomp.sdc.tosca.parser.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.openecomp.sdc.tosca.parser.utils.YamlToObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ConfigurationManager {

	private static Logger log = LoggerFactory.getLogger(ConfigurationManager.class.getName());

	private static final String CONFIGURATION_DIR = "config/";
	private static volatile ConfigurationManager instance;
//    private Configuration configuration;
//    private ErrorConfiguration errorConfiguration;

	Map<String, Object> configurations = new HashMap<String, Object>();



	private ConfigurationManager() {
		initialConfigObjectsFromFiles();
	}

	private void initialConfigObjectsFromFiles() {
		loadConfigurationClass(ErrorConfiguration.class);
		loadConfigurationClass(Configuration.class);
	}

	private <T> void loadConfigurationClass(Class<T> clazz) {
		T object = getObjectFromYaml(clazz);
		configurations.put(clazz.getSimpleName(), object);
	}


	public <T> T getObjectFromYaml(Class<T> className) {


		String configFileName = calculateFileName(className);

		URL url = Resources.getResource(CONFIGURATION_DIR + configFileName);
		String configFileContents = null;
		try {
			configFileContents = Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			log.error("ConfigurationManager - Failed to load configuration file");
		}
		YamlToObjectConverter yamlToObjectConverter = new YamlToObjectConverter();
		T object = yamlToObjectConverter.convertFromString(configFileContents, className);

		return object;
	}


	public static ConfigurationManager getInstance() {
		if (instance == null) {
			synchronized (ConfigurationManager.class) {
				if (instance == null) {
					instance = new ConfigurationManager();
				}
			}
		}
		return instance;
	}

	private static <T> String calculateFileName(Class<T> className) {

		String[] words = className.getSimpleName().split("(?=\\p{Upper})");

		StringBuilder builder = new StringBuilder();

		// There cannot be a null value returned from "split" - words != null is
		// redundant
		// if (words != null) {
		boolean isFirst = true;
		for (int i = 0; i < words.length; i++) {

			String word = words[i];
			if (word != null && !word.isEmpty()) {
				if (!isFirst) {
					builder.append("-");
				} else {
					isFirst = false;
				}
				builder.append(words[i].toLowerCase());
			}
		}
		return builder.toString() + ".yaml";

		/*
         * } else { return className.getSimpleName().toLowerCase() + Constants.YAML_SUFFIX; }
		 */

	}

	public ErrorConfiguration getErrorConfiguration() {
		return (ErrorConfiguration) configurations.get((ErrorConfiguration.class.getSimpleName()));
	}

	public Configuration getConfiguration() {
		return (Configuration) configurations.get((Configuration.class.getSimpleName()));
	}
}
