package com.automationexercise.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final String[] CONFIG_FILES = {"environment.properties", "execution.properties"};

    public static void loadProperties() {
        Properties defaults = new Properties();
        ClassLoader classLoader = PropertiesReader.class.getClassLoader();
        for (String file : CONFIG_FILES) {
            try (InputStream input = classLoader.getResourceAsStream(file)) {
                if (input == null) {
                    throw new IllegalStateException("Configuration file not found: " + file);
                }
                defaults.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Could not load configuration file: " + file, e);
            }
        }
        defaults.forEach((key, value) -> {
            if (System.getProperty(key.toString()) == null) {
                System.setProperty(key.toString(), value.toString());
            }
        });
    }
}
