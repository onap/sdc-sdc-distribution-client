package org.openecomp.sdc.toscaparser.api;

import java.util.List;
import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.parameters.Input;
import org.openecomp.sdc.toscaparser.jython.JyToscaTemplate;

import com.google.common.base.MoreObjects;

public class ToscaTemplate {

    private final JyToscaTemplate jyToscaTemplate;
    private final TopologyTemplate topologyTemplate;

    public ToscaTemplate(JyToscaTemplate jyToscaTemplate, TopologyTemplate topologyTemplate) {
        this.jyToscaTemplate = Objects.requireNonNull(jyToscaTemplate);
        this.topologyTemplate = Objects.requireNonNull(topologyTemplate);
    }

    public String getVersion() {
        return jyToscaTemplate.getJyVersion();
    }

    public String getDescription() {
        return jyToscaTemplate.getJyDescription();
    }
    
    public TopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    public List<NodeTemplate> getNodeTemplates() {
        return topologyTemplate.getNodeTemplates();
    }
    
    public List<Input> getInputs() {
        return topologyTemplate.getInputs();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("version", getVersion())
                .add("description", getDescription())
                .add("topologyTemplate", topologyTemplate)
                .toString();
    }
}