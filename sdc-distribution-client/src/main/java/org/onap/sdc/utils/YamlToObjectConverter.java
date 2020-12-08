/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.utils;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.onap.sdc.utils.heat.HeatConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class YamlToObjectConverter {

    private static Logger log = LoggerFactory
            .getLogger(YamlToObjectConverter.class.getName());

    private static final Map<String, Yaml> YAMLS = new HashMap<>();

    private static final Yaml DEFAULT_YAML = new Yaml();

    static {
        YAMLS.put(HeatConfiguration.class.getName(), provideYamlForHeatConfiguration());
    }

    private static Yaml provideYamlForHeatConfiguration() {
        org.yaml.snakeyaml.constructor.Constructor heatConstructor = new org.yaml.snakeyaml.constructor.Constructor(HeatConfiguration.class);
        TypeDescription heatDescription = new TypeDescription(HeatConfiguration.class);
        heatConstructor.addTypeDescription(heatDescription);
        PropertyUtils propertyUtils = new PropertyUtils() {
            @Override
            //This is in order to workaround "default" field in HeatParameterEntry, since default is Java keyword
            public Property getProperty(Class<?> type, String name, BeanAccess bAccess) {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                return super.getProperty(type, name, bAccess);
            }

        };
        //Skip properties which are not found - we only are interested in "parameters"
        propertyUtils.setSkipMissingProperties(true);
        heatConstructor.setPropertyUtils(propertyUtils);

        return new Yaml(heatConstructor);
    }

    public <T> T convert(String dirPath, Class<T> className,
                         String configFileName) {
        T config = null;

        try {
            String fullFileName = dirPath + File.separator + configFileName;
            config = convert(fullFileName, className);
        } catch (Exception e) {
            log.error("Failed to convert yaml file {} to object.", configFileName, e);
        }

        return config;
    }

    public <T> T convert(String fullFileName, Class<T> className) {
        T config = null;

        Yaml yaml = getYamlByClassName(className);

        try {
            File f = new File(fullFileName);
            if (!f.exists()) {
                log.warn("The file {} cannot be found. Ignore reading configuration.", fullFileName);
            } else {
                try (InputStream in = Files.newInputStream(Paths.get(fullFileName))) {
                    config = yaml.loadAs(in, className);
                }
            }
        } catch (Exception e) {
            log.error("Failed to convert yaml file {} to object.", fullFileName, e);
        }

        return config;
    }

    public <T> T convertFromString(String yamlContents, Class<T> className) {
        T config = null;

        try {
            Yaml yaml = getYamlByClassName(className);
            config = yaml.loadAs(yamlContents, className);
        } catch (Exception e) {
            log.error("Failed to convert YAML {} to object.", yamlContents, e);
        }

        return config;
    }

    private static synchronized <T> Yaml getYamlByClassName(Class<T> className) {
        Yaml yaml = YAMLS.get(className.getName());
        if (yaml == null) {
            yaml = DEFAULT_YAML;
        }

        return yaml;
    }
}
