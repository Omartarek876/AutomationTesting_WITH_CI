package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.Properties;

public class PropertiesReader {
    private static final Path PROP_ROOT = Path.of("src", "main", "resources");

    public static void loadProperties() {
        Properties fileProperties = new Properties();
        if (!Files.isDirectory(PROP_ROOT)) {
            throw new IllegalStateException("Properties directory not found: " + PROP_ROOT.toAbsolutePath());
        }
        try (Stream<Path> paths = Files.walk(PROP_ROOT)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".properties"))
                    .sorted()
                    .forEach(path -> {
                        try (var input = Files.newInputStream(path)) {
                            fileProperties.load(input);
                        } catch (IOException e) {
                            throw new IllegalStateException("Could not load properties file: " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Could not read properties directory: " + PROP_ROOT, e);
        }
        // Explicit JVM properties (for example CI overrides) take precedence over defaults.
        fileProperties.putAll(System.getProperties());
        fileProperties.forEach((key, value) -> {
            if (System.getProperty(key.toString()) == null) {
                System.setProperty(key.toString(), value.toString());
            }
        });
    }
}
