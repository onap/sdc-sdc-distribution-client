package org.openecomp.sdc.toscaparser.jython;

import java.util.List;

public interface JyToscaTemplate {
    
    String getJyVersion();
    String getJyDescription();
    List<JyNodeTemplate> getJyNodeTemplates();
    List<JyTopologyTemplate> getNestedTopologyTemplates();
    JyTopologyTemplate getJyTopologyTemplate();
}
