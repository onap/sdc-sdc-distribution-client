package org.openecomp.sdc.toscaparser.api;

import java.util.Map;

import com.google.common.base.MoreObjects;

public class Metadata {
	
	private final Map<String, Object> metadataMap;

	public Metadata(Map<String, Object> metadataMap) {
        this.metadataMap = metadataMap;
    }
	
	public String getValue(String key)  {
		return !isEmpty() ? String.valueOf(this.metadataMap.get(key)) : null;
	}

	private boolean isEmpty() {
		return this.metadataMap == null || this.metadataMap.size() == 0;
	}

    @Override
    public String toString() {
    	return MoreObjects.toStringHelper(this)
		.add("metadataMap", metadataMap).toString();
    }
}
