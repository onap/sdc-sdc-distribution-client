package org.openecomp.sdc.toscaparser.jython;

import java.util.List;
import java.util.Map;

import org.openecomp.sdc.toscaparser.jython.elements.JyStatefulEntityType;

public interface JyEntityTemplate {
    
    String getJyName();
    String getJyDescription();
    JyStatefulEntityType getJyTypeDefinition();
    List<JyProperty> getJyProperties();
    List<JyCapability> getJyCapabilities();
    List<Map<String, Map<String, Object>>> getJyRequirements();
}
