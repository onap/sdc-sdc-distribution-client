package org.openecomp.sdc.toscaparser.api.elements.constraints;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.elements.ScalarUnit;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public abstract class Constraint {
	
	// Parent class for constraints for a Property or Input
	
	protected static final String EQUAL = "equal";
	protected static final String GREATER_THAN = "greater_than";
	protected static final String GREATER_OR_EQUAL = "greater_or_equal";
	protected static final String LESS_THAN = "less_than";
	protected static final String LESS_OR_EQUAL = "less_or_equal";
	protected static final String IN_RANGE = "in_range";
	protected static final String VALID_VALUES = "valid_values";
	protected static final String LENGTH = "length";
	protected static final String MIN_LENGTH = "min_length";
	protected static final String MAX_LENGTH = "max_length";
	protected static final String PATTERN = "pattern";

	protected static final String CONSTRAINTS[] = {
			EQUAL, GREATER_THAN,GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL, 
			IN_RANGE, VALID_VALUES, LENGTH, MIN_LENGTH, MAX_LENGTH, PATTERN};

	@SuppressWarnings("unchecked")
	public static Constraint factory(String constraintClass,String propname,String proptype,Object constraint) {

		// a factory for the different Constraint classes
		// replaces Python's __new__() usage

        if(!(constraint instanceof LinkedHashMap) || 
        		((LinkedHashMap<String,Object>)constraint).size() != 1) {
                 ThreadLocalsHolder.getCollector().appendException(
                     "InvalidSchemaError: Invalid constraint schema " + constraint.toString());
        }
        
        if(constraintClass.equals(EQUAL)) {
        	return new Equal(propname,proptype,constraint);
        }
        else if(constraintClass.equals(GREATER_THAN)) {
        	return new GreaterThan(propname,proptype,constraint);
        }
        else if(constraintClass.equals(GREATER_OR_EQUAL)) {
        	return new GreaterOrEqual(propname,proptype,constraint);
        }
        else if(constraintClass.equals(LESS_THAN)) {
        	return new LessThan(propname,proptype,constraint);
        }
        else if(constraintClass.equals(LESS_OR_EQUAL)) {
        	return new LessOrEqual(propname,proptype,constraint);
        }
        else if(constraintClass.equals(IN_RANGE)) {
        	return new InRange(propname,proptype,constraint);
        }
        else if(constraintClass.equals(VALID_VALUES)) {
        	return new ValidValues(propname,proptype,constraint);
        }
        else if(constraintClass.equals(LENGTH)) {
        	return new Length(propname,proptype,constraint);
        }
        else if(constraintClass.equals(MIN_LENGTH)) {
        	return new MinLength(propname,proptype,constraint);
        }
        else if(constraintClass.equals(MAX_LENGTH)) {
        	return new MaxLength(propname,proptype,constraint);
        }
        else if(constraintClass.equals(PATTERN)) {
        	return new Pattern(propname,proptype,constraint);
        }
        else {
            ThreadLocalsHolder.getCollector().appendException(String.format(
            		"InvalidSchemaError: Invalid property \"%s\"",constraintClass));
            return null;
        }
	}
	
	protected String constraintKey = "TBD";
	protected ArrayList<String> validTypes = new ArrayList<>();
	protected ArrayList<String> validPropTypes = new ArrayList<>();
	
	protected String propertyName;
	protected String propertyType;
	protected Object constraintValue;
	protected Object constraintValueMsg;
	protected Object valueMsg;
	
	@SuppressWarnings("unchecked")
	public Constraint(String propname,String proptype,Object constraint) {
		
		_setValues();
		
        propertyName = propname;
        propertyType = proptype;
        constraintValue = ((LinkedHashMap<String,Object>)constraint).get(constraintKey);
        constraintValueMsg = constraintValue;
        boolean bFound = false;
        for(String s: ScalarUnit.SCALAR_UNIT_TYPES) {
        	if(s.equals(propertyType)) {
        		bFound = true;
        		break;
        	}
        }
        if(bFound) {
            constraintValue = _getScalarUnitConstraintValue();
        }
        // check if constraint is valid for property type
        bFound = false;
        for(String s: validPropTypes) {
        	if(s.equals(propertyType)) {
        		bFound = true;
        		break;
        	}
        }
        if(!bFound) {
            ThreadLocalsHolder.getCollector().appendException(String.format(
            	"InvalidSchemaError: Property \"%s\" is not valid for data type \"%s\"",
            	constraintKey,propertyType));
        }
	}

	@SuppressWarnings("unchecked")
	private Object _getScalarUnitConstraintValue() {
		// code differs from Python because of class creation
		if(constraintValue instanceof ArrayList) {
			ArrayList<Object> ret = new ArrayList<>();
			for(Object v: (ArrayList<Object>)constraintValue) {
				ScalarUnit su = ScalarUnit.getScalarunitClass(propertyType,v);
				ret.add(su.getNumFromScalarUnit(null));
			}
			return ret;
		}
		else {
			ScalarUnit su = ScalarUnit.getScalarunitClass(propertyType,constraintValue);
			return su.getNumFromScalarUnit(null);
		}
	}

	public void validate(Object value) {
		valueMsg = value;
        boolean bFound = false;
        for(String s: ScalarUnit.SCALAR_UNIT_TYPES) {
        	if(s.equals(propertyType)) {
        		bFound = true;
        		break;
        	}
        }
        if(bFound) {
        	value = ScalarUnit.getScalarunitValue(propertyType,value,null);
        }
        if(!_isValid(value)) {
            ThreadLocalsHolder.getCollector().appendWarning("ValidationError: " + _errMsg(value));
        }
	}

	protected abstract boolean _isValid(Object value);
	
	protected abstract void _setValues();

	protected abstract String _errMsg(Object value);
	
}

/*python

class Constraint(object):
    '''Parent class for constraints for a Property or Input.'''

    CONSTRAINTS = (EQUAL, GREATER_THAN,
                   GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL, IN_RANGE,
                   VALID_VALUES, LENGTH, MIN_LENGTH, MAX_LENGTH, PATTERN) = \
                  ('equal', 'greater_than', 'greater_or_equal', 'less_than',
                   'less_or_equal', 'in_range', 'valid_values', 'length',
                   'min_length', 'max_length', 'pattern')

    def __new__(cls, property_name, property_type, constraint):
        if cls is not Constraint:
            return super(Constraint, cls).__new__(cls)

        if(not isinstance(constraint, collections.Mapping) or
           len(constraint) != 1):
            ExceptionCollector.appendException(
                InvalidSchemaError(message=_('Invalid constraint schema.')))

        for type in constraint.keys():
            ConstraintClass = get_constraint_class(type)
            if not ConstraintClass:
                msg = _('Invalid property "%s".') % type
                ExceptionCollector.appendException(
                    InvalidSchemaError(message=msg))

        return ConstraintClass(property_name, property_type, constraint)

    def __init__(self, property_name, property_type, constraint):
        self.property_name = property_name
        self.property_type = property_type
        self.constraint_value = constraint[self.constraint_key]
        self.constraint_value_msg = self.constraint_value
        if self.property_type in scalarunit.ScalarUnit.SCALAR_UNIT_TYPES:
            self.constraint_value = self._get_scalarunit_constraint_value()
        # check if constraint is valid for property type
        if property_type not in self.valid_prop_types:
            msg = _('Property "%(ctype)s" is not valid for data type '
                    '"%(dtype)s".') % dict(
                        ctype=self.constraint_key,
                        dtype=property_type)
            ExceptionCollector.appendException(InvalidSchemaError(message=msg))

    def _get_scalarunit_constraint_value(self):
        if self.property_type in scalarunit.ScalarUnit.SCALAR_UNIT_TYPES:
            ScalarUnit_Class = (scalarunit.
                                get_scalarunit_class(self.property_type))
        if isinstance(self.constraint_value, list):
            return [ScalarUnit_Class(v).get_num_from_scalar_unit()
                    for v in self.constraint_value]
        else:
            return (ScalarUnit_Class(self.constraint_value).
                    get_num_from_scalar_unit())

    def _err_msg(self, value):
        return _('Property "%s" could not be validated.') % self.property_name

    def validate(self, value):
        self.value_msg = value
        if self.property_type in scalarunit.ScalarUnit.SCALAR_UNIT_TYPES:
            value = scalarunit.get_scalarunit_value(self.property_type, value)
        if not self._is_valid(value):
            err_msg = self._err_msg(value)
            ExceptionCollector.appendException(
                ValidationError(message=err_msg))


*/
