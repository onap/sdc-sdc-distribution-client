package org.openecomp.sdc.toscaparser;

import java.util.Objects;

import org.openecomp.sdc.toscaparser.api.ToscaTemplate;
import org.openecomp.sdc.toscaparser.api.ToscaTemplateFactory;
import org.openecomp.sdc.toscaparser.jython.JyToscaTemplate;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class ToscaParser {
    
    private final ToscaTemplateFactory toscaTemplateFactory;
    private final PyObject jythonToscaTemplate;
    private final PythonInterpreter pythonInterpreter;

    public ToscaParser(ToscaTemplateFactory toscaTemplateFactory, PythonInterpreter pythonInterpreter) {
        this.toscaTemplateFactory = Objects.requireNonNull(toscaTemplateFactory);
        this.pythonInterpreter = Objects.requireNonNull(pythonInterpreter);
        jythonToscaTemplate = getJythonToscaTemplate();
    }

    private PyObject getJythonToscaTemplate() {
        try (PythonInterpreter interpreter = pythonInterpreter) {
            interpreter.exec("from toscaparser.tosca_template import ToscaTemplate");
            return interpreter.get("ToscaTemplate");
        }
    }

    public ToscaTemplate parse(String path) {
        PyObject toscaTemplateInstance = jythonToscaTemplate.__call__(new PyString(path));
        JyToscaTemplate jyToscaTemplate = (JyToscaTemplate) toscaTemplateInstance.__tojava__(JyToscaTemplate.class);
        return toscaTemplateFactory.create(jyToscaTemplate);   
    }
}
