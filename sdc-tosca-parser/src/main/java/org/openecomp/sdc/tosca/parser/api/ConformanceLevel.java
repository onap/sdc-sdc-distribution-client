package org.openecomp.sdc.tosca.parser.api;

public class ConformanceLevel {

    private String minVersion;
    private String maxVersion;

    public String getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }


}
