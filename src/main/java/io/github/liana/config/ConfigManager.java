package io.github.liana.config;

public interface ConfigManager {
    ConfigReader load(ConfigResourceLocation configResourceLocation);
}
