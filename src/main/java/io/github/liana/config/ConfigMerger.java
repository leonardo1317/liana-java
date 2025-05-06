package io.github.liana.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class ConfigMerger {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ConfigMerger() {
    }

    public static Map<String, Object> merge(List<Map<String, Object>> configs) {
        if (configs == null || configs.isEmpty()) {
            return Collections.emptyMap();
        }

        if (configs.size() == 1) {
            return configs.get(0);
        }

        ObjectNode merged = mapper.createObjectNode();
        for (Map<String, Object> config : configs) {
            ObjectNode current = mapper.convertValue(config, ObjectNode.class);
            try {
                mapper.readerForUpdating(merged).readValue(current);
            } catch (IOException ex) {
                throw new RuntimeException("Error merging configurations", ex);
            }
        }

        return mapper.convertValue(merged, new TypeReference<>() {
        });
    }
}
