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

import java.util.List;
import java.util.Map;

public class HeatParameterConstraint {

	Map<String, String> length;
	Map<String, String> range;
	List<String> allowed_values;
	String allowed_pattern;
	String custom_constraint;
	String description;
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getLength() {
		return length;
	}
	public void setLength(Map<String, String> length) {
		this.length = length;
	}
	public Map<String, String> getRange() {
		return range;
	}
	public void setRange(Map<String, String> range) {
		this.range = range;
	}
	public List<String> getAllowed_values() {
		return allowed_values;
	}
	public void setAllowed_values(List<String> allowed_values) {
		this.allowed_values = allowed_values;
	}
	public String getAllowed_pattern() {
		return allowed_pattern;
	}
	public void setAllowed_pattern(String allowed_pattern) {
		this.allowed_pattern = allowed_pattern;
	}
	public String getCustom_constraint() {
		return custom_constraint;
	}
	public void setCustom_constraint(String custom_constraint) {
		this.custom_constraint = custom_constraint;
	}
	
	@Override
	public String toString() {
		String constraintTypeValue = "<empty>";
		String descriptionStr = "<empty>";
		if (length != null){
			constraintTypeValue = "length:"+length;
		} else if (range != null){
			constraintTypeValue = "range:"+range;
		} else if (allowed_values != null){
			constraintTypeValue = "allowed_values:"+allowed_values;
		} else if (allowed_pattern != null){
			constraintTypeValue = "allowed_pattern:"+allowed_pattern;
		} else if (custom_constraint != null){
			constraintTypeValue = "custom_constraint:"+custom_constraint;
		}
		if (description != null){
			descriptionStr = "description:"+description;
		}
		return new StringBuilder().append(constraintTypeValue).append(", ").append(descriptionStr).toString();
	}
}
