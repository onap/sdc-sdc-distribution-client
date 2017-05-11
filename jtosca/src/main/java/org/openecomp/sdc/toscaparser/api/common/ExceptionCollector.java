package org.openecomp.sdc.toscaparser.api.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Perfectly good enough... 

public class ExceptionCollector {

	private static Logger log = LoggerFactory.getLogger(ExceptionCollector.class.getName());

	//private static boolean isCollecting = false;
	private static ArrayList<String> exceptionStrings = new ArrayList<>();
	private static ArrayList<String> exceptionTraceStrings = new ArrayList<>();
	private static ArrayList<String> warningStrings = new ArrayList<>();
	private static ArrayList<String> warningTraceStrings = new ArrayList<>();
	private static boolean bWantTrace = true;

	/*public static void start() {
		if(exceptionStrings == null) {
			exceptionStrings = new ArrayList<String>();
			exceptionTraceStrings = new ArrayList<String>();
		}
		isCollecting = true;
	}*/

	/*public static void stop() {
		isCollecting = false;
	}*/

	public static void clear() {
		exceptionStrings = new ArrayList<>();
		exceptionTraceStrings = new ArrayList<>();
		warningStrings = new ArrayList<>();
		warningTraceStrings = new ArrayList<>();
	}

	public static void appendException(String strExc) { // throws Exception {

		/*if(!isCollecting) {
			// throw new Exception("Can't append exception " + strExc);
			log.error("ExceptionCollector - appendException - Can't append exception {}", strExc);
		}*/

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
			exceptionTraceStrings.add(sb.toString());
		}
	}
	
	public static void appendWarning(String strExc) { // throws Exception {

		/*if(!isCollecting) {
			// throw new Exception("Can't append exception " + strExc);
			log.error("ExceptionCollector - appendException - Can't append exception {}", strExc);
		}*/

		if(!warningStrings.contains(strExc)) {
			warningStrings.add(strExc);
			// get stack trace
			StackTraceElement[] ste =  Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			// skip the last 2 (getStackTrace and this)
			for(int i=2; i<ste.length; i++) {
				sb.append(String.format("  %s(%s:%d)%s",ste[i].getClassName(),ste[i].getFileName(),
												ste[i].getLineNumber(),i==ste.length-1?" ":"\n"));
			}
			warningTraceStrings.add(sb.toString());
		}
	}

	public static List<String> getCriticalsReport() {
		
		List<String> res = new ArrayList<>();
		if(exceptionStrings.size() > 0) {
			for(int i=0; i<exceptionStrings.size(); i++) {
				res.add(exceptionStrings.get(i));
				if(bWantTrace) {
					res.add(exceptionTraceStrings.get(i));
				}
			}
		}
		return res;
	}
	
	public static List<String> getWarningsReport() {
		
		List<String> res = new ArrayList<>();
		if(warningStrings.size() > 0) {
			for(int i=0; i<warningStrings.size(); i++) {
				res.add(warningStrings.get(i));
				if(bWantTrace) {
					res.add(warningTraceStrings.get(i));
				}
			}
		}
		return res;
	}
	
	public static int errorsCaught() {
		return exceptionStrings.size();
	}
	
	public static int warningsCaught() {
		return warningStrings.size();
	}
	
	public static void setWantTrace(boolean b) {
		bWantTrace = b;
	}

}
