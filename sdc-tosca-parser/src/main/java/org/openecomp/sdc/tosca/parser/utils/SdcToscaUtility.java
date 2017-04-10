package org.openecomp.sdc.tosca.parser.utils;

import java.util.regex.Pattern;

public class SdcToscaUtility {
	
	public final static Pattern COMPONENT_INSTANCE_NAME_DELIMETER_PATTERN = Pattern.compile("[\\.\\-]+");
	
	public static String normaliseComponentInstanceName(String name) {
		String normalizedName = name.toLowerCase();
		normalizedName = COMPONENT_INSTANCE_NAME_DELIMETER_PATTERN.matcher(normalizedName).replaceAll(" ");
		String[] split = normalizedName.split(" ");
		StringBuffer sb = new StringBuffer();
		for (String splitElement : split) {
			sb.append(splitElement);
		}
		return sb.toString();
	}
}
