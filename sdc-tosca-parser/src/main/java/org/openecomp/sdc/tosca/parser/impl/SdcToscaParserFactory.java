package org.openecomp.sdc.tosca.parser.impl;

import java.io.IOException;
import java.util.List;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.toscaparser.api.common.JToscaException;

public class SdcToscaParserFactory{

	private static SdcToscaParserFactory instance;
	
	private SdcToscaParserFactory(){}

	/**
	 * Get an SdcToscaParserFactory instance.
	 */
	public static SdcToscaParserFactory getInstance() {
		if (instance == null) {
			synchronized (SdcToscaParserFactory.class) {
				if (instance == null) {
					instance = new SdcToscaParserFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * Get an ISdcCsarHelper object for this CSAR file.
	 * @param csarPath - the path to CSAR file.
	 * @return ISdcCsarHelper object.
	 * @throws SdcToscaParserException - in case the path or CSAR are invalid.
	 * @throws JToscaException 
	 */
	public ISdcCsarHelper getSdcCsarHelper(String csarPath) throws SdcToscaParserException, JToscaException, IOException{
		//TODO add logic to check if legal file and csar
		synchronized (SdcToscaParserFactory.class) {
			ToscaTemplate tosca = new ToscaTemplate(csarPath, null, true, null);
			SdcCsarHelperImpl sdcCsarHelperImpl = new SdcCsarHelperImpl(tosca);
			return sdcCsarHelperImpl;
		}
	}

}