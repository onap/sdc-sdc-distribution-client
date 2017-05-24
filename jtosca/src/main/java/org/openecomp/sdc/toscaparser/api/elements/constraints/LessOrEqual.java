package org.openecomp.sdc.toscaparser.api.elements.constraints;

import java.util.Date;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class LessOrEqual extends Constraint {
	// Constraint class for "less_or_equal"

	// Constrains a property or parameter to a value less than or equal
	// to ('<=') the value declared.

	protected void _setValues() {

		constraintKey = LESS_OR_EQUAL;

		validTypes.add("Integer");
		validTypes.add("Double");
		validTypes.add("Float");
		// timestamps are loaded as Date objects
		validTypes.add("Date");
		//validTypes.add("datetime.date");
		//validTypes.add("datetime.time");
		//validTypes.add("datetime.datetime");
		
		validPropTypes.add(Schema.INTEGER);
		validPropTypes.add(Schema.FLOAT);
		validPropTypes.add(Schema.TIMESTAMP);
		validPropTypes.add(Schema.SCALAR_UNIT_SIZE);
		validPropTypes.add(Schema.SCALAR_UNIT_FREQUENCY);
		validPropTypes.add(Schema.SCALAR_UNIT_TIME);
		
	}
	
	public LessOrEqual(String name,String type,Object c) {
		super(name,type,c);
		
		if(!validTypes.contains(constraintValue.getClass().getSimpleName())) {
	        ThreadLocalsHolder.getCollector().appendException("InvalidSchemaError: The property \"less_or_equal\" expects comparable values");
		}
	}
	
	@Override
	protected boolean _isValid(Object value) {

		// timestamps
	    if(value instanceof Date) {
	    	if(constraintValue instanceof Date) {
	    		return !((Date)value).after((Date)constraintValue);
	    	}
	    	return false;
	    }
	    
		Double n1 = new Double(value.toString());
		Double n2 = new Double(constraintValue.toString());
		return n1 <= n2;
	}

	@Override
	protected String _errMsg(Object value) {
	    return String.format("The value \"%s\" of property \"%s\" must be less or equal to \"%s\"",
	    		valueMsg,propertyName,constraintValueMsg);
	}

}

/*python

class LessOrEqual(Constraint):
    """Constraint class for "less_or_equal"

    Constrains a property or parameter to a value less than or equal
    to ('<=') the value declared.
    """

    constraint_key = Constraint.LESS_OR_EQUAL

    valid_types = (int, float, datetime.date,
                   datetime.time, datetime.datetime)

    valid_prop_types = (Schema.INTEGER, Schema.FLOAT, Schema.TIMESTAMP,
                        Schema.SCALAR_UNIT_SIZE, Schema.SCALAR_UNIT_FREQUENCY,
                        Schema.SCALAR_UNIT_TIME)

    def __init__(self, property_name, property_type, constraint):
        super(LessOrEqual, self).__init__(property_name, property_type,
                                          constraint)
        if not isinstance(self.constraint_value, self.valid_types):
            ExceptionCollector.appendException(
                InvalidSchemaError(message=_('The property "less_or_equal" '
                                             'expects comparable values.')))

    def _is_valid(self, value):
        if value <= self.constraint_value:
            return True

        return False

    def _err_msg(self, value):
        return (_('The value "%(pvalue)s" of property "%(pname)s" must be '
                  'less than or equal to "%(cvalue)s".') %
                dict(pname=self.property_name,
                     pvalue=self.value_msg,
                     cvalue=self.constraint_value_msg))
*/