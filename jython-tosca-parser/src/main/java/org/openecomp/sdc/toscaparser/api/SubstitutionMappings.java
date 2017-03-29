package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.elements.NodeType;
import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.openecomp.sdc.toscaparser.jython.JySubstitutionMappings;

import com.google.common.base.MoreObjects;

public class SubstitutionMappings {

    private final JySubstitutionMappings jySubstitutionMappings;

    public SubstitutionMappings(JySubstitutionMappings jySubstitutionMappings) {
        this.jySubstitutionMappings = Objects.requireNonNull(jySubstitutionMappings);
    }

    public List<NodeTemplate> getNodeTemplates() {
        return jySubstitutionMappings.getJyNodeTemplates()
                .stream()
                .map(NodeTemplate::new)
                .collect(toImmutableList());
    }

    public List<Input> getInputs() {
        return jySubstitutionMappings.getJyInputs()
                .stream()
                .map(Input::new)
                .collect(toImmutableList());
    }

    public NodeType getNodeDefinition() {
        return new NodeType(jySubstitutionMappings.getJyNodeDefinition());
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeTemplates", getNodeTemplates())
                .add("inputs", getInputs())
                .add("nodeDefinition", getNodeDefinition())
                .toString();
    }    
}
