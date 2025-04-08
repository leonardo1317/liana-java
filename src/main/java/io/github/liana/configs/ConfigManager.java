package io.github.liana.configs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ConfigManager() {
    }

    public static Configuration mergeInConfig(Configuration... configs) {
        ObjectNode merged = mapper.createObjectNode();

        for (Configuration config : configs) {
            ObjectNode current = mapper.convertValue(toMap(config), ObjectNode.class);
            try {
                mapper.readerForUpdating(merged).readValue(current);
            } catch (IOException ex) {
                throw new RuntimeException("Error merging configurations", ex);
            }
        }

        Map<String, Object> mergedMap = mapper.convertValue(merged, new TypeReference<>() {
        });
        return new MapConfiguration(mergedMap);
    }

    private static Map<String, Object> toMap(Configuration config) {
        Map<String, Object> map = new LinkedHashMap<>();
        config.getKeys().forEachRemaining(key -> map.put(key, config.getProperty(key)));
        return map;
    }
}
