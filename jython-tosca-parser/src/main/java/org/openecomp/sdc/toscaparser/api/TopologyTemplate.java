package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.openecomp.sdc.toscaparser.jython.JyGroup;
import org.openecomp.sdc.toscaparser.jython.JyNodeTemplate;
import org.openecomp.sdc.toscaparser.jython.JySubstitutionMappings;
import org.openecomp.sdc.toscaparser.jython.JyTopologyTemplate;
import org.openecomp.sdc.toscaparser.jython.parameters.JyInput;

import com.google.common.base.MoreObjects;

public class TopologyTemplate {

    private final JyTopologyTemplate jyTopologyTemplate;

    public TopologyTemplate(JyTopologyTemplate jyTopologyTemplate) {
        this.jyTopologyTemplate = Objects.requireNonNull(jyTopologyTemplate);        
    }

    public String getDescription() {
        return jyTopologyTemplate.getJyDescription();
    }

    public List<NodeTemplate> getNodeTemplates() {
        List<JyNodeTemplate> jyNodeTemplates = jyTopologyTemplate.getJyNodeTemplates();
		return jyNodeTemplates != null ? jyNodeTemplates
                .stream()
                .map(NodeTemplate::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }

    public List<Input> getInputs() {
        List<JyInput> jyInputs = jyTopologyTemplate.getJyInputs();
		return jyInputs != null ? jyInputs
                .stream()
                .map(Input::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }
    
    public List<Group> getGroups() {
        List<JyGroup> jyGroups = jyTopologyTemplate.getJyGroups();
		return jyGroups != null ? jyGroups
                .stream()
                .map(Group::new)
                .collect(toImmutableList()) : new ArrayList<>();
    }
    
    public SubstitutionMappings getSubstitutionMappings() {
        JySubstitutionMappings jySubstitutionMappings = jyTopologyTemplate.getJySubstitutionMappings();
        return jySubstitutionMappings != null ? new SubstitutionMappings(jySubstitutionMappings) : null;
    }
    
    public Metadata getMetadata() {
        return jyTopologyTemplate.getJyMetadata();
    }    

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("description", getDescription())
                .add("inputs", getInputs())
                .add("nodeTemplates", getNodeTemplates())
                .add("groups", getGroups())
                .add("substitutionMappings", getSubstitutionMappings())
                .add("metadata", getMetadata())
                .toString();
    }
}
