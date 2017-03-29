package org.openecomp.sdc.toscaparser;

import java.io.IOException;

import org.openecomp.sdc.toscaparser.api.ToscaTemplateFactory;
import org.openecomp.sdc.toscaparser.utils.JarExtractor;
import org.python.util.PythonInterpreter;

/**
 * This is the entry point of the tosca-parser: a factory for creating {@code ToscaParser}s.
 * <b>This class is not thread-safe.</b> Once you are done with all {@code ToscaParser}s 
 * created by this factory, the {@code ToscaParserFactory} itself must be closed either by
 * calling its close() method explicitly or preferrably by creating it in a try-with-resources block:
 * <pre>
    try (ToscaParserFactory toscaParserFactory = new ToscaParserFactory()){
        ToscaParser parser = toscaParserFactory.create()
        ToscaTemplate toscaTemplate = parser.parse(toscaFilePath);
        ...
    }
 * </pre>
 * @author Yaniv Nahoum
 *
 */
public class ToscaParserFactory implements AutoCloseable {
    
    private JythonRuntime jythonRuntime;

    public ToscaParser create() throws IOException {
        initRuntime();
        ToscaTemplateFactory toscaTemplateFactory = new ToscaTemplateFactory();
        PythonInterpreter pythonInterpreter = new PythonInterpreter();
        return new ToscaParser(toscaTemplateFactory, pythonInterpreter);
    }
    
    private void initRuntime() throws IOException {
        if (jythonRuntime == null) {
            jythonRuntime = new JythonRuntime(new JarExtractor());
            jythonRuntime.initialize();
        }
    }    
    
    @Override
    public void close() throws IOException {
        if (jythonRuntime != null) {
            jythonRuntime.terminate();
        }
    }
}
