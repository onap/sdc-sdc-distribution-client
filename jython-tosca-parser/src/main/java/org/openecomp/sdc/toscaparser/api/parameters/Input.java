package org.openecomp.sdc.toscaparser.api.parameters;

import java.util.Objects;

import org.openecomp.sdc.toscaparser.jython.parameters.JyInput;

import com.google.common.base.MoreObjects;

public class Input {
    
    private final JyInput jyInput;

    public Input(JyInput jyInput) {
        this.jyInput = Objects.requireNonNull(jyInput);
    }
    
    public String getName() {
        return jyInput.getJyName();
    }
    
    public String getType() {
        return jyInput.getJyType();
    }
    
    public boolean isRequired() {
        return jyInput.isJyRequired();
    }
    
    public String getDescription() {
        return jyInput.getJyDescription();
    }
    
    public Object getDefault() {
        return jyInput.getJyDefault();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("type", getType())
                .add("required", isRequired())
                .add("description", getDescription())
                .add("default", getDefault())
                .toString();
    }    
}
