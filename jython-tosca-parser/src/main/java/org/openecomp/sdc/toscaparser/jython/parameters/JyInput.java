package org.openecomp.sdc.toscaparser.jython.parameters;

public interface JyInput {
    
    String getJyName();
    String getJyType();
    String getJyDefault();
    boolean isJyRequired();
    String getJyDescription();
}
