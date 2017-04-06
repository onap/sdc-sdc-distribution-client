package org.openecomp.sdc.toscaparser.api;

import java.util.List;

import org.openecomp.sdc.toscaparser.jython.JyGroup;

import com.google.common.base.MoreObjects.ToStringHelper;

public class Group extends EntityTemplate {
    
    private final JyGroup jyGroup;
	
    public Group(JyGroup jyGroup) {
        super(jyGroup);
        this.jyGroup = jyGroup;
    }
    
    public List<String> getMembers(){
    	return jyGroup.getJyMembers();
    }
    
    public Metadata getMetadata(){
    	return jyGroup.getJyMetadata() != null ? new Metadata(jyGroup.getJyMetadata()) : null;
    }
    
    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("members", getMembers())
                .add("metadata", getMetadata());
    }   
}
