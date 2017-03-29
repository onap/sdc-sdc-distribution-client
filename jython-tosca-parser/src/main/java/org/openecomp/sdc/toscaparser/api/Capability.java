package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.jython.JyCapability;

import com.google.common.base.MoreObjects;

public class Capability {
    
    private final JyCapability jyCapability;
    
    public Capability(JyCapability jyCapability) {
        this.jyCapability = Objects.requireNonNull(jyCapability);
    }
    
    public String getName() {
        return jyCapability.getJyName();
    }
    
    public List<Property> getProperties() {
        return jyCapability.getJyProperties()
                .stream()
                .map(Property::new)
                .collect(toImmutableList());
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("properties", getProperties())
                .toString();
    }
}
