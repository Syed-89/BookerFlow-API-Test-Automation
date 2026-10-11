package com.framework.config;

import org.aeonbits.owner.ConfigFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Single access point for configuration (thread-safe, immutable after class load).
 *
 * Overrides: environment variables are mapped by lower-casing and replacing '_' with '.'
 * (AUTH_PASSWORD -> auth.password), then -D system properties are applied on top.
 * Blank env vars are ignored so an unset CI secret never wipes a default.
 */
public final class ConfigManager {

    private static final String ENVIRONMENT;
    private static final FrameworkConfig CONFIG;

    static {
        Map<String, String> overrides = new HashMap<>();
        System.getenv().forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                overrides.put(key.toLowerCase(Locale.ROOT).replace('_', '.'), value);
            }
        });
        System.getProperties().stringPropertyNames()
                .forEach(key -> overrides.put(key, System.getProperty(key)));

        ENVIRONMENT = overrides.getOrDefault("env", "qa");
        ConfigFactory.setProperty("env", ENVIRONMENT);
        CONFIG = ConfigFactory.create(FrameworkConfig.class, overrides);
    }

    private ConfigManager() {
    }

    public static FrameworkConfig get() {
        return CONFIG;
    }

    public static String environment() {
        return ENVIRONMENT;
    }
}
