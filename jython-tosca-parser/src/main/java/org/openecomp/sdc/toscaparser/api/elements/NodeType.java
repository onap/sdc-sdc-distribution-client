package org.openecomp.sdc.toscaparser.api.elements;

import java.util.List;

import org.openecomp.sdc.toscaparser.jython.elements.JyNodeType;

import com.google.common.base.MoreObjects.ToStringHelper;

public class NodeType extends StatefulEntityType {
    
    private final JyNodeType jyNodeType;

    public NodeType(JyNodeType jyNodeType) {
        super(jyNodeType);
        this.jyNodeType = jyNodeType;
    }
    
    public List<?> getRequirements() {
        return jyNodeType.getJyRequirements();
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("requirements", getRequirements());
    }    
}
