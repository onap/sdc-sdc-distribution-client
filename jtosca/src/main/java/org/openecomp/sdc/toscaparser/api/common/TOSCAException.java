package org.openecomp.sdc.toscaparser.api.common;

import java.util.IllegalFormatException;

public class TOSCAException extends Exception {
	private String message = "An unkown exception has occurred";
	private static boolean FATAL_EXCEPTION_FORMAT_ERRORS = false;
	private String msgFmt = null;

	public TOSCAException(String...strings) {
		try {
			message = String.format(msgFmt,(Object[])strings);
		}
		catch (IllegalFormatException e) {
			// TODO log
			
			if(FATAL_EXCEPTION_FORMAT_ERRORS) {
				throw e;
			}
			 
		}
		
	}
	
	public String __str__() {
		return message;
	}
	
	public static void generate_inv_schema_property_error(String name, String attr, String value, String valid_values) {
		//TODO
		
	}
	
	public static void setFatalFormatException(boolean flag) {
		FATAL_EXCEPTION_FORMAT_ERRORS = flag;
	}
		
}

