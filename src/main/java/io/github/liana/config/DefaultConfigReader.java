package io.github.liana.config;

import java.time.Duration;
import java.util.Map;

class DefaultConfigReader implements ConfigReader {
    private final ConfigWrapper config;

    public DefaultConfigReader(ConfigWrapper configuration) {
        this.config = configuration;
    }

    @Override
    public String getString(String key) {
        return getValue(key, String.class);
    }

    @Override
    public int getInt(String key) {
        return getValue(key, Integer.class);
    }

    @Override
    public long getLong(String key) {
        return getValue(key, Long.class);
    }

    @Override
    public boolean getBoolean(String key) {
        return getValue(key, Boolean.class);
    }

    @Override
    public float getFloat(String key) {
        return getValue(key, Float.class);
    }

    @Override
    public double getDouble(String key) {
        return getValue(key, Double.class);
    }

    @Override
    public Duration getDuration(String key) {
        return getValue(key, Duration.class);
    }

    @Override
    public String[] getStringArray(String key) {
        return getValue(key, String[].class);
    }

    @Override
    public Map<String, Object> getAllSettings() {
        return config.getAllSettings();
    }

    @Override
    public boolean has(String key) {
        return config.has(key);
    }

    @Override
    public <T> T getValue(String key, Class<T> clazz) {
        return config.getValue(key, clazz);
    }
}
