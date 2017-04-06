package org.openecomp.sdc.toscaparser.jython;

import java.util.List;
import java.util.Map;

public interface JyToscaTemplate {
    
    String getJyVersion();
    String getJyDescription();
    List<JyNodeTemplate> getJyNodeTemplates();
    List<JyTopologyTemplate> getJyNestedTopologyTemplates();
    JyTopologyTemplate getJyTopologyTemplate();
    Map<String, Object> getJyMetadata();
}
