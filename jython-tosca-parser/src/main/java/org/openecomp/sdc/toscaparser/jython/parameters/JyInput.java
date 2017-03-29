package org.openecomp.sdc.toscaparser.jython.parameters;

public interface JyInput {
    
    String getJyName();
    String getJyType();
    boolean isJyRequired();
    String getJyDescription();
}
