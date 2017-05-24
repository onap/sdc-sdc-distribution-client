package org.openecomp.sdc.toscaparser.api.elements;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class PropertyDef {
	
	private static final String PROPERTY_KEYNAME_DEFAULT = "default";
	private static final String PROPERTY_KEYNAME_REQUIRED = "required";
	private static final String PROPERTY_KEYNAME_STATUS = "status";
	private static final String VALID_PROPERTY_KEYNAMES[] = {
			PROPERTY_KEYNAME_DEFAULT,
            PROPERTY_KEYNAME_REQUIRED,
            PROPERTY_KEYNAME_STATUS};

    private static final boolean PROPERTY_REQUIRED_DEFAULT = true;

    private static final String VALID_REQUIRED_VALUES[] = {"true", "false"};

    private static final String PROPERTY_STATUS_SUPPORTED = "supported";
	private static final String PROPERTY_STATUS_EXPERIMENTAL = "experimental";
	private static final String VALID_STATUS_VALUES[] = {
			PROPERTY_STATUS_SUPPORTED, PROPERTY_STATUS_EXPERIMENTAL};

	private static final String PROPERTY_STATUS_DEFAULT = PROPERTY_STATUS_SUPPORTED;
	
	private String name;
	private Object value;
	private LinkedHashMap<String,Object> schema;
	private String _status;
	private boolean _required;

	public PropertyDef(String pdName, Object pdValue, 
						LinkedHashMap<String,Object>  pdSchema) {
		name = pdName;
		value = pdValue;
		schema = pdSchema;
        _status = PROPERTY_STATUS_DEFAULT;
        _required = PROPERTY_REQUIRED_DEFAULT;

        if(schema != null) {
        	// Validate required 'type' property exists
        	if(schema.get("type") == null) {
        		//msg = (_('Schema definition of "%(pname)s" must have a "type" '
	            //         'attribute.') % dict(pname=self.name))
	            ThreadLocalsHolder.getCollector().appendException(String.format(
	            		"InvalidSchemaError: Schema definition of \"%s\" must have a \"type\" attribute",name));
        	}
	        _loadRequiredAttrFromSchema();
	        _loadStatusAttrFromSchema();
        }
	}

	public Object getDefault() {
		if(schema != null) {
			for(Map.Entry<String,Object> me: schema.entrySet()) {
				if(me.getKey().equals(PROPERTY_KEYNAME_DEFAULT)) {
					return me.getValue();
				}
			}
		}
		return null;
	}

	public boolean isRequired() {
		return _required;
	}

	private void _loadRequiredAttrFromSchema() {
        // IF 'required' keyname exists verify it's a boolean,
        // if so override default
		Object val = schema.get(PROPERTY_KEYNAME_REQUIRED);
		if(val != null) {
			if(val instanceof Boolean) {
				_required = (boolean)val;
			}
			else {
                //valid_values = ', '.join(self.VALID_REQUIRED_VALUES)
                //attr = self.PROPERTY_KEYNAME_REQUIRED
                //TOSCAException.generate_inv_schema_property_error(self,
                //                                                  attr,
                //                                                  value,
                //                                                  valid_values)
				ThreadLocalsHolder.getCollector().appendException(String.format(
						"Schema definition of \"%s\" has \"required\" attribute with an invalid value",
						name));
			}
		}
	}
	
	public String getStatus() {
		return _status;
	}

	private void _loadStatusAttrFromSchema() {
        // IF 'status' keyname exists verify it's a boolean,
        // if so override default
		String sts = (String)schema.get(PROPERTY_KEYNAME_STATUS);
		if(sts != null) {
			boolean bFound = false;
			for(String vsv: VALID_STATUS_VALUES) {
				if(vsv.equals(sts)) {
					bFound = true;
					break;
				}
			}
			if(bFound) {
				_status = sts;
			}
			else {
                //valid_values = ', '.join(self.VALID_STATUS_VALUES)
                //attr = self.PROPERTY_KEYNAME_STATUS
                //TOSCAException.generate_inv_schema_property_error(self,
                //                                                  attr,
                //                                                  value,
                //                                                  valid_values)
				ThreadLocalsHolder.getCollector().appendWarning(String.format(
						"Schema definition of \"%s\" has \"status\" attribute with an invalid value",
						name));
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public LinkedHashMap<String,Object> getSchema() {
		return schema;
	}
	
	public Object getPDValue() {
		// there's getValue in EntityType...
		return value;
	}
	
}
/*python

from toscaparser.common.exception import ExceptionCollector
from toscaparser.common.exception import InvalidSchemaError
from toscaparser.common.exception import TOSCAException
from toscaparser.utils.gettextutils import _


class PropertyDef(object):
    '''TOSCA built-in Property type.'''

    VALID_PROPERTY_KEYNAMES = (PROPERTY_KEYNAME_DEFAULT,
                               PROPERTY_KEYNAME_REQUIRED,
                               PROPERTY_KEYNAME_STATUS) = \
        ('default', 'required', 'status')

    PROPERTY_REQUIRED_DEFAULT = True

    VALID_REQUIRED_VALUES = ['true', 'false']
    VALID_STATUS_VALUES = (PROPERTY_STATUS_SUPPORTED,
                           PROPERTY_STATUS_EXPERIMENTAL) = \
        ('supported', 'experimental')

    PROPERTY_STATUS_DEFAULT = PROPERTY_STATUS_SUPPORTED

    def __init__(self, name, value=None, schema=None):
        self.name = name
        self.value = value
        self.schema = schema
        self._status = self.PROPERTY_STATUS_DEFAULT
        self._required = self.PROPERTY_REQUIRED_DEFAULT

        # Validate required 'type' property exists
        try:
            self.schema['type']
        except KeyError:
            msg = (_('Schema definition of "%(pname)s" must have a "type" '
                     'attribute.') % dict(pname=self.name))
            ExceptionCollector.appendException(
                InvalidSchemaError(message=msg))

        if self.schema:
            self._load_required_attr_from_schema()
            self._load_status_attr_from_schema()

    @property
    def default(self):
        if self.schema:
            for prop_key, prop_value in self.schema.items():
                if prop_key == self.PROPERTY_KEYNAME_DEFAULT:
                    return prop_value
        return None

    @property
    def required(self):
        return self._required

    def _load_required_attr_from_schema(self):
        # IF 'required' keyname exists verify it's a boolean,
        # if so override default
        if self.PROPERTY_KEYNAME_REQUIRED in self.schema:
            value = self.schema[self.PROPERTY_KEYNAME_REQUIRED]
            if isinstance(value, bool):
                self._required = value
            else:
                valid_values = ', '.join(self.VALID_REQUIRED_VALUES)
                attr = self.PROPERTY_KEYNAME_REQUIRED
                TOSCAException.generate_inv_schema_property_error(self,
                                                                  attr,
                                                                  value,
                                                                  valid_values)

    @property
    def status(self):
        return self._status

    def _load_status_attr_from_schema(self):
        # IF 'status' keyname exists verify it's a valid value,
        # if so override default
        if self.PROPERTY_KEYNAME_STATUS in self.schema:
            value = self.schema[self.PROPERTY_KEYNAME_STATUS]
            if value in self.VALID_STATUS_VALUES:
                self._status = value
            else:
                valid_values = ', '.join(self.VALID_STATUS_VALUES)
                attr = self.PROPERTY_KEYNAME_STATUS
                TOSCAException.generate_inv_schema_property_error(self,
                                                                  attr,
                                                                  value,
                                                                  valid_values)
*/
