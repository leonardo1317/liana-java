package io.github.liana.config;

import java.util.Map;

public class MapConfiguration extends AbstractConfiguration {
    protected MapConfiguration(Map<String, Object> nestedMap) {
        super(nestedMap);
    }
}
