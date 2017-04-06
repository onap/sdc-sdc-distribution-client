package org.openecomp.sdc.toscaparser.jython.parameters;

public interface JyInput {
    
    String getJyName();
    String getJyType();
    Object getJyDefault();
    boolean isJyRequired();
    String getJyDescription();
}
