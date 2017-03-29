package org.openecomp.sdc.toscaparser.jython;

import java.util.List;

public interface JyCapability {
    
    String getJyName();
    List<JyProperty> getJyProperties();
}
