package org.openecomp.sdc.api.consumer;



public interface IFinalDistrStatusMessage extends IDistributionStatusMessageBasic{

    default String getConsumerID(){return "";}
    default String getComponentName(){return "";}
}
