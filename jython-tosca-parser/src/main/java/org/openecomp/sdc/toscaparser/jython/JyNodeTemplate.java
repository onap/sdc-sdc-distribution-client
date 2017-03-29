package org.openecomp.sdc.toscaparser.jython;

import java.util.Map;

public interface JyNodeTemplate extends JyEntityTemplate {
    
    Map<String, String> getJyMetadata();
}
