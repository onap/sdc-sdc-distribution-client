package org.openecomp.sdc.tosca.parser.config;

import java.util.EnumMap;
import java.util.Map;

import org.openecomp.sdc.toscaparser.api.utils.JToscaErrorCodes;

public enum SdcToscaParserErrors {

    BAD_FORMAT, CONFORMANCE_LEVEL_ERROR, FILE_NOT_FOUND, GENERAL_ERROR;

    private static final  Map<JToscaErrorCodes, SdcToscaParserErrors> JTOSCA_ERRORS =
        new EnumMap<JToscaErrorCodes, SdcToscaParserErrors>(JToscaErrorCodes.class) {{

            put(JToscaErrorCodes.GENERAL_ERROR, GENERAL_ERROR);
            
            put(JToscaErrorCodes.PATH_NOT_VALID, FILE_NOT_FOUND);
            //CSAR contents problems
            put(JToscaErrorCodes.MISSING_META_FILE, BAD_FORMAT);
            put(JToscaErrorCodes.INVALID_META_YAML_CONTENT, BAD_FORMAT);
            put(JToscaErrorCodes.ENTRY_DEFINITION_NOT_DEFINED, BAD_FORMAT);
            put(JToscaErrorCodes.MISSING_ENTRY_DEFINITION_FILE, BAD_FORMAT);
            put(JToscaErrorCodes.CSAR_TOSCA_VALIDATION_ERROR, BAD_FORMAT);
            put(JToscaErrorCodes.INVALID_CSAR_FORMAT, BAD_FORMAT);
    }};

    public static SdcToscaParserErrors getSdcErrorByJToscaError(JToscaErrorCodes jToscaErrorCode) {
        return JTOSCA_ERRORS.get(jToscaErrorCode);
    }

}
