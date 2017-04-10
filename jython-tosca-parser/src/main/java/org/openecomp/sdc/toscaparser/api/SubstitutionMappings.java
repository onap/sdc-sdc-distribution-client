package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.elements.NodeType;
import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.openecomp.sdc.toscaparser.jython.JyGroup;
import org.openecomp.sdc.toscaparser.jython.JyNodeTemplate;
import org.openecomp.sdc.toscaparser.jython.JySubstitutionMappings;
import org.openecomp.sdc.toscaparser.jython.parameters.JyInput;

import com.google.common.base.MoreObjects;

public class SubstitutionMappings {

    private final JySubstitutionMappings jySubstitutionMappings;

    public SubstitutionMappings(JySubstitutionMappings jySubstitutionMappings) {
        this.jySubstitutionMappings = Objects.requireNonNull(jySubstitutionMappings);
    }

    public List<NodeTemplate> getNodeTemplates() {
        List<JyNodeTemplate> jyNodeTemplates = jySubstitutionMappings.getJyNodeTemplates();
		return jyNodeTemplates != null ? jyNodeTemplates
                .stream()
                .map(NodeTemplate::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }
    
    public List<Group> getGroups() {
        List<JyGroup> jyGroups = jySubstitutionMappings.getJyGroups();
		return jyGroups != null ? jyGroups
                .stream()
                .map(Group::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }

    public List<Input> getInputs() {
        List<JyInput> jyInputs = jySubstitutionMappings.getJyInputs();
		return jyInputs != null ? jyInputs
                .stream()
                .map(Input::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }

    public NodeType getNodeDefinition() {
        return new NodeType(jySubstitutionMappings.getJyNodeDefinition());
    }
    
    public Metadata getMetadata(){
    	return jySubstitutionMappings.getJyMetadata() != null ? new Metadata(jySubstitutionMappings.getJyMetadata()) : null;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeTemplates", getNodeTemplates())
                .add("inputs", getInputs())
                .add("nodeDefinition", getNodeDefinition())
                .add("metadata", getMetadata())
                .toString();
    }    
}
