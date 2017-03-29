package org.openecomp.sdc.toscaparser.jython;

import java.util.List;
import java.util.Map;

import org.openecomp.sdc.toscaparser.jython.parameters.JyInput;

public interface JyTopologyTemplate {
    
    String getJyDescription();    
    List<JyNodeTemplate> getJyNodeTemplates();
    List<JyInput> getJyInputs();
    List<JyGroup> getJyGroups();
    JySubstitutionMappings getJySubstitutionMappings();
    Map<String, String> getJyMetadata();
}
