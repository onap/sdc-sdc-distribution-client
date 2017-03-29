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

import java.util.ArrayList;
import java.util.List;

public class HeatParameter{

	String type;
	String label;
	String description;
	//This is in order to workaround "default" field in HeatParameterEntry, since default is Java keyword
	//YAML constructor will lowercase it during parsing	
	String Default;
	String hidden = "false";//Default value according to OpenStack spec
	List<HeatParameterConstraint> constraints;



	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public List<HeatParameterConstraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<HeatParameterConstraint> constraints) {
		this.constraints = constraints;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDefault() {
		return Default;
	}
	public void setDefault(String default1) {
		Default = default1;
	}
	
	

	// Getting specific constraints
	/**
	 * Get the first "length" constraint from HEAT parameter.
	 * No additional "length" constraint is searched for.
	 * 
	 * @return first "length" constraint found for this parameter,
	 * or null if no such constraint exists. 
	 */
	public HeatParameterConstraint getLengthConstraint(){
		HeatParameterConstraint res = null;
		if (constraints != null){
			for (HeatParameterConstraint entry : constraints){
				if (entry.getLength() != null){
					res = entry;
					break;
				}
			}
		}
		return res;
	}


	/**
	 * Get the first "range" constraint from HEAT parameter.
	 * No additional "range" constraint is searched for.
	 * 
	 * @return first "range" constraint found for this parameter,
	 * or null if no such constraint exists. 
	 */
	public HeatParameterConstraint getRangeConstraint(){
		HeatParameterConstraint res = null;
		if (constraints != null){
			for (HeatParameterConstraint entry : constraints){
				if (entry.getRange() != null){
					res = entry;
					break;
				}
			}
		}
		return res;
	}

	/**
	 * Get the first "allowed_values" constraint from HEAT parameter.
	 * No additional "allowed_values" constraint is searched for.
	 * 
	 * @return first "allowed_values" constraint found for this parameter,
	 * or null if no such constraint exists. 
	 */
	public HeatParameterConstraint getAllowedValuesConstraint(){
		HeatParameterConstraint res = null;
		if (constraints != null){
			for (HeatParameterConstraint entry : constraints){
				if (entry.getAllowed_values() != null){
					res = entry;
					break;
				}
			}
		}
		return res;
	}

	/**
	 * Get the "allowed_pattern" constraint list from HEAT parameter.
	 * 
	 * @return "allowed_pattern" constraint list found for this parameter,
	 * or null if no such constraint exists. 
	 */
	public List<HeatParameterConstraint> getAllowedPatternConstraint(){
		List<HeatParameterConstraint> res = null;
		if (constraints != null){
			for (HeatParameterConstraint entry : constraints){
				if (entry.getAllowed_pattern() != null){
					if (res == null){
						res = new ArrayList<>();
					}
					res.add(entry);
				}
			}
		}
		return res;
	}

	/**
	 * Get the "custom_constraint" constraint list from HEAT parameter.
	 * 
	 * @return "custom_constraint" constraint list found for this parameter,
	 * or null if no such constraint exists. 
	 */
	public List<HeatParameterConstraint> getCustomConstraintConstraint(){
		List<HeatParameterConstraint> res = null;
		if (constraints != null){
			for (HeatParameterConstraint entry : constraints){
				if (entry.getCustom_constraint() != null){
					if (res == null){
						res = new ArrayList<>();
					}
					res.add(entry);
				}
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (type != null){
			sb.append("type:"+type+", ");
		} 
		if (label != null){
			sb.append("label:"+label+", ");
		}
		if (Default != null){
			sb.append("default:"+Default+", ");
		}
		if (hidden != null){
			sb.append("hidden:"+hidden+", ");
		}
		if (constraints != null){
			sb.append("constraints:"+constraints+", ");
		}
		if (description != null){
			sb.append("description:"+description);
		}
		return sb.toString();
	}

}
