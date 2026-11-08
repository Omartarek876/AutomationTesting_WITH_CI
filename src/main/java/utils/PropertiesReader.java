package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesReader {
    private static final String[] CONFIG_FILES = {"environment.properties", "execution.properties"};

    private PropertiesReader() {
    }

    /** Loads bundled defaults without replacing values passed through -D options. */
    public static synchronized void loadProperties() {
        Properties defaults = new Properties();
        ClassLoader classLoader = PropertiesReader.class.getClassLoader();
        for (String resource : CONFIG_FILES) {
            try (InputStream input = classLoader.getResourceAsStream(resource)) {
                if (input == null) {
                    throw new IllegalStateException("Missing configuration resource: " + resource);
                }
                defaults.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Could not load configuration resource: " + resource, e);
            }
        }

        defaults.forEach((key, value) -> System.getProperties().putIfAbsent(key, value));
    }
}
