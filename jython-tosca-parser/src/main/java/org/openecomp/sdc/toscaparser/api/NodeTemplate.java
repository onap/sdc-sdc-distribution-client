package org.openecomp.sdc.toscaparser.api;

import java.util.Map;

import org.openecomp.sdc.toscaparser.jython.JyNodeTemplate;

import com.google.common.base.MoreObjects.ToStringHelper;

public class NodeTemplate extends EntityTemplate {

    private final JyNodeTemplate jyNodeTemplate;

    public NodeTemplate(JyNodeTemplate jyNodeTemplate) {
        super(jyNodeTemplate);
        this.jyNodeTemplate = jyNodeTemplate;
    }
    
    public Map<String, String> getMetadata() {
        return jyNodeTemplate.getJyMetadata();
    }
    
    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("metadata", getMetadata());
    }    
}
