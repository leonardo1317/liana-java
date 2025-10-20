package io.github.liana.config;

public interface LianaConfigBuilder {

  LianaConfigBuilder addProviders(ConfigProvider... providers);

  LianaConfigBuilder addLoaders(ConfigLoader... loaders);

  ConfigManager build();
}
