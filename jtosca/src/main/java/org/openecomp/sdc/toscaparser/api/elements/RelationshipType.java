package org.openecomp.sdc.toscaparser.api.elements;

import java.util.LinkedHashMap;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.elements.EntityType;
import org.openecomp.sdc.toscaparser.api.elements.StatefulEntityType;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class RelationshipType extends StatefulEntityType {

	private static final String DERIVED_FROM = "derived_from";
	private static final String VALID_TARGET_TYPES = "valid_target_types";
	private static final String INTERFACES = "interfaces";
	private static final String ATTRIBUTES = "attributes";
	private static final String PROPERTIES = "properties";
	private static final String DESCRIPTION = "description";
	private static final String VERSION = "version";
	private static final String CREDENTIAL = "credential";
	
	private static final String SECTIONS[] = {
			DERIVED_FROM, VALID_TARGET_TYPES, INTERFACES, 
			ATTRIBUTES, PROPERTIES, DESCRIPTION, VERSION, CREDENTIAL};
	
	private String capabilityName;
	private LinkedHashMap<String,Object> customDef;

	public RelationshipType(String _type, String _capabilityName, LinkedHashMap<String,Object> _customDef) {
		super(_type,RELATIONSHIP_PREFIX,_customDef);
		capabilityName = _capabilityName;
		customDef = _customDef;
	}
	
	public RelationshipType getParentType() {
        // Return a relationship this reletionship is derived from.'''
        String prel = derivedFrom(defs);
        if(prel != null) {
            return new RelationshipType(prel,null,customDef);
        }
        return null;
	}
	
	public Object getValidTargetTypes() {
		return entityValue(defs,"valid_target_types");
	}
	
	private void _validateKeys() {
        for(String key:  defs.keySet()) {
        	boolean bFound = false;
        	for(int i=0; i< SECTIONS.length; i++) {
        		if(key.equals(SECTIONS[i])) {
        			bFound = true;
        			break;
        		}
        	}
        	if(!bFound) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                        "UnknownFieldError: Relationshiptype \"%s\" has unknown field \"%s\"",type,key));
        	}
        }
	}
}

/*python

from toscaparser.common.exception import ExceptionCollector
from toscaparser.common.exception import UnknownFieldError
from toscaparser.elements.statefulentitytype import StatefulEntityType


class RelationshipType(StatefulEntityType):
    '''TOSCA built-in relationship type.'''
    SECTIONS = (DERIVED_FROM, VALID_TARGET_TYPES, INTERFACES,
                ATTRIBUTES, PROPERTIES, DESCRIPTION, VERSION,
                CREDENTIAL) = ('derived_from', 'valid_target_types',
                               'interfaces', 'attributes', 'properties',
                               'description', 'version', 'credential')

    def __init__(self, type, capability_name=None, custom_def=None):
        super(RelationshipType, self).__init__(type, self.RELATIONSHIP_PREFIX,
                                               custom_def)
        self.capability_name = capability_name
        self.custom_def = custom_def
        self._validate_keys()

    @property
    def parent_type(self):
        '''Return a relationship this reletionship is derived from.'''
        prel = self.derived_from(self.defs)
        if prel:
            return RelationshipType(prel, self.custom_def)

    @property
    def valid_target_types(self):
        return self.entity_value(self.defs, 'valid_target_types')

    def _validate_keys(self):
        for key in self.defs.keys():
            if key not in self.SECTIONS:
                ExceptionCollector.appendException(
                    UnknownFieldError(what='Relationshiptype "%s"' % self.type,
                                      field=key))
*/