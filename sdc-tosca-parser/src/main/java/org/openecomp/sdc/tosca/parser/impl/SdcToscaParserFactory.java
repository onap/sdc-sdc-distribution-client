package org.openecomp.sdc.tosca.parser.impl;

import org.openecomp.sdc.tosca.parser.api.ConformanceLevel;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.config.*;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.utils.GeneralUtility;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.toscaparser.api.common.JToscaException;
import org.openecomp.sdc.toscaparser.api.utils.JToscaErrorCodes;

public class SdcToscaParserFactory {

    private static volatile SdcToscaParserFactory instance;
    private static Configuration configuration;
    private static ErrorConfiguration errorConfiguration;

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
                    errorConfiguration = ConfigurationManager.getInstance().getErrorConfiguration();
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
     */
    public ISdcCsarHelper getSdcCsarHelper(String csarPath) throws SdcToscaParserException {
        synchronized (SdcToscaParserFactory.class) {
            ToscaTemplate tosca = null;
            try {
                tosca = new ToscaTemplate(csarPath, null, true, null);
            } catch (JToscaException e) {
                throwSdcToscaParserException(e);
            }
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
                throwConformanceLevelException(minVersion, maxVersion);
            }
        } else {
            throwConformanceLevelException(minVersion, maxVersion);
        }
    }

    private void throwConformanceLevelException(String minVersion, String maxVersion) throws SdcToscaParserException {
        ErrorInfo errorInfo = errorConfiguration.getErrorInfo(SdcToscaParserErrors.CONFORMANCE_LEVEL_ERROR.toString());
        throw new SdcToscaParserException(String.format(errorInfo.getMessage(), minVersion, maxVersion), errorInfo.getCode());
    }

    private void throwSdcToscaParserException(JToscaException e) throws SdcToscaParserException {
        ErrorInfo errorInfo = errorConfiguration.getErrorInfo(SdcToscaParserErrors.getSdcErrorByJToscaError(JToscaErrorCodes.getByCode(e.getCode())).toString());
        throw new SdcToscaParserException(errorInfo.getMessage(), errorInfo.getCode());
    }
}