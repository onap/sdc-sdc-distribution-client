package org.openecomp.sdc.tosca.parser.exceptions;

public class SdcToscaParserException extends Exception {

    private static final long serialVersionUID = 626014844866501196L;
    private String code;

    public SdcToscaParserException(String string, String code) {
        super(string);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
