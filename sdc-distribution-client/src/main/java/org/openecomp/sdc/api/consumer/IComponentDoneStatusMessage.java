package org.openecomp.sdc.api.consumer;

public interface IComponentDoneStatusMessage extends IDistributionStatusMessageBasic {
    String getComponentName();
    default String getArtifactURL(){return "";}

}
