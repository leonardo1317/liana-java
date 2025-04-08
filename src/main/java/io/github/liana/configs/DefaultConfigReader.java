package io.github.liana.configs;

import org.apache.commons.configuration2.Configuration;

import java.time.Duration;

public class DefaultConfigReader implements ConfigReader {
    private final Configuration config;

    private DefaultConfigReader(Configuration configuration) {
        this.config = configuration;
    }

    public static ConfigReader fromFile(String path) {
        ConfigLoader configLoader = ConfigFactory.create();
        return new DefaultConfigReader(configLoader.load(path));
    }

    @Override
    public String getString(String key) {
        return config.getString(key);
    }

    @Override
    public String getStringOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getString(key) : config.getString(fallbackKey);
    }

    @Override
    public int getInt(String key) {
        return config.getInt(key);
    }

    @Override
    public int getIntOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getInt(key) : config.getInt(fallbackKey);
    }

    @Override
    public long getLong(String key) {
        return config.getLong(key);
    }

    @Override
    public long getLongOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getLong(key) : config.getLong(fallbackKey);
    }

    @Override
    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    @Override
    public boolean getBooleanOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getBoolean(key) : config.getBoolean(fallbackKey);
    }

    @Override
    public float getFloat(String key) {
        return config.getFloat(key);
    }

    @Override
    public float getFloatOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getFloat(key) : config.getFloat(fallbackKey);
    }

    @Override
    public double getDouble(String key) {
        return config.getDouble(key);
    }

    @Override
    public double getDoubleOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getDouble(key) : config.getDouble(fallbackKey);
    }

    @Override
    public Duration getDuration(String key) {
        return config.getDuration(key);
    }

    @Override
    public Duration getDurationOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getDuration(key) : config.getDuration(fallbackKey);
    }

    @Override
    public String[] getStringArray(String key) {
        return config.getStringArray(key);
    }

    @Override
    public String[] getStringArrayOrFallback(String key, String fallbackKey) {
        return config.containsKey(key) ? config.getStringArray(key) : config.getStringArray(fallbackKey);
    }
}
