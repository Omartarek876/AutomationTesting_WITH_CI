package utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Reads JSON test data from the test resources folder on the classpath. */
public class JsonFileManager {
    private final String jsonDocument;
    private final String resourceName;

    public JsonFileManager(String resourceName) {
        this.resourceName = normalizeResourceName(resourceName);

        String content;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(this.resourceName)) {
            if (input == null) {
                throw new IllegalArgumentException("JSON test data resource was not found: " + this.resourceName);
            }
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read JSON test data resource: " + this.resourceName, e);
        }

        try {
            JsonPath.parse(content);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid JSON in test data resource: " + this.resourceName, e);
        }
        this.jsonDocument = content;
    }

    /** Reads a JSON value as a string. */
    public String getTestData(String jsonPath) {
        Object value = readValue(jsonPath);
        if (value == null) {
            throw missingValue(jsonPath);
        }
        return String.valueOf(value);
    }

    /** Reads a JSON object as a map so callers can import a whole data group at once. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getObject(String jsonPath) {
        Object value = readValue(jsonPath);
        if (value instanceof Map<?, ?>) {
            return (Map<String, Object>) value;
        }
        throw new IllegalArgumentException(
                "Expected a JSON object at path '" + jsonPath + "' in resource '" + resourceName + "'");
    }

    private Object readValue(String jsonPath) {
        try {
            return JsonPath.parse(jsonDocument).read(jsonPath);
        } catch (PathNotFoundException e) {
            throw missingValue(jsonPath, e);
        }
    }

    private IllegalArgumentException missingValue(String jsonPath) {
        return missingValue(jsonPath, null);
    }

    private IllegalArgumentException missingValue(String jsonPath, Exception cause) {
        String message = "No value found for JSON path '" + jsonPath + "' in resource '" + resourceName + "'";
        return cause == null ? new IllegalArgumentException(message) : new IllegalArgumentException(message, cause);
    }

    private static String normalizeResourceName(String resourceName) {
        String normalized = resourceName.replace('\\', '/').trim();
        String testResourcesPrefix = "src/test/resources/";
        if (normalized.startsWith(testResourcesPrefix)) {
            normalized = normalized.substring(testResourcesPrefix.length());
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("JSON test data resource name must not be blank");
        }
        return normalized;
    }
}
