/**
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */
package io.github.liana.config;

/**
 * Provides access to the default singleton {@link ConfigManager}.
 *
 * <p>This class acts as the main entry point for loading and managing configuration data.
 * It exposes both a default, globally shared instance, and a builder for custom configurations.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Use the default configuration manager
 * ConfigManager config = LianaConfig.defaultManager();
 *
 * // Build a custom one with extra providers or loaders
 * ConfigManager custom = LianaConfig.builder()
 *     .addProviders(new HttpProvider())
 *     .addLoaders(new TomlLoader(...))
 *     .build();
 * }</pre>
 */
public final class LianaConfig {

  private static class Holder {

    private static final ConfigManager INSTANCE = builder().build();
  }

  private LianaConfig() {
  }

  /**
   * Returns the singleton instance of the {@link ConfigManager}.
   *
   * @return the default configuration manager instance
   */
  public static ConfigManager getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Returns a new builder for creating custom {@link ConfigManager} instances.
   *
   * @return a new {@link LianaConfigBuilder}
   */
  public static LianaConfigBuilder builder() {
    return new DefaultLianaConfigBuilder();
  }
}
