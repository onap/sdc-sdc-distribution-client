package org.openecomp.sdc.toscaparser.utils;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum PythonType {
    
    INT("int", Integer.class),
    FLOAT("float", Double.class),
    BOOL("bool", Boolean.class),
    STR("str", String.class),
    LIST("list", List.class),
    DICT("dict", Map.class),
    UNKNOWN("", Object.class);
    
    private static final Map<String, PythonType> NAME_TO_TYPE;
    private final String typeName;
    private final Class<?> javaClass;
    
    static {
        NAME_TO_TYPE = Arrays.stream(values())
                .filter(type -> type != UNKNOWN)
                .collect(toMap(PythonType::getTypeName, Function.identity()));
    }

    private PythonType(String typeName, Class<?> javaClass) {
        this.typeName = typeName;
        this.javaClass = javaClass;
    }

    public static PythonType fromName(String name) {
        PythonType pythonType = NAME_TO_TYPE.get(name);
        return pythonType != null ? pythonType : UNKNOWN;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public Class<?> getJavaClass() {
        return javaClass;
    }
}