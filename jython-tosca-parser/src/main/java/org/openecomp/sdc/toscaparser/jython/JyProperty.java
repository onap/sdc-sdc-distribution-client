package org.openecomp.sdc.toscaparser.jython;

public interface JyProperty {
    
    String getJyName();
    Object getJyValue();
    String getJyValueClassName();
    String getJyType();
    boolean isJyRequired();
    String getJyDescription();
}
