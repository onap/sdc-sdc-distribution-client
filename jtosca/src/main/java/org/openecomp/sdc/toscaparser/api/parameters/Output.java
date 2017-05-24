package org.openecomp.sdc.toscaparser.api.parameters;

import java.util.LinkedHashMap;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class Output {
	
	private static final String DESCRIPTION = "description";
	public static final String VALUE = "value";
	private static final String OUTPUTFIELD[] = {DESCRIPTION, VALUE};
	
	private String name;
	private LinkedHashMap<String,Object> attrs;//TYPE???
	
	public Output(String oname,LinkedHashMap<String,Object> oattrs) {
		name = oname;
		attrs = oattrs;
	}
	
	public String getDescription() {
		return (String)attrs.get(DESCRIPTION);
	}

	public Object getValue() {
		return attrs.get(VALUE);
	}
	
	public void validate() {
		_validateField();
	}
	
	private void _validateField() {
		if(!(attrs instanceof LinkedHashMap)) {
			//TODO wrong error message...
            ThreadLocalsHolder.getCollector().appendException(String.format(
                    "ValidationError: Output \"%s\" has wrong type. Expecting a dict",
                    name));
		}
		
		if(getValue() == null) {
            ThreadLocalsHolder.getCollector().appendException(String.format(
                    "MissingRequiredFieldError: Output \"%s\" is missing required \"%s\"",
                    name,VALUE));
		}
        for(String key: attrs.keySet()) {
    		boolean bFound = false;
    		for(String of: OUTPUTFIELD) {
    			if(key.equals(of)) {
    				bFound = true;
    				break;
    			}
    		}
    		if(!bFound) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                    "UnknownFieldError: Output \"%s\" contains unknown field \"%s\"",
                    name,key));
            }
        }
	}
	
	// getter/setter
	
	public String getName() {
		return name;
	}
	
	public void setAttr(String name,Object value) {
		attrs.put(name, value);
	}
}

/*python

class Output(object):

    OUTPUTFIELD = (DESCRIPTION, VALUE) = ('description', 'value')

    def __init__(self, name, attrs):
        self.name = name
        self.attrs = attrs

    @property
    def description(self):
        return self.attrs.get(self.DESCRIPTION)

    @property
    def value(self):
        return self.attrs.get(self.VALUE)

    def validate(self):
        self._validate_field()

    def _validate_field(self):
        if not isinstance(self.attrs, dict):
            ExceptionCollector.appendException(
                MissingRequiredFieldError(what='Output "%s"' % self.name,
                                          required=self.VALUE))
        if self.value is None:
            ExceptionCollector.appendException(
                MissingRequiredFieldError(what='Output "%s"' % self.name,
                                          required=self.VALUE))
        for name in self.attrs:
            if name not in self.OUTPUTFIELD:
                ExceptionCollector.appendException(
                    UnknownFieldError(what='Output "%s"' % self.name,
                                      field=name))
*/
