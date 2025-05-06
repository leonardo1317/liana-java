package io.github.liana.config;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * An immutable key-value configuration map with strict key validation.
 * Keys must match [a-zA-Z0-9_.-]+, and values cannot be null or blank.
 */
public class ConfigMap {
    private static final Pattern REGEX_KEY_PATTERN = Pattern.compile("[a-zA-Z0-9_.-]+");
    private static final ConfigMap EMPTY = new ConfigMap(Collections.emptyMap());
    private final Map<String, String> map;

    private ConfigMap(Map<String, String> map) {
        this.map = map;
    }

    public static ConfigMap emptyMap() {
        return EMPTY;
    }

    public static ConfigMap of(String... entries) {
        int length = entries.length;
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Missing value for key: " + entries[length - 1]);
        }

        Map<String, String> validatedMap = new LinkedHashMap<>();
        for (int i = 0; i < length; i += 2) {
            String key = entries[i];
            String value = entries[i + 1];
            validateEntry(key, value);
            validatedMap.put(key, value);
        }

        return new ConfigMap(Collections.unmodifiableMap(validatedMap));
    }

    private static void validateEntry(String key, String value) {
        if (key == null || key.isBlank() || !REGEX_KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid value for key '" + key + "'");
        }
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Map<String, String> toMap() {
        return new LinkedHashMap<>(map);
    }
}
