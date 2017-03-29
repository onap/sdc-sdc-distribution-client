package org.openecomp.sdc.toscaparser.api.elements;

import java.util.Objects;

import org.openecomp.sdc.toscaparser.jython.elements.JyStatefulEntityType;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public abstract class StatefulEntityType {
    
    private final JyStatefulEntityType jyStatefulEntityType;
    
    public StatefulEntityType(JyStatefulEntityType jyStatefulEntityType) {
        this.jyStatefulEntityType = Objects.requireNonNull(jyStatefulEntityType);
    }

    public String getType() {
        return jyStatefulEntityType.getJyType();
    }
    
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("type", getType());
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }    
}
