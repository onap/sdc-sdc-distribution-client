package org.openecomp.sdc.tosca.parser.config;

import org.openecomp.sdc.tosca.parser.api.ConformanceLevel;

public class Configuration {

    private ConformanceLevel conformanceLevel;

    public ConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    public void setConformanceLevel(ConformanceLevel conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
    }
}
