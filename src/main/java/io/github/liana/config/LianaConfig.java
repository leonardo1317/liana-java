/**
 * Copyright 2025 Leonardo Favio Romero Silva
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */
package io.github.liana.config;

/**
 * Provides access to the singleton {@link ConfigManager} used to manage configuration loading.
 * <p>
 * This class acts as a static entry point for retrieving the default configuration manager
 * instance, typically used throughout the application to load and access configuration values.
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigManager config = LianaConfig.getInstance();
 * }</pre>
 *
 * <p>This class cannot be instantiated.
 */
public final class LianaConfig {

    private static final DefaultConfigManager INSTANCE = new DefaultConfigManager();

    private LianaConfig() {
    }

    /**
     * Returns the singleton instance of the {@link ConfigManager}.
     *
     * @return the default configuration manager instance
     */
    public static ConfigManager getInstance() {
        return INSTANCE;
    }
}
