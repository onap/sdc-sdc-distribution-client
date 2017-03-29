package org.openecomp.sdc.toscaparser.api;

import org.openecomp.sdc.toscaparser.api.elements.GroupType;
import org.openecomp.sdc.toscaparser.api.elements.NodeType;
import org.openecomp.sdc.toscaparser.api.elements.StatefulEntityType;
import org.openecomp.sdc.toscaparser.jython.elements.JyGroupType;
import org.openecomp.sdc.toscaparser.jython.elements.JyNodeType;
import org.openecomp.sdc.toscaparser.jython.elements.JyStatefulEntityType;

public class StatefulEntityTypeFactory {
    
    public StatefulEntityType create(JyStatefulEntityType jyStatefulEntityType) {
        String jyClassName = jyStatefulEntityType.getJyClassName();
        StatefulEntityType statefulEntityType;
        switch (jyClassName) {
            case "NodeType":
                statefulEntityType = new NodeType((JyNodeType) jyStatefulEntityType);
                break;
            case "GroupType":
                statefulEntityType = new GroupType((JyGroupType) jyStatefulEntityType);
                break;
            default: 
                throw new UnsupportedOperationException(jyClassName + " is not supported!");
        }
        
        return statefulEntityType;
    }
}