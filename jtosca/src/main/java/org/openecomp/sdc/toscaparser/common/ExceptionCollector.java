package org.openecomp.sdc.toscaparser.common;

import org.openecomp.sdc.toscaparser.elements.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

// Perfectly good enough... 

public class ExceptionCollector {

	private static Logger log = LoggerFactory.getLogger(ExceptionCollector.class.getName());

	private static boolean isCollecting = false;
	private static ArrayList<String> exceptionStrings = null;
	private static ArrayList<String> traceStrings = null;
	private static boolean bWantTrace = true;

	public static void start() {
		if(exceptionStrings == null) {
			exceptionStrings = new ArrayList<String>();
			traceStrings = new ArrayList<String>();
		}
		isCollecting = true;
	}

	public static void stop() {
		isCollecting = false;
	}

	public static void clear() {
		exceptionStrings = null;
		traceStrings = null;
	}

	public static void appendException(String strExc) { // throws Exception {

		if(!isCollecting) {
			// throw new Exception("Can't append exception " + strExc);
			log.error("ExceptionCollector - appendException - Can't append exception {}", strExc);
		}

		if(!exceptionStrings.contains(strExc)) {
			exceptionStrings.add(strExc);
			// get stack trace
			StackTraceElement[] ste =  Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			// skip the last 2 (getStackTrace and this)
			for(int i=2; i<ste.length; i++) {
				sb.append(String.format("  %s(%s:%d)%s",ste[i].getClassName(),ste[i].getFileName(),
												ste[i].getLineNumber(),i==ste.length-1?" ":"\n"));
			}
			traceStrings.add(sb.toString());
		}
	}

	public static ArrayList<String> getExceptionReport() {
		if(exceptionStrings.size() > 0) {
			ArrayList<String> report = new ArrayList<>();
			for(int i=0; i<exceptionStrings.size(); i++) {
				report.add(exceptionStrings.get(i));
				if(bWantTrace) {
					report.add(traceStrings.get(i));
				}
			}
			return report;
		}
		return null;
	}
	
	public static int errorsCaught() {
		return exceptionStrings.size();
	}
	
	public static void setWantTrace(boolean b) {
		bWantTrace = b;
	}

}
