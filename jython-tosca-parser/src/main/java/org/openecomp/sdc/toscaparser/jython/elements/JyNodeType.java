package org.openecomp.sdc.toscaparser.jython.elements;

import java.util.List;

public interface JyNodeType extends JyStatefulEntityType {
    
    List<?> getJyRequirements();
}
