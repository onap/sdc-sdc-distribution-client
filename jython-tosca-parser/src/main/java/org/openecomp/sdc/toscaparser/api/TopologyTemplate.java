package org.openecomp.sdc.toscaparser.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.openecomp.sdc.toscaparser.jython.JySubstitutionMappings;
import org.openecomp.sdc.toscaparser.jython.JyTopologyTemplate;

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
        return jyTopologyTemplate.getJyNodeTemplates()
                .stream()
                .map(NodeTemplate::new)
                .collect(toImmutableList());
    }

    public List<Input> getInputs() {
        return jyTopologyTemplate.getJyInputs()
                .stream()
                .map(Input::new)
                .collect(toImmutableList());
    }
    
    public List<Group> getGroups() {
        return jyTopologyTemplate.getJyGroups()
                .stream()
                .map(Group::new)
                .collect(toImmutableList());
    }
    
    public SubstitutionMappings getSubstitutionMappings() {
        JySubstitutionMappings jySubstitutionMappings = jyTopologyTemplate.getJySubstitutionMappings();
        return jySubstitutionMappings != null ? new SubstitutionMappings(jySubstitutionMappings) : null;
    }
    
    public Map<String, String> getMetadata() {
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
