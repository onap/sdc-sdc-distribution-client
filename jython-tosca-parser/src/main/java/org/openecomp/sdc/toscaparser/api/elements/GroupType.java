package org.openecomp.sdc.toscaparser.api.elements;

import org.openecomp.sdc.toscaparser.jython.elements.JyGroupType;

import com.google.common.base.MoreObjects.ToStringHelper;

public class GroupType extends StatefulEntityType {

    private final JyGroupType jyGroupType;

    public GroupType(JyGroupType jyGroupType) {
        super(jyGroupType);
        this.jyGroupType = jyGroupType;
    }

    public String getVersion() {
        return jyGroupType.getJyVersion();
    }

    public String getDescription() {
        return jyGroupType.getJyDescription();
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("version", getVersion())
                .add("description", getDescription());
    }
}
