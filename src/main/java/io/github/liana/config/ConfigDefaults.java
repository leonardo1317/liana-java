package io.github.liana.config;

public final class ConfigDefaults {
    public static final String PROVIDER = "classpath";
    public static final String PROFILE_VAR = "profile";
    public static final String DEFAULT_PROFILE = "default";
    public static final String PROFILE_ENV_VAR = "LIANA_PROFILE";
    public static final String BASE_RESOURCE_NAME = "application";
    public static final String BASE_RESOURCE_NAME_PATTERN = BASE_RESOURCE_NAME + "-${" + PROFILE_VAR + "}";

    private ConfigDefaults() {
    }
}
