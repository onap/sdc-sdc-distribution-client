package org.openecomp.sdc.toscaparser.api;

import org.openecomp.sdc.toscaparser.jython.JyNodeTemplate;
import org.openecomp.sdc.toscaparser.jython.JySubstitutionMappings;

import com.google.common.base.MoreObjects.ToStringHelper;

public class NodeTemplate extends EntityTemplate {

    private final JyNodeTemplate jyNodeTemplate;

    public NodeTemplate(JyNodeTemplate jyNodeTemplate) {
        super(jyNodeTemplate);
        this.jyNodeTemplate = jyNodeTemplate;
    }
    
    public Metadata getMetadata() {
    	return jyNodeTemplate.getJyMetadata() != null ? new Metadata(jyNodeTemplate.getJyMetadata()) : null;
    }
    
    public SubstitutionMappings getSubstitutionMappings(){
    	JySubstitutionMappings jySubstitutionMappings = jyNodeTemplate.getJySubstitutionMappings();
		return jySubstitutionMappings != null ? new SubstitutionMappings(jySubstitutionMappings) : null;
    }
    
    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("metadata", getMetadata())
                .add("substitutionMappings", getSubstitutionMappings());
    }    
}
