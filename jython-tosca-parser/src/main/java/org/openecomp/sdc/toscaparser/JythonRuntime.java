package org.openecomp.sdc.toscaparser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.openecomp.sdc.toscaparser.utils.JarExtractor;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JythonRuntime {

    private static final Logger LOGGER = LoggerFactory.getLogger(JythonRuntime.class);
    private final JarExtractor jarExtractor;
    private Path homePath;

    public JythonRuntime(JarExtractor jarExtractor) {
        this.jarExtractor = jarExtractor;
    }

    public void initialize() throws IOException {
        tryExtractPyhtonPackages();
        initRuntime();
    }

    private void initRuntime() {
        Properties systemProperties = System.getProperties();
        Properties properties = getPythonProperties();
        PythonInterpreter.initialize(systemProperties, properties, new String[0]);
    }

    private void tryExtractPyhtonPackages() throws IOException {
        homePath = jarExtractor.extractPyhtonPackages();
    }

    private Properties getPythonProperties() {
        Properties properties = new Properties();
        if (homePath != null) {
            LOGGER.debug("getPythonProperties - Setting python.home to {}", homePath);
            properties.put("python.home", homePath.toString());
        }
        // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
        properties.put("python.console.encoding", "UTF-8");
        return properties;
    }

    public void terminate() throws IOException {
        if (homePath != null) {
            jarExtractor.deleteDirectory(homePath);
        }
    }
}