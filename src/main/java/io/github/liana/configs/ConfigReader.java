package io.github.liana.configs;

import java.time.Duration;

public interface ConfigReader {
    String getString(String key);

    String getStringOrFallback(String key, String fallbackKey);

    int getInt(String key);

    int getIntOrFallback(String key, String fallbackKey);

    long getLong(String key);

    long getLongOrFallback(String key, String fallbackKey);

    boolean getBoolean(String key);

    boolean getBooleanOrFallback(String key, String fallbackKey);

    float getFloat(String key);

    float getFloatOrFallback(String key, String fallbackKey);

    double getDouble(String key);

    double getDoubleOrFallback(String key, String fallbackKey);

    Duration getDuration(String key);

    Duration getDurationOrFallback(String key, String fallbackKey);

    String[] getStringArray(String key);

    String[] getStringArrayOrFallback(String key, String fallbackKey);
}
