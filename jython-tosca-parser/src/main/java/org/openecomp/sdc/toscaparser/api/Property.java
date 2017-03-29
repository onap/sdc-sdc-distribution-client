package org.openecomp.sdc.toscaparser.api;

import java.util.Objects;

import org.openecomp.sdc.toscaparser.jython.JyProperty;
import org.openecomp.sdc.toscaparser.utils.PythonUtils;

import com.google.common.base.MoreObjects;

public class Property {
    
    private final JyProperty jyProperty;
    
    public Property(JyProperty jyProperty) {
        this.jyProperty = Objects.requireNonNull(jyProperty);
    }
    
    public String getName() {
        return jyProperty.getJyName();
    }
    
    public Object getValue() {
        return PythonUtils.cast(jyProperty.getJyValue(), jyProperty.getJyValueClassName());
    }
    
    public String getType() {
        return jyProperty.getJyType();
    }
    
    public boolean isRequired() {
        return jyProperty.isJyRequired();
    }
    
    public String getDescription() {
        return jyProperty.getJyDescription();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("type", getType())
                .add("required", isRequired())
                .add("description", getDescription())
                .add("value", getValue())
                .toString();
    }
}
