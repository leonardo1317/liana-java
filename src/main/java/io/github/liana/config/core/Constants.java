package io.github.liana.config.core;

/**
 * Default values and constants used across the configuration system.
 *
 * <p>This class provides standard identifiers such as the default provider name,
 * environment variable names, default profile, and base resource naming conventions.
 *
 * <p>This class is final and cannot be instantiated.
 */
final class Constants {

  public static final String PROVIDER = "classpath";
  public static final String PROFILE_VAR = "profile";
  public static final String DEFAULT_PROFILE = "default";
  public static final String PROFILE_ENV_VAR = "LIANA_PROFILE";
  public static final String BASE_RESOURCE_NAME = "application";
  public static final String BASE_RESOURCE_NAME_PATTERN =
      BASE_RESOURCE_NAME + "-${" + PROFILE_VAR + "}";

  private Constants() {
  }
}
