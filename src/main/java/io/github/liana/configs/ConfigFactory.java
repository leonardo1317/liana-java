package io.github.liana.configs;

import io.github.liana.configs.strategies.PropertiesConfigStrategy;
import io.github.liana.configs.strategies.YamlConfigStrategy;

import java.util.List;

public class ConfigFactory {
    private static final List<ConfigStrategy> strategies = List.of(
            new YamlConfigStrategy(),
            new PropertiesConfigStrategy()
    );

    private ConfigFactory() {
    }

    public static ConfigLoader create() {
        return new DefaultConfigLoader(strategies);
    }

}
