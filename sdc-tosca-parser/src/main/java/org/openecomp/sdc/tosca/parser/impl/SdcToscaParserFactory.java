package org.openecomp.sdc.tosca.parser.impl;

import java.io.IOException;
import java.util.List;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.ToscaParser;
import org.openecomp.sdc.toscaparser.ToscaParserFactory;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplate;

public class SdcToscaParserFactory implements AutoCloseable{

	private static SdcToscaParserFactory instance;
	private static ToscaParserFactory toscaParserFactory; 
	
	private SdcToscaParserFactory(){}

	/**
	 * Get an SdcToscaParserFactory instance.
	 * After parsing work is done, it must be closed using the close() method.
	 */
	public static SdcToscaParserFactory getInstance() {
		if (instance == null) {
			synchronized (SdcToscaParserFactory.class) {
				if (instance == null) {
					instance = new SdcToscaParserFactory();
					toscaParserFactory = new ToscaParserFactory();
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
	 */
	public ISdcCsarHelper getSdcCsarHelper(String csarPath) throws SdcToscaParserException{
		//TODO add logic to check if legal file and csar
		synchronized (SdcToscaParserFactory.class) {
			if (toscaParserFactory == null){
				throw new SdcToscaParserException("The factory is closed. It was probably closed too soon.");
			}
			try {
				ToscaParser create = toscaParserFactory.create();
				ToscaTemplate parse = create.parse(csarPath);
				SdcCsarHelperImpl sdcCsarHelperImpl = new SdcCsarHelperImpl(parse);
				return sdcCsarHelperImpl;
			} catch (IOException e) {
				throw new SdcToscaParserException("Exception when creating the parser: "+e.getMessage());
			}
		}
	}

	/**
	 * Close the SdcToscaParserFactory.
	 */
	public void close() {
		if (toscaParserFactory != null){
			synchronized (SdcToscaParserFactory.class) {
				if (toscaParserFactory != null) {
					try {
						toscaParserFactory.close();
						toscaParserFactory = null;
					} catch (IOException e) {
						//TODO add logging
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws SdcToscaParserException {
		try (SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance()){ //Autoclosable
			ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper("C:\\Users\\pa0916\\Desktop\\Work\\ASDC\\CSARs\\csar_hello_world.zip");
			//Can run methods on the helper
			List<NodeTemplate> allottedResources = sdcCsarHelper.getAllottedResources();
			//..............
		}
	}
}