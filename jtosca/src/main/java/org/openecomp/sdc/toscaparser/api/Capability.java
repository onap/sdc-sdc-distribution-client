package org.openecomp.sdc.toscaparser.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openecomp.sdc.toscaparser.api.elements.CapabilityTypeDef;
import org.openecomp.sdc.toscaparser.api.elements.PropertyDef;

public class Capability {
	
	private String name;
	private LinkedHashMap<String,Object> _properties;
	private CapabilityTypeDef _definition;

	public Capability(String cname, 
				 	  LinkedHashMap<String,Object> cproperties,
				 	 CapabilityTypeDef cdefinition) {
		name = cname;
		_properties = cproperties;
		_definition = cdefinition;
	}
	
	public ArrayList<Property> getPropertiesObjects() {
		// Return a list of property objects
		ArrayList<Property> properties = new ArrayList<Property>();
		LinkedHashMap<String,Object> props = _properties;
		if(props != null) {
			for(Map.Entry<String,Object> me: props.entrySet()) {
				String pname = me.getKey();
				Object pvalue = me.getValue();
				
				LinkedHashMap<String,PropertyDef> propsDef = _definition.getPropertiesDef();
				if(propsDef != null) {
					PropertyDef pd = (PropertyDef)propsDef.get(pname);
					if(pd != null) {
						properties.add(new Property(pname,pvalue,pd.getSchema(),null));
					}
				}
			}
		}
		return properties;
	}
	
	public LinkedHashMap<String,Property> getProperties() {
        // Return a dictionary of property name-object pairs
		LinkedHashMap<String,Property> npps = new LinkedHashMap<>();
		for(Property p: getPropertiesObjects()) {
			npps.put(p.getName(),p);
		}
		return npps;
	}

	public Object getPropertyValue(String pname) {
        // Return the value of a given property name
		LinkedHashMap<String,Property> props = getProperties();
        if(props != null && props.get(pname) != null) {
            return props.get(name).getValue();
        }
        return null;
	}

	 public String getName() {
		 return name;
	 }
	 
	 public CapabilityTypeDef getDefinition() {
		 return _definition;
	 }
	 
	 // setter
	 public void setProperty(String pname,Object pvalue) {
		 _properties.put(pname,pvalue);
	 }

    @Override
    public String toString() {
        return "Capability{" +
                "name='" + name + '\'' +
                ", _properties=" + _properties +
                ", _definition=" + _definition +
                '}';
    }
}

/*python

from toscaparser.properties import Property


class Capability(object):
    '''TOSCA built-in capabilities type.'''

    def __init__(self, name, properties, definition):
        self.name = name
        self._properties = properties
        self.definition = definition

    def get_properties_objects(self):
        '''Return a list of property objects.'''
        properties = []
        props = self._properties
        if props:
            for name, value in props.items():
                props_def = self.definition.get_properties_def()
                if props_def and name in props_def:
                    properties.append(Property(name, value,
                                               props_def[name].schema))
        return properties

    def get_properties(self):
        '''Return a dictionary of property name-object pairs.'''
        return {prop.name: prop
                for prop in self.get_properties_objects()}

    def get_property_value(self, name):
        '''Return the value of a given property name.'''
        props = self.get_properties()
        if props and name in props:
            return props[name].value
*/ 
