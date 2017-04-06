package org.openecomp.sdc.toscaparser.jython;

import java.util.List;
import java.util.Map;

public interface JyGroup extends JyEntityTemplate {
	
	List<String> getJyMembers();
	Map<String, Object> getJyMetadata();
}
