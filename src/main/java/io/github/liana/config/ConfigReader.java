package io.github.liana.config;

import java.time.Duration;
import java.util.Map;

public interface ConfigReader {
    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    boolean getBoolean(String key);

    float getFloat(String key);

    double getDouble(String key);

    Duration getDuration(String key);

    String[] getStringArray(String key);

    boolean has(String key);

    <T> T getValue(String key, Class<T> clazz);

    Map<String, Object> getAllSettings();
}
