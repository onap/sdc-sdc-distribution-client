package org.openecomp.sdc.toscaparser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

public class JarExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarExtractor.class);
    private static final String PYTHON_DEPENDENCIES_PATH = "Lib/site-packages";
    private static final String TEMP_DIR_PREFIX = "__tosca__";

    public Path extractPyhtonPackages() throws IOException {
        String codePath = getCodePath();
        if (!isRunningInJar(codePath)) {
            LOGGER.info("#extractPyhtonPackages - Nothing to extract, we're not running in a jar file.");
            return null;
        }

        Path tempDirPath = createTempDirectory();
        String tempDirName = tempDirPath.toString();
        extractJarDirectory(codePath, tempDirName);
        LOGGER.info("#extractPyhtonPackages - End. Extracted python dependencies to {}", tempDirName);
        return tempDirPath;
    }

    private Path createTempDirectory() throws IOException {
        Path tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);
        LOGGER.debug("#createTempDirectory - tempDir created: {}", tempDir);
        return tempDir;
    }

    private void extractJarDirectory(String jarDir, String tempDir) throws IOException {
        try (JarFile jarFile = new JarFile(jarDir)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (shouldExtract(entry)) {
                    extract(jarFile, entry, tempDir);
                }
            }
        }
    }

    private void extract(JarFile jarFile, JarEntry entry, String tempDirName) throws IOException {
        try (InputStream source = jarFile.getInputStream(entry)) {
            Path targetPath = Paths.get(tempDirName, entry.getName());
            Files.createDirectories(targetPath.getParent());
            Files.copy(source, targetPath);
        }
    }

    private boolean shouldExtract(JarEntry entry) {
        return !entry.isDirectory() && entry.getName()
                .startsWith(PYTHON_DEPENDENCIES_PATH);
    }

    private String getCodePath() {
        String codePath = this.getClass()
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile();
        LOGGER.debug("#getCodePath - codePath: {}", codePath);
        return codePath;
    }

    private boolean isRunningInJar(String path) {
        return path.endsWith(".jar");
    }
    
    public void deleteDirectory(Path path) throws IOException {
        MoreFiles.deleteRecursively(path, RecursiveDeleteOption.ALLOW_INSECURE);
        LOGGER.info("#deleteDirectory - deleted temp directory: {}", path);
    }
}
