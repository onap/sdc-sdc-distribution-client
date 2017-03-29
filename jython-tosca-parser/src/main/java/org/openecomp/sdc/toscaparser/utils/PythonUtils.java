package org.openecomp.sdc.toscaparser.utils;

public final class PythonUtils {
    
    private PythonUtils() {
        // No instances allowed
    }
    
    public static Object cast(Object object, String pythonTypeName) {
        PythonType pythonType = PythonType.fromName(pythonTypeName);
        return pythonType.getJavaClass().cast(object);
    }
}
