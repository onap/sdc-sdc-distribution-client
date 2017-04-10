package org.openecomp.sdc.toscaparser.jython;

import java.util.List;
import java.util.Map;

import org.openecomp.sdc.toscaparser.jython.elements.JyNodeType;
import org.openecomp.sdc.toscaparser.jython.parameters.JyInput;

public interface JySubstitutionMappings {
    
    List<JyNodeTemplate> getJyNodeTemplates();
    List<JyInput> getJyInputs();
    List<JyGroup> getJyGroups();
    JyNodeType getJyNodeDefinition();
    Map<String, Object> getJyMetadata();
}
