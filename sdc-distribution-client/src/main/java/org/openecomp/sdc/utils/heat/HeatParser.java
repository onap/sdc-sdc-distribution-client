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

package org.openecomp.sdc.utils.heat;

import java.util.Map;

import org.openecomp.sdc.utils.YamlToObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatParser {
	
	private static Logger log = LoggerFactory.getLogger(HeatParser.class.getName());


	/**
	 * Parses and returns the contents of the "parameters" section of YAML-formatted HEAT template.
	 *  
	 * @param heatFileContents - the string contents of HEAT template
	 * @return map of parameter name to HeatParameter object.
	 * For the following YAML snippet:
	 * <b>parameters:
	 *		image_name_1:
	 *			type: string
	 * 			label: Image Name
	 * 			description: SCOIMAGE Specify an image name for instance1
	 * 			default: cirros-0.3.1-x86_64
	 * </b>
	 * the map with one entry will be returned, the key will be "image_name_1".
	 * For a HeatParameter object, getConstraints() returns the list of all constraints,
	 * regardless of constraint type. 
	 * For that reason, for each constraint type a sugaring function were added on the HeatParameter type,
	 * for example getLengthConstraint(). A correct way to fetch the "length" constraint values map would be
	 * parameter.getLengthConstraint().getLength(). Same logic was implemented for all other constraint types.
	 * 
	 * In case of parse error, null will be returned.
	 * 
	 */
	public Map<String, HeatParameter> getHeatParameters(String heatFileContents){
		log.debug("Start of extracting HEAT parameters from file, file contents: {}", heatFileContents);
		Map<String, HeatParameter> heatParameters = null;
		YamlToObjectConverter yamlToObjectConverter = new YamlToObjectConverter();
		HeatConfiguration heatConfiguration = yamlToObjectConverter.convertFromString(heatFileContents, HeatConfiguration.class);
		if (heatConfiguration != null){
			heatParameters = heatConfiguration.getParameters();
		} else {
			log.error("Couldn't parse HEAT template.");
		}
		if (heatParameters != null && heatParameters.size() > 0){
			System.out.println("Found HEAT parameters: "+heatParameters.toString());
			log.debug("Found HEAT parameters: {}", heatParameters.toString());
		} else {
			log.warn("HEAT template parameters section wasn't found or is empty.");
		}
		return heatParameters;
	}
}
