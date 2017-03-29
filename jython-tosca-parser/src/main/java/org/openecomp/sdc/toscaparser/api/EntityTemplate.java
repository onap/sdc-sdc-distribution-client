package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.elements.StatefulEntityType;
import org.openecomp.sdc.toscaparser.jython.JyEntityTemplate;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public abstract class EntityTemplate {

    private final JyEntityTemplate jyEntityTemplate;
    private final StatefulEntityType statefulEntityType;

    public EntityTemplate(JyEntityTemplate jyEntityTemplate) {
        this.jyEntityTemplate = Objects.requireNonNull(jyEntityTemplate);
        StatefulEntityTypeFactory statefulEntityTypeFactory = new StatefulEntityTypeFactory();
        statefulEntityType = statefulEntityTypeFactory.create(jyEntityTemplate.getJyTypeDefinition());
    }

    public String getName() {
        return jyEntityTemplate.getJyName();
    }
    
    public String getDescription() {
        return jyEntityTemplate.getJyDescription();
    }

    public StatefulEntityType getTypeDefinition() {
        return statefulEntityType;
    }

    public List<Property> getProperties() {
        return jyEntityTemplate.getJyProperties()
                .stream()
                .map(Property::new)
                .collect(toImmutableList());
    }

    public List<Capability> getCapabilities() {
        return jyEntityTemplate.getJyCapabilities()
                .stream()
                .map(Capability::new)
                .collect(toImmutableList());
    }
    
    public List<Map<String, Map<String, Object>>> getRequirements() {
        return jyEntityTemplate.getJyRequirements();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("description", getDescription())
                .add("typeDefinition", getTypeDefinition())
                .add("properties", getProperties())
                .add("capabilities", getCapabilities())
                .add("requirements", getRequirements());
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
