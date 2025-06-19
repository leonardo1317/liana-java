package io.github.liana.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.liana.config.ObjectMapperProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapMerger {
    private static final ObjectMapper mapper = ObjectMapperProvider.getJsonInstance();

    private MapMerger() {
    }

    public static Map<String, Object> merge(List<Map<String, Object>> sources) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyMap();
        }

        if (sources.size() == 1) {
            return Collections.unmodifiableMap(sources.get(0));
        }

        ObjectNode merged = mapper.createObjectNode();
        for (Map<String, Object> source : sources) {
            ObjectNode current = mapper.convertValue(source, ObjectNode.class);
            overrideArrays(merged, current);
            try {
                mapper.readerForUpdating(merged).readValue(current);
            } catch (IOException ex) {
                throw new RuntimeException("Error merging data structures", ex);
            }
        }

        return mapper.convertValue(merged, new TypeReference<>() {
        });
    }

    private static void overrideArrays(ObjectNode merged, ObjectNode current) {
        List<String> fieldNames = new ArrayList<>();
        current.fieldNames().forEachRemaining(fieldNames::add);

        for (String field : fieldNames) {
            JsonNode value = current.get(field);
            if (value.isArray()) {
                merged.set(field, value);
                current.remove(field);
            }
        }
    }
}
