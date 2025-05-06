package io.github.liana.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

class ConfigWrapper {
    private final Configuration config;

    public ConfigWrapper(Map<String, Object> config) {
        this.config = new MapConfiguration(config);
    }

    public ConfigWrapper(Configuration config) {
        this.config = config;
    }

    public <T> T getValue(String key, Class<T> clazz) {
        return config.get(clazz, key, null);
    }

    public Map<String, Object> getAllSettings() {
        Map<String, Object> map = new LinkedHashMap<>();
        config.getKeys().forEachRemaining(key -> map.put(key, config.getProperty(key)));
        return map;
    }

    public boolean has(String key) {
        return config.containsKey(key);
    }
}
