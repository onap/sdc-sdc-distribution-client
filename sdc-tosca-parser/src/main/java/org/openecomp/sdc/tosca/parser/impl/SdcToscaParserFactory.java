package org.openecomp.sdc.tosca.parser.impl;

import org.openecomp.sdc.tosca.parser.api.ConformanceLevel;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.config.Configuration;
import org.openecomp.sdc.tosca.parser.config.ConfigurationManager;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.utils.GeneralUtility;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.toscaparser.api.common.JToscaException;

public class SdcToscaParserFactory {

    private static volatile SdcToscaParserFactory instance;
    private static Configuration configuration;

    private SdcToscaParserFactory() {

    }

    /**
     * Get an SdcToscaParserFactory instance.
     * @return SdcToscaParserFactory instance.
     */
    public static SdcToscaParserFactory getInstance() {
        if (instance == null) {
            synchronized (SdcToscaParserFactory.class) {
                if (instance == null) {
                    instance = new SdcToscaParserFactory();
                    configuration = ConfigurationManager.getInstance().getConfiguration();
                }
            }
        }
        return instance;
    }

    /**
     * Get an ISdcCsarHelper object for this CSAR file.
     *
     * @param csarPath - the absolute path to CSAR file.
     * @return ISdcCsarHelper object.
     * @throws SdcToscaParserException - in case the path or CSAR are invalid.
     * @throws JToscaException - in case the path or CSAR are invalid.
     */
    public ISdcCsarHelper getSdcCsarHelper(String csarPath) throws JToscaException, SdcToscaParserException {
        synchronized (SdcToscaParserFactory.class) {
            ToscaTemplate tosca = new ToscaTemplate(csarPath, null, true, null);
            SdcCsarHelperImpl sdcCsarHelperImpl = new SdcCsarHelperImpl(tosca);
            validateCsarVersion(sdcCsarHelperImpl.getConformanceLevel());
            return sdcCsarHelperImpl;
        }
    }

    private void validateCsarVersion(String cSarVersion) throws SdcToscaParserException {
        ConformanceLevel level = configuration.getConformanceLevel();
        String minVersion = level.getMinVersion();
        String maxVersion = level.getMaxVersion();
        if (cSarVersion != null) {
            if ((GeneralUtility.conformanceLevelCompare(cSarVersion, minVersion) < 0) || (GeneralUtility.conformanceLevelCompare(cSarVersion, maxVersion) > 0)) {
                throw new SdcToscaParserException("Model is not supported. Parser supports versions " + minVersion + " to " + maxVersion);
            }
        } else {
            throw new SdcToscaParserException("Model is not supported. Parser supports versions " + minVersion + " to " + maxVersion);
        }
    }

}