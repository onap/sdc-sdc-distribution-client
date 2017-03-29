package org.openecomp.sdc.toscaparser.api;

import java.util.Objects;

import org.openecomp.sdc.toscaparser.jython.JyToscaTemplate;

public class ToscaTemplateFactory {
    
    public ToscaTemplate create(JyToscaTemplate jyToscaTemplate) {
        Objects.requireNonNull(jyToscaTemplate);
        TopologyTemplate topologyTemplate = new TopologyTemplate(jyToscaTemplate.getJyTopologyTemplate());
        return new ToscaTemplate(jyToscaTemplate, topologyTemplate);
    }
}