package io.github.liana.config;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.liana.internal.StringUtils;
import java.util.Locale;
import java.util.ServiceLoader;

/**
 * Factory for creating Jackson {@link ObjectMapper} instances.
 *
 * <p>Provides preconfigured mappers for JSON, YAML, XML, and Java Properties.
 * Mappers are discovered via {@link ServiceLoader}. If a required format is not available, an
 * {@link IllegalStateException} is thrown.
 *
 * <p>Each mapper is configured with:
 * <ul>
 *   <li>{@link JavaTimeModule} for Java time types</li>
 *   <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} disabled</li>
 * </ul>
 */
final class JacksonMappers {

  private static final String JSON = "JSON";
  private static final String YAML = "YAML";
  private static final String XML = "XML";
  private static final String PROPERTIES = "java_properties";

  private final ServiceRegistry<JsonFactory, ObjectMapper> registry;

  private JacksonMappers(ServiceRegistry<JsonFactory, ObjectMapper> registry) {
    this.registry = registry;
  }

  /**
   * Creates a new {@code JacksonMappers} instance backed by {@link ServiceLoader}.
   *
   * @return new {@code JacksonMappers} instance
   * @throws IllegalStateException if no mappers can be discovered
   */
  public static JacksonMappers create() {
    ServiceRegistry<JsonFactory, ObjectMapper> registry = new ServiceRegistry<>(
        ServiceLoader.load(JsonFactory.class),
        JacksonMappers::matchesFormat,
        JacksonMappers::configure
    );

    return new JacksonMappers(registry);
  }

  /**
   * Returns {@code true} if the given {@link JsonFactory} supports the requested format.
   *
   * @param jsonFactory candidate factory
   * @param type        format name, e.g. "JSON", "YAML", "XML", or "java_properties"
   * @return {@code true} if the factory supports the format, {@code false} otherwise
   */
  private static boolean matchesFormat(JsonFactory jsonFactory, String type) {
    return !isNull(jsonFactory) && StringUtils.equalsIgnoreCase(jsonFactory.getFormatName(), type);
  }

  /**
   * Returns the JSON {@link ObjectMapper}.
   *
   * @return JSON mapper
   * @throws IllegalStateException if JSON support is unavailable
   */
  public ObjectMapper getJson() {
    return get(JSON);
  }

  /**
   * Returns the YAML {@link ObjectMapper}.
   *
   * @return YAML mapper
   * @throws IllegalStateException if YAML support is unavailable
   */
  public ObjectMapper getYaml() {
    return get(YAML);
  }

  /**
   * Returns the XML {@link ObjectMapper}.
   *
   * @return XML mapper
   * @throws IllegalStateException if XML support is unavailable
   */
  public ObjectMapper getXml() {
    return get(XML);
  }

  /**
   * Returns the Java Properties {@link ObjectMapper}.
   *
   * @return Properties mapper
   * @throws IllegalStateException if Properties support is unavailable
   */
  public ObjectMapper getProperties() {
    return get(PROPERTIES);
  }

  /**
   * Returns the {@link ObjectMapper} for the given format.
   *
   * @param formatName format name, e.g. "JSON", "YAML", "XML", or "java_properties"
   * @return mapper for the given format
   * @throws IllegalStateException if no mapper supports the format
   */
  private ObjectMapper get(String formatName) {
    return registry.get(formatName.toUpperCase(Locale.ROOT))
        .orElseThrow(
            () -> new IllegalStateException("no mapper available for format " + formatName));
  }

  /**
   * Creates and configures an {@link ObjectMapper} for the given {@link JsonFactory}.
   *
   * <p>The mapper registers {@link JavaTimeModule} and disables
   * {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}.
   *
   * @param jsonFactory non-null JSON factory
   * @return configured {@link ObjectMapper}
   */
  private static ObjectMapper configure(JsonFactory jsonFactory) {
    ObjectMapper mapper = new ObjectMapper(jsonFactory);
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }
}
