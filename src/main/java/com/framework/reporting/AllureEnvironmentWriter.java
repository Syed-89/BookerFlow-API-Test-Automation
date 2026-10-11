package com.framework.reporting;

import com.framework.config.ConfigManager;
import com.framework.config.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Writes environment.properties so the Allure "Environment" widget shows run context. */
public final class AllureEnvironmentWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    private AllureEnvironmentWriter() {
    }

    public static void write() {
        FrameworkConfig cfg = ConfigManager.get();
        Properties props = new Properties();
        props.setProperty("Environment", ConfigManager.environment());
        props.setProperty("Base URL", cfg.baseUrl());
        props.setProperty("Auth type", cfg.authType().name());
        props.setProperty("Java", System.getProperty("java.version", "unknown"));
        props.setProperty("OS", System.getProperty("os.name", "unknown"));

        Path dir = Paths.get(System.getProperty("allure.results.directory", "target/allure-results"));
        try {
            Files.createDirectories(dir);
            try (OutputStream out = Files.newOutputStream(dir.resolve("environment.properties"))) {
                props.store(out, "Allure environment");
            }
        } catch (IOException e) {
            LOG.warn("Could not write Allure environment file: {}", e.getMessage());
        }
    }
}
