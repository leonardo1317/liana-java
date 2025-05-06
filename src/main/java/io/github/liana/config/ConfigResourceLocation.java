package io.github.liana.config;

/*
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ConfigResourceLocation {
    private final String provider;
    private final String resourceName;
    private final String resourceNamePattern;
    private final ConfigMap variables;
    private final ConfigMap credentials;

    public ConfigResourceLocation(Builder builder) {
        this.provider = builder.provider;
        this.resourceName = builder.resourceName;
        this.resourceNamePattern = builder.resourceNamePattern;
        this.variables = builder.variables;
        this.credentials = builder.credentials;
    }

    public String getProvider() {
        return provider;
    }

    public ConfigMap getCredentials() {
        return credentials;
    }

    public ConfigMap getVariables() {
        return variables;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceNamePattern() {
        return resourceNamePattern;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ConfigResourceLocation{" +
                "provider='" + provider + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceNamePattern='" + resourceNamePattern + '\'' +
                ", variables=" + variables +
                ", credentials=" + credentials +
                '}';
    }

    public static class Builder {
        private String provider = "";
        private String resourceName = "";
        private String resourceNamePattern = "";
        private ConfigMap variables = ConfigMap.emptyMap();
        private ConfigMap credentials = ConfigMap.emptyMap();

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder resourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public Builder resourceNamePattern(String resourceNamePattern) {
            this.resourceNamePattern = resourceNamePattern;
            return this;
        }

        public Builder variables(String... entries) {
            try {
                this.variables = ConfigMap.of(entries);
            } catch (IllegalArgumentException ex) {
                throw new InvalidConfigVariablesException(ex.getMessage());
            }
            return this;
        }

        public Builder credentials(String... entries) {
            try {
                this.credentials = ConfigMap.of(entries);
            } catch (IllegalArgumentException ex) {
                throw new InvalidConfigCredentialsException(ex.getMessage());
            }
            return this;
        }

        public ConfigResourceLocation build() {
            return new ConfigResourceLocation(this);
        }
    }
}
